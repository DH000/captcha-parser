package com.lin.captcha;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.util.Assert;

public abstract class AbstractCaptchaParser implements CaptchaParser {
	
	/**
	 * 
	 * desc: 获取验证码匹配模板
	 * @return
	 * @throws IOException 
	 */
	public abstract List<CharacterTemplate> getCharacterTemplates() throws IOException;

	/**
	 * 
	 * desc: 讲验证码图片分割成N个字符图片
	 * 
	 * @param image
	 *            已清理过干扰元素的验证码图片
	 * @return N个字符图片
	 */
	public abstract List<BufferedImage> splitImage(BufferedImage image);
	
	/**
	 * 
	 * desc: 判断是否为白色
	 * @param rgb	rgb色值
	 * @return
	 */
	public boolean isWhite(int rgb){
		return Color.WHITE.getRGB() == rgb;
	}
	
	/**
	 * 
	 * desc: 判断是否为黑色
	 * @param rgb	rgb色值
	 * @return
	 */
	public boolean isBlack(int rgb){
		return Color.BLACK.getRGB() == rgb;
	}
	
	/**
	 * 
	 * desc: 解析验证码
	 * 
	 * @param is
	 *            验证码输入流
	 * @return 解析过后的验证码
	 * @throws IOException
	 */
	@Override
	public String parseCaptcha(InputStream is) throws IOException {
		return this.parseCaptcha(ImageIO.read(is));
	}
	
	/**
	 * 
	 * desc: 解析验证码
	 * 
	 * @param srcImage
	 *            源验证码
	 * @return 解析过后的验证码
	 */
	@Override
	public String parseCaptcha(BufferedImage srcImage) throws IOException{
		List<BufferedImage> characters = this.splitImage(srcImage);
		StringBuilder captchaBuf = new StringBuilder();
		for(BufferedImage character : characters){
			captchaBuf.append(this.characterMatcher(character));
		}
		
		return captchaBuf.toString();
	}
	
	/**
	 * 
	 * desc: 通过计算字符的匹配值决定验证码的命中字符
	 * @param characterImage	单个字符
	 * @return
	 * @throws IOException 
	 */
	public String characterMatcher(BufferedImage characterImage) throws IOException {
		List<CharacterTemplate> templates = getCharacterTemplates();
		Assert.notEmpty(templates, "验证码模板未加载！");
		Map<String, Double> matchMap = new HashMap<>(templates.size());
		
		// 计算匹配值
		for(CharacterTemplate template : templates){
			BufferedImage templateImage = template.getImage();
			int matchCount = 0;
			for(int x=0; x<templateImage.getWidth(); x++){
				for(int y=0; y<templateImage.getHeight(); y++){
					if(templateImage.getRGB(x, y) == characterImage.getRGB(x, y)){
						matchCount++;
					}
				}
			}
			// 保存匹配百分比
			matchMap.put(template.getCharcter(), matchCount / (templateImage.getWidth() * templateImage.getHeight() * 1.0));
		}
		
		// 获取百分比最高的字符
		double maxMatchVal = 0;
		String hitCharacter = "";
		for(String character : matchMap.keySet()){
			double matchVal = matchMap.get(character);
			if(matchVal > maxMatchVal){
				maxMatchVal = matchVal;
				hitCharacter = character;
			}
		}
		
		return hitCharacter;
	}
}
