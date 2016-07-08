/**
 * Copyright © 2016 科大讯飞股份有限公司. All rights reserved.
 */
package com.iflytek.documenttransform.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * @description：
 *
 * @author suenlai
 * @date 2016年6月14日
 */
public class Config {
    public static int         BIND_PORT                        = 8080;
    public static Set<String> WRITE_IP_LIST                    = null;

    public static String      OPENOFFICE_HOST                  = "127.0.0.1";
    public static int         OPENOFFICE_PORT                  = 8100;

    public static String      SWFTOOLS_EXEC_PDF2SWF            = "/usr/local/bin/pdf2swf";
    public static String      SWFTOOLS_EXEC_PDF2PNG            = "/usr/local/bin/pdftopng";
    public static String      SWFTOOLS_EXEC_PDF2JPEG           = "/usr/local/bin/pdfimages";
    public static String      SWFTOOLS_EXEC_PDF2TEXT           = "/usr/local/bin/pdftotext";
    public static String      SWFTOOLS_LANGUAGE_DIR            =
            "/Users/suenlai/Downloads/xpdf-chinese-simplified/";

    public static String      EXEC_FFMPEG                      = "/Users/suenlai/Downloads/ffmpeg";


    public static int         FASTDFS_CONFIG_CONNECT_TIMEOUT   = 30;
    public static int         FASTDFS_CONFIG_NETWORK_TIMEOUT   = 60;
    public static String      FASTDFS_CONFIG_CHARSET           = "utf-8";
    public static int         FASTDFS_CONFIG_TRACKER_HTTP_PORT = 8090;
    public static String      FASTDFS_CONFIG_TRACKER_SERVERS   = "172.16.16.141:22122";
    public static boolean     FASTDFS_CONFIG_ANTISTEAL_TOKEN   = false;
    public static String      FASTDFS_CONFIG_HTTPSECRET_KEY    = null;


    static {
        Properties properties = new Properties();
        InputStream in = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("config.properties");
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);

        }
        if (properties.containsKey("bind_port")) {
            BIND_PORT = Integer.parseInt(properties.getProperty("bind_port", "8080"));
        }
        if (properties.containsKey("openoffice.host")) {
            OPENOFFICE_HOST = properties.getProperty("openoffice.host", "127.0.0.1");
        }
        if (properties.containsKey("openoffice.port")) {
            OPENOFFICE_PORT = Integer.parseInt(properties.getProperty("openoffice.port", "8100"));
        }
        if (properties.containsKey("fastdfs.tracker.servers")) {
            FASTDFS_CONFIG_TRACKER_SERVERS =
                    properties.getProperty("fastdfs.tracker.servers", "127.0.0.1:22122");
        }


        if (properties.containsKey("swftools.exec.pdf2swf")) {
            SWFTOOLS_EXEC_PDF2SWF =
                    properties.getProperty("swftools.exec.pdf2swf", "/usr/local/bin/pdf2swf");
        }
        if (properties.containsKey("swftools.exec.pdf2png")) {
            SWFTOOLS_EXEC_PDF2PNG =
                    properties.getProperty("swftools.exec.pdf2png", "/usr/local/bin/pdftopng");
        }
        if (properties.containsKey("swftools.exec.pdf2text")) {
            SWFTOOLS_EXEC_PDF2TEXT =
                    properties.getProperty("swftools.exec.pdf2text", "/usr/local/bin/pdftotext");
        }
        if (properties.containsKey("swftools.language.dir")) {
            SWFTOOLS_LANGUAGE_DIR = properties.getProperty("swftools.language.dir",
                    "/usr/local/xpdf-chinese-simplified/");
        }
        if (properties.containsKey("exec.ffmpeg")) {
            EXEC_FFMPEG = properties.getProperty("exec.ffmpeg", "/usr/local/bin/ffmpeg");
        }


        if (properties.containsKey("write_ip_list")) {
            WRITE_IP_LIST = new HashSet<String>();
            List<String> list = Arrays.asList(
                    StringUtils.split(properties.getProperty("write_ip_list", "127.0.0.1"), ";"));
            WRITE_IP_LIST.addAll(list);
        }

    }
}
