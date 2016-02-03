package com.lin.captcha;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

public class ParserTest {
	
	BufferedImage image = null;
	
	@Before
	public void init() throws IOException {
		File file = new File("imgs//median.jpg");
		image = ImageIO.read(file);
		
		cleanImage();
		
		toGray();
		
		toBinary();
		
		medianFilter();
		
		avgFilter();
	}
	
	@Test
	public void test() {
	
	}
	
	/**
	 * 
	 * desc: 判断是否为验证码的字符rgb 阀值为55
	 * @param rgb
	 * @return
	 */
	protected boolean isCharacterRGB(int rgb){
		Color color = new Color(rgb);
		return (color.getRed() + color.getGreen() + color.getBlue()) <= 70;
	}
	
	protected void cleanImage() throws IOException{
		BufferedImage cleanImg = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int rgb = image.getRGB(x, y);
				if (isCharacterRGB(rgb)) {
					cleanImg.setRGB(x, y, Color.BLACK.getRGB());
				} else {
					cleanImg.setRGB(x, y, Color.WHITE.getRGB());
				}
			}
		}
		
		File file = new File("imgs//clean.jpg");
		ImageIO.write(cleanImg, "JPG", file);
	}
	
	public void toGray() throws IOException {
		BufferedImage grayImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				grayImg.setRGB(x, y, image.getRGB(x, y));
			}
		}
		File file = new File("imgs//gray.jpg");
		if (file.exists()) {
			file.delete();
		}
		ImageIO.write(grayImg, "JPG", file);
	}
	
	public void toBinary() throws IOException {
		BufferedImage binaryImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				binaryImg.setRGB(x, y, image.getRGB(x, y));
			}
		}
		File file = new File("imgs//binary.jpg");
		if (file.exists()) {
			file.delete();
		}
		ImageIO.write(binaryImg, "JPG", file);
	}
	
	/**
	 * 中值滤波3*3
	 * 5为当前(x,y)，取其中值
	 * |- - - 
	 * |1|2|3|
	 * |4|5|6|
	 * |7|8|9|
	 * |- - -
	 * @throws IOException 
	 * 
	 */
	public void medianFilter() throws IOException{
		BufferedImage medianImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		for(int x=0; x<image.getWidth(); x++){
			for(int y=0; y<image.getHeight(); y++){
				if(x>0 && y>0 && x<image.getWidth()-1 && y<image.getHeight()-1){
					// 取临近rgb
					List<Integer> rgbs = new ArrayList<>(9);
					rgbs.add(image.getRGB(x - 1, y - 1));
					rgbs.add(image.getRGB(x, y - 1));
					rgbs.add(image.getRGB(x + 1, y - 1));
					rgbs.add(image.getRGB(x - 1, y));
					rgbs.add(image.getRGB(x, y));
					rgbs.add(image.getRGB(x + 1, y));
					rgbs.add(image.getRGB(x - 1, y + 1));
					rgbs.add(image.getRGB(x, y + 1));
					rgbs.add(image.getRGB(x + 1, y + 1));
					
					int red = getMiddleRed(rgbs);
					int green = getMiddleGreen(rgbs);
					int blue = getMiddleBlue(rgbs);
					
					Color color = new Color(red, green, blue);
					medianImage.setRGB(x, y, color.getRGB());
				}else{
					medianImage.setRGB(x, y, image.getRGB(x, y));
				}
			}
		}
		
		File file = new File("imgs//median.jpg");
		if(file.exists()){
			file.delete();
		}
		ImageIO.write(medianImage, "JPG", file);
	}
	
	/**
	 * 
	 * desc: 均值滤波3*3
	 * @throws IOException
	 */
	public void avgFilter() throws IOException{
		BufferedImage avgImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		for(int x=0; x<image.getWidth(); x++){
			for(int y=0; y<image.getHeight(); y++){
				if(x>0 && y>0 && x<image.getWidth()-1 && y<image.getHeight()-1){
					// 取临近rgb
					List<Integer> rgbs = new ArrayList<>(9);
					rgbs.add(image.getRGB(x - 1, y - 1));
					rgbs.add(image.getRGB(x, y - 1));
					rgbs.add(image.getRGB(x + 1, y - 1));
					rgbs.add(image.getRGB(x - 1, y));
					rgbs.add(image.getRGB(x, y));
					rgbs.add(image.getRGB(x + 1, y));
					rgbs.add(image.getRGB(x - 1, y + 1));
					rgbs.add(image.getRGB(x, y + 1));
					rgbs.add(image.getRGB(x + 1, y + 1));
					
					int red = getAvgRed(rgbs);
					int green = getAvgGreen(rgbs);
					int blue = getAvgBlue(rgbs);
					
					Color color = new Color(red, green, blue);
					avgImage.setRGB(x, y, color.getRGB());
				}else{
					avgImage.setRGB(x, y, image.getRGB(x, y));
				}
			}
		}
		
		File file = new File("imgs//avg.jpg");
		if(file.exists()){
			file.delete();
		}
		ImageIO.write(avgImage, "JPG", file);
	}
	
	/**
	 * 
	 * desc: 红色中值
	 * @param rgbs
	 * @return
	 */
	protected int getMiddleRed(List<Integer> rgbs){
		ColorModel model = ColorModel.getRGBdefault(); 
		List<Integer> redList = new ArrayList<>(rgbs.size());
		for(Integer rgb : rgbs){
			redList.add(model.getRed(rgb.intValue()));
		}
		
		Collections.sort(redList);
		return redList.get(Math.round(redList.size() / 2));
	}
	
	/**
	 * 
	 * desc: 绿色中值
	 * @param rgbs
	 * @return
	 */
	protected int getMiddleGreen(List<Integer> rgbs){
		ColorModel model = ColorModel.getRGBdefault(); 
		List<Integer> redList = new ArrayList<>(rgbs.size());
		for(Integer rgb : rgbs){
			redList.add(model.getGreen(rgb.intValue()));
		}
		
		Collections.sort(redList);
		return redList.get(Math.round(redList.size() / 2));
	}
	
	/**
	 * 
	 * desc: 蓝色中值
	 * @param rgbs
	 * @return
	 */
	protected int getMiddleBlue(List<Integer> rgbs){
		ColorModel model = ColorModel.getRGBdefault(); 
		List<Integer> redList = new ArrayList<>(rgbs.size());
		for(Integer rgb : rgbs){
			redList.add(model.getBlue(rgb.intValue()));
		}
		
		Collections.sort(redList);
		return redList.get(Math.round(redList.size() / 2));
	}
	
	/**
	 * 
	 * desc: 红色均值
	 * @param rgbs
	 * @return
	 */
	protected int getAvgRed(List<Integer> rgbs){
		ColorModel model = ColorModel.getRGBdefault(); 
		int red = 0;
		for(Integer rgb : rgbs){
			red += model.getRed(rgb.intValue());
		}
		
		return red / rgbs.size();
	}
	
	/**
	 * 
	 * desc: 绿色均值
	 * @param rgbs
	 * @return
	 */
	protected int getAvgGreen(List<Integer> rgbs){
		ColorModel model = ColorModel.getRGBdefault(); 
		int red = 0;
		for(Integer rgb : rgbs){
			red += model.getGreen(rgb.intValue());
		}
		
		return red / rgbs.size();
	}
	
	/**
	 * 
	 * desc: 蓝色均值
	 * @param rgbs
	 * @return
	 */
	protected int getAvgBlue(List<Integer> rgbs){
		ColorModel model = ColorModel.getRGBdefault(); 
		int red = 0;
		for(Integer rgb : rgbs){
			red += model.getBlue(rgb.intValue());
		}
		
		return red / rgbs.size();
	}
	
}
