/**
 * Copyright © 2016 科大讯飞股份有限公司. All rights reserved.
 */
package com.iflytek.documenttransform.route;

import java.util.Map;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @description：
 *
 * @author suenlai
 * @date 2016年6月13日
 */
public interface Route {
    void service(ChannelHandlerContext ctx, HttpRequest request,Map<String,Object>attrribute);

}
