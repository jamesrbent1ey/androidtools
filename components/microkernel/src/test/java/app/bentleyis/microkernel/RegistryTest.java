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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class RegistryTest {
    interface Marker extends Component {

    }

    @After
    public void tearDown() throws Exception {
        Registry.s_instance = null;
    }

    @Test
    public void getInstance() {
        assertNull(Registry.s_instance);
        Registry registry = Registry.getInstance();
        assertNotNull(Registry.s_instance);
        assertEquals(registry, Registry.getInstance());
    }

    @Test
    public void getRegisteredComponents() {
        Registry registry = Registry.getInstance();
        Collection collection = registry.getRegisteredComponents(Marker.class);
        assertNotNull(collection);
        assertTrue(collection.isEmpty());

        Marker marker = new Marker() {
        };
        Registry.getInstance().register(Marker.class, marker);
        collection = registry.getRegisteredComponents(Marker.class);
        assertNotNull(collection);
        assertFalse(collection.isEmpty());
    }

    @Test
    public void getFirstComponent() {
        Marker marker = new Marker() {
        };
        assertNull(Registry.getInstance().getFirstComponent(Marker.class));
        Registry.getInstance().register(Marker.class,marker);
        assertEquals(marker, Registry.getInstance().getFirstComponent(Marker.class));
    }

    @Test
    public void register() {
        Marker marker = new Marker() {
        };
        Registry.getInstance().register(Marker.class, marker);
        Collection collection = Registry.getInstance().getRegisteredComponents(Marker.class);
        assertNotNull(collection);
        assertFalse(collection.isEmpty());
        int size = collection.size();
        Registry.getInstance().register(Marker.class, marker); // attempt to register again
        collection = Registry.getInstance().getRegisteredComponents(Marker.class);
        assertNotNull(collection);
        assertEquals(size, collection.size());
    }

    @Test
    public void unregister() {
        Marker marker = new Marker() {
        };
        Registry.getInstance().unregister(Marker.class,marker);
        Collection collection = Registry.getInstance().getRegisteredComponents(Marker.class);
        assertNotNull(collection);
        assertTrue(collection.isEmpty());
        Registry.getInstance().register(Marker.class, marker);
        collection = Registry.getInstance().getRegisteredComponents(Marker.class);
        assertNotNull(collection);
        assertFalse(collection.isEmpty());
        Registry.getInstance().unregister(Marker.class, marker);
        collection = Registry.getInstance().getRegisteredComponents(Marker.class);
        assertNotNull(collection);
        assertTrue(collection.isEmpty());
    }
}