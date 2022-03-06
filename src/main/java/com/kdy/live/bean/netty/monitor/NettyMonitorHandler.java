package com.kdy.live.bean.netty.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kdy.live.bean.montior.MontiorCommandHandler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.CharsetUtil;

@Sharable
@Component
public class NettyMonitorHandler extends SimpleChannelInboundHandler<HttpObject> {
	
	private Logger logger = LoggerFactory.getLogger(NettyMonitorHandler.class);
	
	@Autowired
	private MontiorCommandHandler cmdHandler;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
		logger.debug("NettyMonitorHandler channelRead()");
		FullHttpResponse res = cmdHandler.service(ctx, msg);
		sendResponse(ctx, (FullHttpRequest) msg, res);
	}
	
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
	
	private void sendResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
		
		if (res.status().code() != 200) {
			res.content().writeBytes(Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8));
            HttpUtil.setContentLength(res, res.content().readableBytes());
        }
		
		ChannelFuture future = ctx.channel().writeAndFlush(res);
		future.addListener(ChannelFutureListener.CLOSE);
	}
}
