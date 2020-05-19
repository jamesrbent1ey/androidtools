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

package app.bentleyis.appcentersub;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.microsoft.appcenter.Flags;

import java.util.LinkedHashMap;
import java.util.Map;

import app.bentleyis.messagebroker.Message;

/**
 * Note that Metric should be Serializable.
 * Metric represents a Metric log that can be delivered to Visual Studio App Center
 * The identifier is used as the event name.
 */
public class Metric extends Message {
    private LinkedHashMap<String,String> m_properties = new LinkedHashMap<>();
    private int m_flags = Flags.NORMAL;

    /**
     * The Metric must have a unique identifier to be used as the event name
     * @param id
     */
    public Metric(String id) {
        super(id);
        setType("application/"+Metric.class.getSimpleName());
    }

    /**
     * Flags correspond to the Flags types in App Center SDK - Critical and Normal
     * @param flags
     */
    public void setFlags(int flags) {
        m_flags = flags;
    }

    public int getFlags() {
        return m_flags;
    }

    /**
     * Properties are name value pairs associated with the event when posted to App Center
     * @param name
     * @param value
     */
    public synchronized void setProperty(@NonNull String name, String value) {
        m_properties.put(name,value);
    }

    public synchronized @Nullable String getProperty(String name) {
        return m_properties.get(name);
    }

    /**
     * Internally, this gets the set of properties for transfer to App Center
     * @return
     */
    Map<String, String> getProperties() {
        return m_properties;
    }
}
