package com.kdy.live.bean.encoding.command.thumbnail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kdy.live.bean.encoding.command.EncodeCommandFactoryIF;
import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.live.LiveBroadcastVO;

import net.bramp.ffmpeg.builder.FFmpegBuilder;

public class DefaultThumbnail implements EncodeCommandFactoryIF {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public FFmpegBuilder getCommandBuilder(int port, LiveBroadcastVO liveVO, LiveSchedMemoryVO memoryVO)
			throws Exception {
		
		int thumbnailCnt = 1; //자동 추출 썸네일 수
		int encDuration = (int) Math.ceil(liveVO.getLbjDuration());
		int thumbInterval = encDuration / thumbnailCnt;
		
		logger.debug("ThumbnailBuilder => Duration : " + encDuration + ", Thumbnail_cnt : " + thumbnailCnt + ", Thumbnail_interval : " + thumbInterval);
		FFmpegBuilder builder = new FFmpegBuilder()
								.overrideOutputFiles(true)
								.addExtraArgs("-loglevel", "warning")
								.addExtraArgs("-stats")
								.setInput(liveVO.getVodSavePath() + liveVO.getLbSeq() + "/" + liveVO.getRecordCopyName() + "high.mp4")
								.addOutput(liveVO.getVodSavePath() + liveVO.getLbSeq() + "/" + liveVO.getRecordCopyName() + "high_thumb_%d.png")
									.setFrames(thumbnailCnt)
									.addExtraArgs("-ss", "0")
									.addExtraArgs("-vf", "select=gt(scene\\,0.1)") //장면감지점수
									.addExtraArgs("-vsync","vfr")
									.addExtraArgs("-vf", "fps=fps=1/" + thumbInterval)
									.addExtraArgs("-q", "2")
									.done();
		
		logger.debug(builder.toString());
		
		return builder;
	}
	

}
