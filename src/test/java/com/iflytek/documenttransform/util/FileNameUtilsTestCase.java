/**
 * Copyright © 2016 科大讯飞股份有限公司. All rights reserved.
 */
package com.iflytek.documenttransform.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @description：
 *
 * @author suenlai
 * @date 2016年6月14日
 */

public class FileNameUtilsTestCase {
    @Test
    public void getBaseName() {
        String result = FileNameUtils.getBaseName("123123fds/123123fds.txt");
        Assert.assertEquals("123123fds", result);
    }

    @Test
    public void getFileExt() {
        String result = FileNameUtils.getFileExt("123123..fds.txt");
        Assert.assertEquals("txt", result);
    }

    @Test
    public void changeFileExt() {
        String result = FileNameUtils.changeFileExt("123123..fds.txt", "pdf");
        Assert.assertEquals("123123..fds.pdf", result);
    }

}
