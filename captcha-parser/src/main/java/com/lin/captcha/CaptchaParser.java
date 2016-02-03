package com.lin.captcha;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * desc:   验证码识别
 * @author xuelin
 * @date   Feb 2, 2016
 */
public interface CaptchaParser {

	/**
	 * 
	 * desc: 解析验证码
	 * @param is	验证码输入流
	 * @return		解析过后的验证码
	 * @throws IOException 
	 */
	public String parseCaptcha(InputStream is) throws IOException;
	
	/**
	 * 
	 * desc: 解析验证码
	 * @param srcImage	源验证码
	 * @return			解析过后的验证码
	 */
	public String parseCaptcha(BufferedImage srcImage) throws IOException;
	
}
