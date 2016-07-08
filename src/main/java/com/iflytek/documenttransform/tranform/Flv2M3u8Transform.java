/**
 * Copyright © 2016 科大讯飞股份有限公司. All rights reserved.
 */
package com.iflytek.documenttransform.tranform;

import java.util.Map;

/**
 * @description： use commond ffmpeg -i 动画片.flv -c:v libx264 -c:a aac -strict -2 -f hls 动画.m3u8
 * 
 * 
 * @author suenlai
 * @date 2016年6月17日
 */
public class Flv2M3u8Transform implements Transform {



    /*
     * (non-Javadoc)
     * 
     * @see com.iflytek.documenttransform.tranform.Transform#transform(java.lang.String)
     */
    public String transform(String flvFileId, Map<String, Object> parameter) {
        return null;

    }

}
