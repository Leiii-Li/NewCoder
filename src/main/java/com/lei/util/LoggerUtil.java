package com.lei.util;


import com.lei.constant.KeyConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by John on 2017/5/22.
 */
public class LoggerUtil {
    private static final Logger logger = LoggerFactory.getLogger(LoggerUtil.class);

    public static void Logger(String msg) {
        if (KeyConstant.isDebug) {
            logger.error(msg);
        }
    }
}
