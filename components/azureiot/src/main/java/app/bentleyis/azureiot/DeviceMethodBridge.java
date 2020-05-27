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

import com.microsoft.azure.sdk.iot.device.DeviceTwin.DeviceMethodCallback;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.DeviceMethodData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import app.bentleyis.microkernel.Registry;

/**
 * The DeviceMethodBridge looks up registered MethodHandlers (registered in the Microkernel Registry)
 * then invokes the method, identified by the Cloud message, with the provided method data as a parameter
 * and returns the result of the execution to the cloud.
 * More than one MethodHandler can be registered. The result of the last successful method invokation is
 * returned.
 */
public class DeviceMethodBridge implements DeviceMethodCallback {
    @Override
    public DeviceMethodData call(String methodName, Object methodData, Object context) {
        DeviceMethodData response = new DeviceMethodData(404,"Not Found");
        Collection<MethodHandler> handlers =
                (Collection<MethodHandler>) Registry.getInstance().getRegisteredComponents(MethodHandler.class);
        if(handlers == null) {
            return response;
        }

        response.setStatus(200);
        for(MethodHandler handler: handlers) {
            try {
                Method m = handler.getClass().getMethod(methodName, methodData.getClass());
                Object ret = m.invoke(handler, methodData);
                if(ret != null) {
                    response.setResponseMessage(ret.toString());
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        //  note that the result from the last successful call is returned.
        return response;
    }

}
