package com.qylk.base.repsonse;

public abstract class BaseResponse {
    public int resultCode;
    public static final int SUCCESS = 0;
    public static final int TIMEOUT = -1;
    public static final int ERROR = -2;
}

