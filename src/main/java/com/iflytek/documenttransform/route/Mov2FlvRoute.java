/**
 * Copyright © 2016 科大讯飞股份有限公司. All rights reserved.
 */
package com.iflytek.documenttransform.route;

import java.util.List;
import java.util.Map;

import com.iflytek.documenttransform.common.JsonResult;
import com.iflytek.documenttransform.config.Config;
import com.iflytek.documenttransform.config.Constant;
import com.iflytek.documenttransform.tranform.Mov2FlvTransform;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

/**
 * @description：
 *
 * @author suenlai
 * @date 2016年6月13日
 */
public class Mov2FlvRoute extends JsonRoute {

    protected Object doTransform(HttpRequest request, Map<String, Object> parameter) {
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
        if (fileIds.size() != 1) {
            return JsonResult.FailureJsonResult("目前只支持单次单个文件转换");
        }

        String movFileId = fileIds.get(0);
        if (!checkFileSizeLimit(movFileId, Config.VIDEO_FILESIZE_LIMIT)) {
            return JsonResult.FailureJsonResult("单个转换视频大小不能超过" + Config.VIDEO_FILESIZE_LIMIT + "M");
        }

        Mov2FlvTransform mov2FlvTransform = new Mov2FlvTransform();
        String flvFileId = mov2FlvTransform.transform(movFileId, null);


        return JsonResult.SuccessJsonResult(flvFileId);
    }

    /*
     * (non-Javadoc)
     * @see com.iflytek.documenttransform.route.JsonRoute#doTransform(io.netty.handler.codec.http.
     * HttpRequest)
     */
    @Override
    protected Object doTransform(HttpRequest request) {
        return null;
    }

}
