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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class MessageBrokerTest {
    class TestableMessage extends Message {
        public TestableMessage(String id) {
            super(id);
        }
    }

    MessageBroker underTest;

    @Before
    public void setUp() throws Exception {
        underTest = MessageBroker.getInstance();
    }

    @After
    public void tearDown() {
        for(Exchange exchange: underTest.m_exchanges.values()) {
            exchange.interrupt();
        }
        MessageBroker.s_instance = null;
    }

    @Test
    public void getInstance() {
        assertNotNull(underTest);
        assertEquals(underTest, MessageBroker.getInstance());
    }

    @Test
    public void publish() {
        Exchange exchange;
        Message msg = new TestableMessage("test");
        assertTrue(underTest.m_exchanges.isEmpty());
        underTest.publish("test", msg);
        assertFalse(underTest.m_exchanges.isEmpty());
        exchange = underTest.m_exchanges.get("test");
        assertNotNull(exchange);
        assertFalse(exchange.m_queue.isEmpty());
    }

    @Test
    public void subscribe() {
        Subscriber subscriber = Mockito.mock(Subscriber.class);

        assertTrue(underTest.m_exchanges.isEmpty());
        underTest.subscribe("test", subscriber);
        assertFalse(underTest.m_exchanges.isEmpty());
        Exchange exchange = underTest.m_exchanges.get("test");
        assertFalse(exchange.m_subscribers.isEmpty());

        Message msg = new TestableMessage("test");
        underTest.publish("test", msg);
        synchronized (this) {
            try {
                wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Mockito.verify(subscriber,Mockito.atLeastOnce()).receive(msg);
    }

    @Test
    public void unsubscribe() {
        Subscriber subscriber = Mockito.mock(Subscriber.class);

        assertTrue(underTest.m_exchanges.isEmpty());
        underTest.unsubscribe("test", subscriber);
        assertTrue(underTest.m_exchanges.isEmpty());
        underTest.subscribe("test", subscriber);
        assertFalse(underTest.m_exchanges.isEmpty());
        Exchange exchange = underTest.m_exchanges.get("test");
        assertFalse(exchange.m_subscribers.isEmpty());

        underTest.unsubscribe("test", subscriber);
        assertTrue(exchange.m_subscribers.isEmpty());
    }
}