/**
 * Copyright © 2016 科大讯飞股份有限公司. All rights reserved.
 */
package com.iflytek.documenttransform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iflytek.documenttransform.config.Config;
import com.iflytek.documenttransform.handler.HttpServerInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @description：
 *
 * @author suenlai
 * @date 2016年6月13日
 */
public class TransformServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransformServer.class);
    private int                 port   = 8080;

    public TransformServer(int port) {
        this.port = port;
    }

    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new HttpServerInitializer());

            Channel ch = b.bind(port).sync().channel();
            TransformServer.LOGGER.info("HTTP Upload Server at port " + port + '.');
            TransformServer.LOGGER
                    .info("Open your browser and navigate to http://localhost:" + port + '/');

            ch.closeFuture().sync();
        } catch (InterruptedException e) {

            TransformServer.LOGGER.error("start failure, plz re-check your config properties", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new TransformServer(Config.BIND_PORT).run();
    }
}
