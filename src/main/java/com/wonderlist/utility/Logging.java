package com.wonderlist.utility;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Logging {
    private static final Logger log = Logger.getLogger(Logging.class.getName());
    public static void log(String message){
        log.log(Level.INFO, message);
    }
    public static void exception(Throwable e){
        log.log(Level.SEVERE, "there is an exception", e);
    }
}
