package com.kdy.live.bean.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kdy.live.dto.netty.NettyVO;

import io.netty.channel.ChannelHandler;

public class NettyServerRunnable extends Thread {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	NettyVO vo;
	NettyServer nettyServer;
	ChannelHandler handler;
	
	int port;
	String type;
	
	public NettyServerRunnable(NettyVO vo, ChannelHandler handler, int port, String type) {
		this.vo = vo;
		this.handler = handler;
		this.port = port;
		this.type = type;
		this.nettyServer = (NettyServer)  vo.getApplicationContext().getBean(NettyServer.class);
	}
	
	@Override
	public void run() {
		logger.info("NettyServerRunnable run()");
		nettyServer.service(handler, port, type);
	}
}
