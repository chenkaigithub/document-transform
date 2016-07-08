/**
 * Copyright © 2016 科大讯飞股份有限公司. All rights reserved.
 */
package com.iflytek.documenttransform.route;

import java.util.Map;

import com.iflytek.documenttransform.config.Constant;

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
 * @date 2016年6月13日
 */
public class WelcomeRoute implements Route {

    /*
     * (non-Javadoc)
     *
     * @see com.iflytek.filetransform.route.Route#execute()
     */
    public void service(ChannelHandlerContext ctx, HttpRequest request,
            Map<String, Object> attrributes) {
        StringBuilder responseContent = new StringBuilder();
        // print several HTML forms
        // Convert the response content to a ChannelBuffer.
        responseContent.setLength(0);

        // create Pseudo Menu
        responseContent.append("<html>");
        responseContent.append("<head>");
        responseContent.append("<title>Docuemnt-Transform-Server</title>\r\n");
        responseContent.append("</head>\r\n");
        responseContent.append(
                "<body bgcolor=white><style>body{text-align:centre;margin-top: 80px}</style>");
        responseContent.append("<h1>Welcome to Docuemnt-Transform-Service!</h1>");
        responseContent.append("</body>");
        responseContent.append("</html>");

        ByteBuf buf = Unpooled.copiedBuffer(responseContent.toString(), CharsetUtil.UTF_8);
        // Build the response object.
        FullHttpResponse response =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, Constant.CONTENT_TYPE_HTML);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());

        // Write the response.
        ctx.channel().writeAndFlush(response);
    }

}
