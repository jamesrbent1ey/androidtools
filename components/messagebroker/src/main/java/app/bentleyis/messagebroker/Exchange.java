/*
 * Copyright (c) 2020  James Bentley
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package app.bentleyis.messagebroker;

import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * The Exchange provides simple Message delivery for a topic.
 * It doesn't evaluate timestamp to apply time-to-live or expiration policy (yet)
 * It doesn't evaluate QoS deliver. Note: since it is not expiring messages, this can exhaust memory if
 *    no subscribers are present.
 * It doesn't persist messages - messages can be lost between application starts.
 * It doesn't evaluate message priority - FIFO processing
 * It doesn't do any message filtering for subscribers - everyone gets a copy
 */
public class Exchange extends Thread {
    private static final long WAIT_TIME = 30000; // 30 seconds to re-attempt delivery
    LinkedHashSet<Subscriber> m_subscribers = new LinkedHashSet<>();
    LinkedList<Message> m_queue = new LinkedList<>();
    Object m_synchLock = new Object();

    public Exchange() {
        this.start();
    }

    @Override
    public void run() {
        Message message = null;
        while(!interrupted()) {
            // on each iteration, wake synchronized threads
            synchronized (m_synchLock) {
                m_synchLock.notifyAll();
            }
            // interrogate the queue for messages to deliver
            synchronized (m_queue) {
                if(m_queue.isEmpty()) {
                    try {
                        m_queue.wait(WAIT_TIME);
                    } catch (InterruptedException e) {
                        // we're done
                        break;
                    }
                }

                // attempt delivery - if there is nothing queued, we timed out. if there are no
                // subscribers, wait until there is one
                if(m_subscribers.isEmpty() || m_queue.isEmpty()) {
                    continue;
                }

                // grab the item to deliver
                message = m_queue.removeFirst();
            }

            // clone to allow changes to subscribers
            LinkedHashSet<Subscriber> clone = new LinkedHashSet<>(m_subscribers);

            // deliver round-robin on this thread - not fastest but least resource intensive
            for(Subscriber subscriber: clone) {
                subscriber.receive(message);
            }
        }
    }

    /**
     * Enqueue a Message for delivery
     * @param message
     */
    public void enqueue(Message message) {
        synchronized (m_queue) {
            // insert message at end - no priority yet
            m_queue.add(message);
            m_queue.notify();
        }
    }

    /**
     * Queue a message and wait until it no longer exists in the queue - i.e. it has been delivered
     * @param message
     */
    public void enqueueSynchronous(Message message) throws InterruptedException
    {
        enqueue(message);
        synchronized (m_synchLock) {
            do {
                m_synchLock.wait();
            } while(m_queue.contains(message));
        }
    }

    public synchronized void register(Subscriber subscriber) {
        if(!m_subscribers.contains(subscriber)) {
            m_subscribers.add(subscriber);
        }
    }

    public synchronized void unregister(Subscriber subscriber) {
        m_subscribers.remove(subscriber);
    }
}
