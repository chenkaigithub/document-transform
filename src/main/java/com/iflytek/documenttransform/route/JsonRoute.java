/**
 * Copyright © 2016 科大讯飞股份有限公司. All rights reserved.
 */
package com.iflytek.documenttransform.route;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.iflytek.documenttransform.config.Constant;
import com.iflytek.documenttransform.store.FastdfsFileServer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

/**
 * @description：
 * 
 * @author suenlai
 * @date 2016年6月17日
 */
public abstract class JsonRoute implements Route {

    /**
     * 
     * @Description: 检查是否符合文件大小
     * @param fileId
     * @param sizeMaxLimit
     * @return 超过大小返回false,否则返回true
     */
    protected boolean checkFileSizeLimit(String fileId, int maxSizeMBLimit) {
        long byteSize = FastdfsFileServer.INSTANCE.fileSize(fileId);
        if ((byteSize / 1024 / 1024) > maxSizeMBLimit) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.iflytek.documenttransform.route.Route#execute(io.netty.channel.ChannelHandlerContext,
     * io.netty.handler.codec.http.HttpRequest, java.util.Map)
     */
    @Override
    public void service(ChannelHandlerContext ctx, HttpRequest request,
            Map<String, Object> attrribute) {
        StringBuilder responseContent = new StringBuilder();

        Object result = doTransform(request);
        System.out.println("output:" + result);
        responseContent.setLength(0);

        // render data to view
        responseContent.append(JSON.toJSONString(result));

        ByteBuf buf = Unpooled.copiedBuffer(responseContent.toString(), CharsetUtil.UTF_8);
        // Build the response object.
        FullHttpResponse response =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, Constant.CONTENT_TYPE_JSON);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());

        // Write the response.
        ctx.channel().writeAndFlush(response);

    }



    protected abstract Object doTransform(HttpRequest request);
}
