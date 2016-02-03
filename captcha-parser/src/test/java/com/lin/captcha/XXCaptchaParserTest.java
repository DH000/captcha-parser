package com.lin.captcha;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import org.junit.Test;

public class XXCaptchaParserTest {
	public BufferedImage getBufferedImage() {
		// 获取验证码
		return null;
	}
	
	@Test
	public void parserTest() throws IOException, InterruptedException {
		for(int i=0; i<50; i++){
			CaptchaParser parser = new XXCaptchaParser();
			BufferedImage image = getBufferedImage();
			String code = parser.parseCaptcha(image);
			File file = new File("result//" + code + ".jpg");
			if(!file.getParentFile().exists()){
				file.mkdirs();
			}
			ImageIO.write(image, "JPG", file);
			Thread.sleep(new Random().nextInt(1000));
		}
	}
}
