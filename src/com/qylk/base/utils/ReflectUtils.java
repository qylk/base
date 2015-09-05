package com.qylk.mp.bus.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectUtils {

    public static Object reflectFiled(String className, String filedName) {
        Object result = null;
        try {
            result = Class.forName(className).getField(filedName).get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Method reflectMethod(String className, String methodName, Class[] paramClasses) throws ClassNotFoundException, NoSuchMethodException {
        return Class.forName(className).getMethod(methodName, paramClasses);
    }

    public static Method reflactMethodNoException(String className, String methodName, Class[] paramClasses) {
        Method method = null;
        try {
            method = reflectMethod(className, methodName, paramClasses);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return method;
    }

    public static Object reflectInvoke(Object obj, Method method, Object[] params) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(obj, params);
    }

    public static Object reflectInvoke(Object obj, String methodName, Class[] paramClasses, Object[] params) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Method method = obj.getClass().getMethod(methodName, paramClasses);
        return method.invoke(obj, params);
    }

    public static Object reflectInvokeNoException(Object obj, String methodName, Class[] paramClasses, Object[] params) {
        Object result = null;
        try {
            result = reflectInvoke(obj, methodName, paramClasses, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
