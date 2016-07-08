/**
 * Copyright © 2016 科大讯飞股份有限公司. All rights reserved.
 */
package com.iflytek.documenttransform.handler;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.iflytek.documenttransform.config.Config;
import com.iflytek.documenttransform.route.Flv2JpegRoute;
import com.iflytek.documenttransform.route.ForbiddenRoute;
import com.iflytek.documenttransform.route.Mov2FlvRoute;
import com.iflytek.documenttransform.route.Mov2JpegRoute;
import com.iflytek.documenttransform.route.Mp42FlvRoute;
import com.iflytek.documenttransform.route.Mp42JpegRoute;
import com.iflytek.documenttransform.route.NotFoundRoute;
import com.iflytek.documenttransform.route.Office2PdfRoute;
import com.iflytek.documenttransform.route.Pdf2PngRoute;
import com.iflytek.documenttransform.route.Pdf2SwfRoute;
import com.iflytek.documenttransform.route.ServerFailureRoute;
import com.iflytek.documenttransform.route.WelcomeRoute;
import com.iflytek.documenttransform.util.IpUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @description：
 *
 * @author suenlai
 * @date 2016年6月13日
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerHandler.class);

    /*
     * (non-Javadoc)
     * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty. channel.
     * ChannelHandlerContext, java.lang.Object)
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        messageReceived(ctx, msg);
    }

    public void messageReceived(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        HttpServerHandler.LOGGER.debug("inbound msg type:" + msg.getClass().getName());
        if (msg instanceof HttpRequest) {
            Map<String, Object> attrribute = new HashMap<String, Object>();
            HttpRequest request = (HttpRequest) msg;

            String remoteIp = remoteIp(request, ctx);
            attrribute.put("remoteIp", remoteIp);
            try {
                if (!inWriteIpList(remoteIp)) {
                    new ForbiddenRoute().service(ctx, request, attrribute);
                    return;
                }

                URI uri = new URI(request.uri());
                LOGGER.info("inbound request uri:" + uri.getPath());
                HttpServerHandler.LOGGER.info("request uri==>" + uri.getPath());
                String path = uri.getPath();
                switch (path) {
                    case "/":
                        new WelcomeRoute().service(ctx, request, attrribute);
                        break;
                    case "/transformation/office2pdf":
                        new Office2PdfRoute().service(ctx, request, attrribute);
                        break;
                    case "/transformation/pdf2swf":
                        new Pdf2SwfRoute().service(ctx, request, attrribute);
                        break;
                    case "/transformation/mp42flv":
                        new Mp42FlvRoute().service(ctx, request, attrribute);
                        break;
                    case "/transformation/mov2flv":
                        new Mov2FlvRoute().service(ctx, request, attrribute);
                        break;
                    case "/transformation/snap4flv":
                        new Flv2JpegRoute().service(ctx, request, attrribute);
                        break;
                    case "/transformation/snap4mov":
                        new Mov2JpegRoute().service(ctx, request, attrribute);
                        break;
                    case "/transformation/snap4mp4":
                        new Mp42JpegRoute().service(ctx, request, attrribute);
                        break;
                    case "/transformation/snap4pdf":
                        new Pdf2PngRoute().service(ctx, request, attrribute);
                        break;
                    default:
                        new NotFoundRoute().service(ctx, request, attrribute);

                }
            } catch (Exception e) {
                attrribute.put("exception", e);
                new ServerFailureRoute().service(ctx, request, attrribute);
            } finally {
            }
        } else {
            LOGGER.warn("unsupported protocol inbound msg:" + JSONObject.toJSONString(msg));
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        HttpServerHandler.LOGGER.error("服务端出错", cause);
        ctx.channel().close();
    }

    public String remoteIp(HttpRequest httpRequest, ChannelHandlerContext ctx) {
        String clientIP = httpRequest.headers().get("X-Forwarded-For");
        if (clientIP == null) {
            InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
            clientIP = insocket.getAddress().getHostAddress();
        }
        return clientIP;
    }

    private boolean inWriteIpList(String remoteIp) {
        for (String ipItem : Config.WRITE_IP_LIST) {
            if (ipItem.contains("~")) {// 网段
                if (IpUtils.inNetSegment(remoteIp, ipItem)) {
                    return true;
                }

            } else {// 单个IP
                if (StringUtils.equals(ipItem, remoteIp)) {
                    return true;
                }
            }
        }
        return false;
    }

}
