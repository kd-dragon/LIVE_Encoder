package com.kdy.live.bean.vod;

import java.io.File;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kdy.live.dto.LiveSchedMemoryVO;
import com.kdy.live.dto.vod.VodMetaVO;

import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;

@Component
@RequiredArgsConstructor
public class MetaInfoBean {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private final LiveSchedMemoryVO mvo;
	
	public FFmpegProbeResult getMetaInfo(String path, String name) throws Exception {
		logger.info("VodMetaInfoBean getMetaInfo() - Started");
		return mvo.getFfprobe().probe(path + File.separator + name);
	}
	
	public VodMetaVO parseToMetaVO(FFmpegProbeResult rslt) throws Exception {
		logger.info("VodMetaInfoBean parseToMetaVO() - Started");
		
		VodMetaVO metaVo = new VodMetaVO();
		FFmpegFormat fmt = rslt.format;
		List<FFmpegStream> streams = rslt.getStreams();
		
		for(FFmpegStream ffs : streams) {
			
			//VideoInfo
			if(ffs.codec_type == FFmpegStream.CodecType.VIDEO) {
				metaVo.setMetaWidth(ffs.width);
				metaVo.setMetaHeight(ffs.height);
				metaVo.setMetaFps(ffs.r_frame_rate.doubleValue());
				metaVo.setMetaDisplayRatio(ffs.display_aspect_ratio);
				metaVo.setMetaVideoCodec(ffs.codec_name);
			//AudioInfo
			} else if(ffs.codec_type == FFmpegStream.CodecType.AUDIO) {
				metaVo.setMetaAudioCodec(ffs.codec_name);
				metaVo.setMetaAudioBps(ffs.bit_rate);
				metaVo.setMetaAudioSampleRate(ffs.sample_rate);
				metaVo.setMetaAudioSampleSize(ffs.bits_per_sample);
				metaVo.setMetaAudioChannels(ffs.channels);

			} else {
				logger.error("unrecognized codec_type stream :: " + ffs.codec_type);
			}
			//
		}
		//formatInfo
		metaVo.setMetaDuration(fmt.duration);
		metaVo.setMetaFileSize(fmt.size);
		metaVo.setMetaVideoBps(fmt.bit_rate);
		metaVo.setFormatName(fmt.format_name);
		
		metaVo.setFpResult(rslt);
		
		return metaVo;
	}
	
	public VodMetaVO getParsedMetaInfo(String path, String name) throws Exception {
		logger.info("VodMetaInfoBean getParsedMetaInfo() - Started");
		return parseToMetaVO(mvo.getFfprobe().probe(path + "/" + name));
	}
}
