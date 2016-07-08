/**
 * Copyright © 2016 科大讯飞股份有限公司. All rights reserved.
 */
package com.iflytek.documenttransform.tranform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;

/**
 * @description：
 *
 * @author suenlai
 * @date 2016年6月14日
 */
public class ProcessStreamPrinter implements Runnable {
    private InputStream is;
    private Logger      logger;
    private String      type;

    public ProcessStreamPrinter(String type, InputStream is, Logger logger) {
        super();
        this.is = is;
        this.logger = logger;
        this.type = type;
    }

    public void asyncPrint() {
        new Thread(this).start();

    }

    public void run() {
        try {

            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                logger.info(type + "=>" + line);
            }
        } catch (IOException ioe) {
            logger.error("获取子进程的" + type + "输出异常", ioe);
        }
    }
}
