package com.example.evacuateme.Utils;

public class STATUS {
    public static final int Ok = 200;
    public static final int NotFound = 404;
    public static final int Unauthorized = 401;
    public static final int Created = 201;
    public static final int BadRequest = 400;

    public static final int Awaiting = 0;
    public static final int OnTheWay = 1;
    public static final int Performing = 2;
    public static final int Completed = 3;
    public static final int CanceledByWorker = 4;
    public static final int CanceledByClient = 5;
}
