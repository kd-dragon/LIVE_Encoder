package com.kdy.app.bean.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResizeImage {
	
	private static Logger logger = LoggerFactory.getLogger(ResizeImage.class);
	
	/**
	 * 이미지 크기 조정
	 * @param originalFileName
	 * @param saveFileName
	 * @param width
	 * @param height
	 * @throws IOException
	 */
	public static void resizePngImages(File originalFile, File encodingFile, int width, int height)
			throws IOException {
		
		StringBuilder logMsg = new StringBuilder();
		
		BufferedImage originalImage = ImageIO.read(originalFile);
		int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

		int srcWidth = originalImage.getWidth();
		int srcHeight = originalImage.getHeight();
		
		height = (width * srcHeight) / srcWidth;
		
		logMsg.append("[Thumbnail >> Resize Image to PNG]");
		logMsg.append("input file path : " + originalFile.getAbsolutePath());
		logMsg.append("resolution : " + width + "x" + height);
		logger.info(logMsg.toString());

		BufferedImage resizedImage = new BufferedImage(width, height, type);
		
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, width, height, null);
		g.dispose();
		g.setComposite(AlphaComposite.Src);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		ImageIO.write(resizedImage, "png", encodingFile);
	}
}
