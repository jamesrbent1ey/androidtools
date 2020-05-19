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

package app.bentleyis.appinsightssub;

import java.util.LinkedHashMap;
import java.util.Map;

import app.bentleyis.messagebroker.Message;

public class Metric extends Message {
    static LinkedHashMap<String,String> s_defaultProperties = new LinkedHashMap<>();

    LinkedHashMap<String,String> m_properties = new LinkedHashMap<>();
    LinkedHashMap<String,Double> m_metrics = new LinkedHashMap<>();

    public Metric(String id) {
        super(id);
        m_properties.putAll(s_defaultProperties);
    }

    public synchronized static void intitializeDefaultProperties(Map<String,String> defaults) {
        s_defaultProperties.putAll(defaults);
    }

    public synchronized void setProperty(String name, String value) {
        m_properties.put(name, value);
    }

    public Map<String,String> getProperties() {
        return m_properties;
    }

    public synchronized void setMetric(String name, Double value) {
        m_metrics.put(name,value);
    }

    public Map<String,Double> getMetrics() {
        return m_metrics;
    }
}
