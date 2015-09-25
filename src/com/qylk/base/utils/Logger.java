package com.qylk.base.utils;

import android.util.Log;

import java.util.Hashtable;

public class Logger {
    private static boolean debug = true;
    private final static int logLevel = Log.VERBOSE;
    private static Hashtable<String, Logger> logger = new Hashtable<String, Logger>();

    public static void setDebug(boolean debug) {
        Logger.debug = debug;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static Logger getLogger(String name) {
        Logger classLogger = logger.get(name);
        if (classLogger == null) {
            classLogger = new Logger();
            logger.put(name, classLogger);
        }
        return classLogger;
    }


    public void i(Class<?> clazz, Object str) {
        if (debug) {
            if (logLevel <= Log.INFO) {
                Log.i(clazz.getSimpleName(), str.toString());
            }
        }
    }

    public void s(Object str) {
        if (debug) {
            if (logLevel <= Log.INFO) {
                System.out.println(str.toString());
            }
        }
    }

    public void d(Class<?> clazz, Object str) {
        if (debug) {
            if (logLevel <= Log.DEBUG) {
                Log.d(clazz.getSimpleName(), str.toString());
            }
        }
    }

    public void v(Class<?> clazz, Object str) {
        if (debug) {
            if (logLevel <= Log.VERBOSE) {
                Log.v(clazz.getSimpleName(), str.toString());
            }
        }
    }

    public void w(Class<?> clazz, Object str) {
        if (debug) {
            if (logLevel <= Log.WARN) {
                Log.w(clazz.getSimpleName(), str.toString());
            }
        }
    }

    public void e(Class<?> clazz, Object str) {
        if (debug) {
            if (logLevel <= Log.ERROR) {
                Log.e(clazz.getSimpleName(), str.toString());
            }
        }
    }
}
