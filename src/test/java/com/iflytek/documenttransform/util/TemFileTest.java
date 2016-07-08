/**
 * Copyright © 2016 科大讯飞股份有限公司. All rights reserved.
 */
package com.iflytek.documenttransform.util;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;

/**
 * @description：
 * 
 * @author suenlai
 * @date 2016年6月18日
 */
public class TemFileTest {
    @Test
    public void testTemp() {
        Properties properties = System.getProperties();
        Set<Object> keys = properties.keySet();
        Iterator<Object> iter = keys.iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            String value = properties.getProperty(key.toString());
            System.out.println(key + "\t====>\t" + value);
        }
    }
    @Test
    public void testFormat() {
        int page = 12;
        System.out.println(page);

        String str = String.format("-%05d.png", page);
        System.out.println(str);
    }
}
