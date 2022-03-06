package com.kdy.live.bean.netty.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

@Component
public class NettyMonitorInitializer extends ChannelInitializer<SocketChannel> {
	
	private Logger logger = LoggerFactory.getLogger(NettyMonitorInitializer.class);
	
	@Autowired
	private NettyMonitorHandler handler;
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		logger.debug("NettyMonitorInitializer initChannel()");
		
		ChannelPipeline p = ch.pipeline();
		
		p.addLast(new HttpServerCodec());
		p.addLast(new HttpObjectAggregator(65536));
		
		p.addLast(handler);
		
	}

}
