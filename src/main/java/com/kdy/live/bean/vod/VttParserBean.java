package com.kdy.live.bean.vod;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.kdy.live.bean.util.FileNameHandler;
import com.kdy.live.dto.live.LiveBroadcastVO;

@Component
public class VttParserBean {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public void makeVttFileByFrame(LiveBroadcastVO vo){
		
		String vttPath = String.format("%1$s%2$s%3$s_preview.vtt", vo.getVodSavePath() + vo.getLbSeq(), File.separator, FileNameHandler.fileNameWithoutFormat(vo.getRecordCopyName() + "high"));
		new File(vttPath).delete();
		
		try {
			
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(vttPath, false), "UTF-8"));
			writer.write(65279);

			String vtt = "WEBVTT \n\n";
			
			String outFile, outFilePath;
			int hour_m, min_m, sec_m;
			String hour_M = "00", min_M = "00", sec_M = "00";
			
			int thumb_cnt = vo.getThumbnailCnt();
			int thumb_interval = Integer.parseInt(vo.getThumbnailTime());
			int tmp_sec_m = 0;
			//String[] preset = vo.getPresetData().split(":");
			//String preset = vo.getPresetData();
			
			List<String> vttInterval = new ArrayList<String>();
			
			for(int i=1; i<thumb_cnt+1; i++) {
				tmp_sec_m = thumb_interval * i;
				
				min_m = tmp_sec_m / 60;
				hour_m = min_m / 60;
				sec_m = tmp_sec_m % 60;
				min_m = min_m % 60;
				
				if(hour_m < 10 ) {
					hour_M = "0" + hour_m;
				} else { 
					hour_M = "" + hour_m;
				};
				if(min_m < 10 ) {
					min_M = "0" + min_m; 
				} else { 
					min_M = "" + min_m; 
				};
				if(sec_m < 10 ) {
					sec_M = "0" + sec_m; 
				} else { 
					sec_M = "" + sec_m; 
				};
				
				//vtt += min_M + ":" + sec_M + ".000 --> "+ min_S + ":" + sec_S + ".000"+" \n" ;
				String tmpInterval = hour_M + ":" + min_M + ":" + sec_M + ".000";
				vttInterval.add(tmpInterval);
			}
			
			for (int i = 0; i < vttInterval.size(); i++) {
				
				logger.info(vttInterval.get(i));
				
				outFile = String.format("%1$s_%2$s_%3$d.%4$s", FileNameHandler.fileNameWithoutFormat(vo.getRecordCopyName() + "high"), "thumb", (i+1), vo.getThumbnailFormat());
				outFilePath = String.format("%1$s%2$s", "/thumb/vod/"+vo.getLbSeq()+"/", outFile);
				
				if(i == 0) {
					vtt += "00:00:00.000 --> " + vttInterval.get(i) + " \n";
				} else {
					vtt += vttInterval.get(i-1) + " --> " + vttInterval.get(i) + " \n";
				}
				vtt += String.format("%1$s#xywh=0,0,%2$s,%3$s \n\n", outFilePath, "1080", "720");
			}
			
			logger.info(vtt);
			writer.write(vtt);
			writer.flush();
			writer.close();

		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} 
	}
	
}
