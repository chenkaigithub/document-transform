/**
 * Copyright © 2016 科大讯飞股份有限公司. All rights reserved.
 */
package com.iflytek.documenttransform.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @description：
 *
 * @author suenlai
 * @date 2016年6月14日
 */
public class FileNameUtils {
    /**
     * 
     * @Description: 获取文件名中的名称 如/123/213213/abc.txt 返回的是abc
     * @param fileName
     * @return
     */
    public static String getBaseName(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return null;
        }

        return FilenameUtils.getBaseName(fileName);
    }

    /**
     *
     * @Description: 获取文件的后缀名
     * @param fileName
     * @return 不带.字符
     */
    public static String getFileExt(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return null;
        }
        int dotIndex = fileName.lastIndexOf(".");
        return StringUtils.substring(fileName, dotIndex + 1, fileName.length());
    }

    /**
     *
     * @Description: 将fileName中的后缀名换成fileExt指定的后缀名
     * @param fileName 带.带后缀的文件名
     * @param fileExt 不带.的后缀名
     * @return
     */
    public static String changeFileExt(String fileName, String fileExt) {
        if (StringUtils.isEmpty(fileName)) {
            return null;
        }
        int dotIndex = fileName.lastIndexOf(".");
        return StringUtils.substring(fileName, 0, dotIndex) + "." + fileExt;
    }

}
