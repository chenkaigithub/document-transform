package com.iflytek.documenttransform.tranform;

import java.util.Map;

/**
 * @description：具有转换功能的类
 * @author suenlai
 * @date 2016年6月19日
 */
public interface Transform {
    /***
     * 
     * @Description: 转换声明
     * @param fileId 存储在fastdfs中的fileId(group:fileId)
     * @param parameter 额外需要的转换参数
     * @return 转换后的文档存储在fastdfs中的fileId(group:fileId)
     */
    String transform(String fileId, Map<String, Object> parameter);
}
