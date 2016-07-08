/**
 * Copyright © 2016 科大讯飞股份有限公司. All rights reserved.
 */
package com.iflytek.documenttransform.route;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iflytek.documenttransform.common.JsonResult;
import com.iflytek.documenttransform.config.Constant;
import com.iflytek.documenttransform.tranform.Mov2JpegTransform;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

/**
 * @description：用于生成一张缩略图
 *
 * @author suenlai
 * @date 2016年6月13日
 */
public class Mov2JpegRoute extends JsonRoute {
    protected Object doTransform(HttpRequest request) {
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
        Map<String, List<String>> uriAttributes = queryStringDecoder.parameters();
        if (uriAttributes.isEmpty()) {
            return JsonResult.FailureJsonResult("请带上必要的参数,如url?fileId=($fileId)");
        }

        // check whether fileId is empty
        if (!uriAttributes.containsKey(Constant.FILE_ID)) {
            return JsonResult.FailureJsonResult("未找到参数:文件ID");
        }

        List<String> fileIds = uriAttributes.get(Constant.FILE_ID);

        if (fileIds.isEmpty()) {
            return JsonResult.FailureJsonResult("参数fileId 不能为空");
        }
        if (fileIds.size() > 1) {
            return JsonResult.FailureJsonResult("目前只支持单次单个文件转换");
        }

        final List<String> timestamps = uriAttributes.get(Constant.TIMESTAMP);
        if (timestamps.isEmpty()) {
            return JsonResult
                    .FailureJsonResult("参数timestamp 不能为空,timestamp的格式为   00:00:00   ,分别代表时分秒");
        }
        if (timestamps.size() > 1) {
            return JsonResult.FailureJsonResult("目前只支持单个时间戳的单帧图片提取");
        }
        final String timestamp = timestamps.get(0);


        String movFileId = fileIds.get(0);
        Mov2JpegTransform mov2JpegTransform = new Mov2JpegTransform();
        @SuppressWarnings("serial")
        String jpegFileId = mov2JpegTransform.transform(movFileId, new HashMap<String, Object>() {
            {
                put(Constant.TIMESTAMP, timestamp);
            }
        });


        return JsonResult.SuccessJsonResult(jpegFileId);
    }

}
