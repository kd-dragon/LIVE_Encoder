package com.kdy.live.bean.montior;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.kdy.live.bean.status.LiveBroadcastSelectBean;
import com.kdy.live.bean.util.system.ProcessManageFactory;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.live.LiveBroadcastVO;
import com.kdy.live.dto.monitor.MonitorVO;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.CharsetUtil;

@Component
public class MontiorCommandHandler {
	
	private Logger logger = LoggerFactory.getLogger(MontiorCommandHandler.class);
	
	private final LiveBroadcastSelectBean liveSelectBean;
	private final LiveSchedMemoryVO memoryVO;
	private final ProcessManageFactory processManager;
	
	@Autowired
	public MontiorCommandHandler(LiveBroadcastSelectBean liveSelectBean, LiveSchedMemoryVO memoryVO,
					ProcessManageFactory processManager) {
		this.liveSelectBean = liveSelectBean;
		this.memoryVO = memoryVO;
		this.processManager = processManager;
	}
	

	public FullHttpResponse service(ChannelHandlerContext ctx, HttpObject obj) throws Exception {
		
		FullHttpResponse res = null;
		FullHttpRequest req = (FullHttpRequest) obj;
		logger.info("Request URI :: " + req.uri() + "\n" + req.headers().toString());
		String jsonStr = req.content().toString(CharsetUtil.UTF_8);
		Gson gson = new Gson();
		
		MonitorVO mvo = new MonitorVO();
		mvo = gson.fromJson(jsonStr, new MonitorVO().getClass());
		logger.debug(mvo.toString());
		
		ByteBuf rtnbuf = null;
		switch(mvo.getMethod()) {
			case "status": rtnbuf = status(mvo); 
				break;
			case "stop" : rtnbuf = stop(mvo); 
				break;
			case "record_progress" : rtnbuf = record_progress(mvo);
				break;
			default : logger.error("Unregisted Monitor Method"); 
				mvo.setMessage("Unregisted Monitor Method");
				mvo.setResult("FAIL");
				rtnbuf = Unpooled.copiedBuffer(new Gson().toJson(mvo), CharsetUtil.US_ASCII);
			    break;
		}
		
		if (rtnbuf != null) {
			res = new DefaultFullHttpResponse(req.protocolVersion(), OK, rtnbuf);
		    res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
		    HttpUtil.setContentLength(res, rtnbuf.readableBytes());
		} else {
			res = new DefaultFullHttpResponse(req.protocolVersion(), BAD_REQUEST, ctx.alloc().buffer(0));
		}
		
		return res;
	}
	
	private ByteBuf status(MonitorVO mvo) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("[TG_LIVE_SCHED] STATUS Request");
		sb.append(System.lineSeparator());
		
		if(memoryVO.getSystemOnOff()) {
			sb.append("Live Scheduler is Running (System On)");
		} else {
			sb.append("Live Scheduler is Not Running (System Off)");
		}
		
		sb.append(System.lineSeparator());
		sb.append("Current Live ON-Air Count [" + memoryVO.getCurrentLiveBatchCount() + "]");
		sb.append(System.lineSeparator());
		
		List<LiveBroadcastVO> liveInfo = new ArrayList<LiveBroadcastVO>();
		ConcurrentHashMap<String, LiveBroadcastVO> liveBroadCastRepository = memoryVO.getLiveSeqToVO();
		Iterator<String> keys = liveBroadCastRepository.keySet().iterator();
		while(keys.hasNext()) {
			String key = keys.next();
			liveInfo.add(liveBroadCastRepository.get(key));
		}
		logger.info(sb.toString());
		mvo.setMessage(sb.toString());
		mvo.setLiveInfo(liveInfo);
		mvo.setResult("SUCCESS");
		
		return Unpooled.copiedBuffer(new Gson().toJson(mvo) ,CharsetUtil.US_ASCII);
	}
	
	private ByteBuf stop(MonitorVO mvo) throws Exception {
		
		//새로운 배치 요청 받지 않는 설정
		memoryVO.setSystemOnOff(false);
		
		List<LiveBroadcastVO> list = liveSelectBean.onAirChannel();
		
		StringBuilder sb = new StringBuilder();
		sb.append("[TG_LIVE_SCHED] STOP Request");
		sb.append(System.lineSeparator());
		sb.append("Lastest ON-Air List >>> ");
		sb.append(System.lineSeparator());
		
		for(LiveBroadcastVO vo : list) {
			
			String pid = vo.getLbjProcessId();
			if(processManager.template().checkPID(pid)) {
				processManager.template().killProcess(pid);
			}
			sb.append(vo.toString());
			sb.append(System.lineSeparator());
		}
		mvo.setMessage(sb.toString());
		mvo.setResult("SUCCESS");
		logger.info(sb.toString());
		
		return Unpooled.copiedBuffer(new Gson().toJson(mvo), CharsetUtil.US_ASCII);
	}
	
	private ByteBuf record_progress(MonitorVO mvo) throws Exception {
		StringBuilder sb = new StringBuilder();
		LiveBroadcastVO lbvo = null;
		sb.append("[TG_LIVE_SCHED] RECORD_PROGRESS Request");
		
		if(mvo.getLbSeq() == null) {
			mvo.setResult("FAIL");
			sb.append("[ERROR] LiveBroadcastSeq(>lbSeq<) is Null ... ");
			
		} else {
			lbvo = memoryVO.getLiveSeqToVO().get(mvo.getLbSeq());
		
			if(lbvo != null) {
				mvo.setLiveBroadcastVO(lbvo);
				sb.append("(1) Encoder is Running ... ");
				sb.append(System.lineSeparator());
				mvo.setResult("RUNNING");
			} else {
				sb.append("(1) Encoder is not working on that Video");
				sb.append(System.lineSeparator());
				mvo.setResult("NONE");
			}
		}
		
		logger.warn(sb.toString());
		mvo.setMessage(sb.toString());
		
		return Unpooled.copiedBuffer(new Gson().toJson(mvo), CharsetUtil.UTF_8);
	}
}
