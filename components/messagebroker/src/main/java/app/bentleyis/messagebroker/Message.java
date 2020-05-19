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

import java.io.Serializable;

/**
 * A Message must have an identifier (that is assumed to be unique for the exchange - but not necessary here)
 * A Message can contain a timestamp - which can be creation or transmission time, your choice, a type -
 * which can be a mime type or anything else used to interpret the payload, and a payload - the data being sent.
 *
 * Abstract - should derive this type to add payload for specific processing.
 */
public abstract class Message implements Serializable {
    String m_id; // message identifier
    long m_timestamp; // message time stamp
    String m_type; // message type or mime type

    public Message(String id) {
        m_id = id;
    }

    public String getId() {
        return m_id;
    }

    public long getTimestamp() {
        return m_timestamp;
    }

    public void setTimestamp(long m_timestamp) {
        this.m_timestamp = m_timestamp;
    }

    public String getType() {
        return m_type;
    }

    public void setType(String m_type) {
        this.m_type = m_type;
    }
}
