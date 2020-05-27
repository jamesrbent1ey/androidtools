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

package app.bentleyis.microkernel;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Registry {
    static Registry s_instance;

    public synchronized static Registry getInstance() {
        if(s_instance == null) {
            s_instance = new Registry();
        }
        return s_instance;
    }

    LinkedHashMap<Class<? extends Component>, LinkedList<? extends Component>> m_registry = new LinkedHashMap<>();

    private Registry() {
        // do nothing
    }

    public synchronized <T extends Component> Collection<? extends Component> getRegisteredComponents(Class<T> type) {
        Collection c = m_registry.get(type);
        if(c == null) {
            return new LinkedList<>();
        }
        return c;
    }

    public synchronized  <T extends Component> T getFirstComponent(Class<T> type) {
        LinkedList<? extends Component> set = m_registry.get(type);
        if(set == null) {
            return null;
        }
        return (T)set.getFirst();
    }

    public synchronized <T extends Component> void register(Class<T> type, T instance) {
        LinkedList<T> set = (LinkedList<T>) m_registry.get(type);
        if(set == null) {
            set = new LinkedList<>();
        }
        if(set.contains(instance)) {
            return;
        }
        set.add(instance);
        m_registry.put(type,set);
    }

    public synchronized <T extends Component> void unregister(Class<T> type, T instance) {
        LinkedList<? extends Component> set = m_registry.get(type);
        if(set == null) {
            return;
        }
        set.remove(instance);
    }
}
