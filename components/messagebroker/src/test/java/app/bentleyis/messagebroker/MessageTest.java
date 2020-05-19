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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageTest {

    class TestableMessage extends Message {
        public TestableMessage(String id) {
            super(id);
        }
    }

    Message underTest;

    @Before
    public void setUp() throws Exception {
        underTest = new TestableMessage("test");
    }

    @Test
    public void getId() {
        assertEquals("test", underTest.getId());
    }

    @Test
    public void getTimestamp() {
        setTimestamp();
    }

    @Test
    public void setTimestamp() {
        assertEquals(0, underTest.getTimestamp());
        long l = System.currentTimeMillis();
        underTest.setTimestamp(l);
        assertEquals(l, underTest.getTimestamp());
    }

    @Test
    public void getType() {
        setType();
    }

    @Test
    public void setType() {
        assertNull(underTest.getType());
        underTest.setType("test");
        assertEquals("test", underTest.getType());
    }
}