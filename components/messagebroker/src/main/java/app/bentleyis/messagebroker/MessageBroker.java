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


import java.util.LinkedHashMap;

/**
 * The MessageBroker represents a simple publish-subscribe broker based on String topics
 * Each topic is associated with an Exchange. The Exchange provides message queuing and delivery.
 */
public class MessageBroker {
    static MessageBroker s_instance;
    // the exchanges are not persistent here (yet)
    LinkedHashMap<String,Exchange> m_exchanges = new LinkedHashMap<>();

    public static synchronized MessageBroker getInstance() {
        if(s_instance == null) {
            s_instance = new MessageBroker();
        }
        return s_instance;
    }

    private MessageBroker() {
        // do nothing
    }

    public void publish(String topic, Message message) {
        Exchange exchange = getExchange(topic, true);
        exchange.enqueue(message);
    }

    public void subscribe(String topic, Subscriber subscriber) {
        Exchange exchange = getExchange(topic, true);
        exchange.register(subscriber);
    }

    public void unsubscribe(String topic, Subscriber subscriber) {
        Exchange exchange = getExchange(topic, false);
        if(exchange == null) {
            return;
        }
        exchange.unregister(subscriber);
    }

    // extracted to reduce synchronization point
    private synchronized Exchange getExchange(String topic, boolean create) {
        Exchange exchange = m_exchanges.get(topic);
        if(!create) {
            return exchange;
        }
        if(exchange == null) {
            exchange = new Exchange();
            m_exchanges.put(topic,exchange);
        }
        return exchange;
    }
}
