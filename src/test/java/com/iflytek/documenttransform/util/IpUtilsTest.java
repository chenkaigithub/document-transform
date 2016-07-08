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
 * @date 2016年6月21日
 */
public class IpUtilsTest {
    @Test
    public void testInNetSegment() {
        String segementExp = "10.4.0.1~10.4.255.255";
        String remoteIp = "10.4.2.12";
        boolean match = IpUtils.inNetSegment(remoteIp, segementExp);
        Assert.assertEquals(match, true);
    }
}




