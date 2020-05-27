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

package app.bentleyis.azureiot;

import java.util.LinkedHashMap;
import java.util.Map;

import app.bentleyis.messagebroker.Message;

/**
 * For Azure, you can have properties and a byte[] payload per message.
 * The id is set in the properties
 */
public class IotMessage extends Message {
    LinkedHashMap<String,String> m_properties = new LinkedHashMap<>();
    private byte[] m_payload;

    public IotMessage(String id) {
        super(id);
        m_properties.put("id",id);
    }

    public synchronized void setProperty(String name, String value) {
        m_properties.put(name,value);
    }

    public Map<String,String> getProperties() {
        return m_properties;
    }

    public void setPayload(byte[] payload) {
        m_payload = payload;
    }

    public byte[] getPayload() {
        return m_payload;
    }
}
