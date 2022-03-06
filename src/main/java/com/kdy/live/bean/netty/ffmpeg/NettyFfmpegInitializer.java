package com.kdy.live.bean.netty.ffmpeg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

@Component
public class NettyFfmpegInitializer extends ChannelInitializer<SocketChannel> {
	
	@Autowired
	private NettyFfmpegHandler nettyFfmpegHandler;
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		//p.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        p.addLast(new StringDecoder());
        p.addLast(new StringEncoder());
        p.addLast(new IdleStateHandler(0, 0, 3600));
        p.addLast(nettyFfmpegHandler);

	}
}
