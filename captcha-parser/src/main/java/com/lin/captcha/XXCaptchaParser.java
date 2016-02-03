package com.lin.captcha;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class XXCaptchaParser extends AbstractCaptchaParser {
	public final static String DEFAULT_TEMPLATE_DIRECTORY = "/xxTpl";
	public final static String DEFAULT_TEMPLATE_SUFFIX = "JPG";
	
	private String templateDirectory = DEFAULT_TEMPLATE_DIRECTORY;
	private List<CharacterTemplate> templates = new ArrayList<>();
	
	public List<CharacterTemplate> getTemplates() {
		return templates;
	}

	public String getTemplateDirectory() {
		return templateDirectory;
	}

	public void setTemplateDirectory(String templateDirectory) {
		this.templateDirectory = templateDirectory;
	}
	
	public void addCharacterTemplate(CharacterTemplate template){
		this.getTemplates().add(template);
	}
	
	/**
	 * 
	 * desc: 判断是否为验证码的字符rgb 阀值为55
	 * @param rgb
	 * @return
	 */
	protected boolean isCharacterRGB(int rgb){
		Color color = new Color(rgb);
		return (color.getRed() + color.getGreen() + color.getBlue()) <= 55;
	}
	
	/**
	 * 
	 * desc: 获取模板文件
	 * @return
	 * @throws IOException
	 */
	protected File[] getTemplateFiles() throws IOException{
		// 获取模板绝对路径
		URL url = this.getClass().getResource(getTemplateDirectory());
		if(null == url){
			throw new FileNotFoundException("南昌验证码识别模板不存在！");
		}
		
		File dir = new File(url.getPath());
		return dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				String suffix = StringUtils.getFilenameExtension(name);
				return null != suffix && DEFAULT_TEMPLATE_SUFFIX.equalsIgnoreCase(suffix);
			}
		});
	}

	/**
	 * 
	 * desc: 获取验证码模板	
	 * @return
	 * @throws IOException 
	 */
	@Override
	public List<CharacterTemplate> getCharacterTemplates() throws IOException {
		if(CollectionUtils.isEmpty(getTemplates())){
			// 加载验证码模板
			this.doLoadTemplates();
		}
		
		return getTemplates();
	}
	
	/**
	 * 
	 * desc: 加载验证码模板
	 * @throws IOException 
	 */
	protected void doLoadTemplates() throws IOException{
		File[] templateFiles = this.getTemplateFiles();
		for(File templateFile : templateFiles){
			BufferedImage templateImage = ImageIO.read(templateFile);
			String fileName = templateFile.getName().split("\\.")[0];
			CharacterTemplate template = new CharacterTemplate(fileName, templateImage);
			this.addCharacterTemplate(template);
		}
	}
	
	/**
	 * 
	 * desc: 切割验证码	
	 * @param image
	 * @return
	 */
	@Override
	public List<BufferedImage> splitImage(BufferedImage image) {
		this.cleanCaptchaImage(image);
		return this.splitByXAxle(this.splitByYAxle(image));
	}
	
	/**
	 * 
	 * desc: 清除验证码干扰线、杂色，并让字符为黑色，其余为白色，不能完全清除，但影响不大
	 * @param image	验证码原图
	 */
	protected void cleanCaptchaImage(BufferedImage image){
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int rgb = image.getRGB(x, y);
				if (isCharacterRGB(rgb)) {
					image.setRGB(x, y, Color.BLACK.getRGB());
				} else {
					image.setRGB(x, y, Color.WHITE.getRGB());
				}
			}
		}
	}
	
	/**
	 * 
	 * desc: 竖切为4份，一个字符一份
	 * @param image	清理后的验证码图片
	 * @return
	 */
	protected List<BufferedImage> splitByYAxle(BufferedImage image){
		List<BufferedImage> images = new ArrayList<>(4);
		
		// 第一个字符
		images.add(image.getSubimage(8, 0, 10, image.getHeight()));
		// 第二个字符
		images.add(image.getSubimage(29, 0, 10, image.getHeight()));
		// 第三个字符
		images.add(image.getSubimage(50, 0, 10, image.getHeight()));
		// 第四个字符
		images.add(image.getSubimage(71, 0, 10, image.getHeight()));
		
		return images;
	}
	
	/**
	 * 
	 * desc: 竖切之后做横切，将上部分空白去除，取固定高度，切取得图片的面积必须与模板面积一致，以便于匹配
	 * @param images
	 * @return
	 */
	protected List<BufferedImage> splitByXAxle(List<BufferedImage> images){
		// 模板图片高度
		final int height = 18;
		List<BufferedImage> resImages = new ArrayList<>(4);
		
		for(BufferedImage image : images){
			for(int y=0; y<image.getHeight(); y++){
				// 获取中间的y轴rgb
				int rgb = image.getRGB(image.getWidth() / 2, y);
				if(isCharacterRGB(rgb)){
					resImages.add(image.getSubimage(0, y, image.getWidth(), height));
					break;
				}
			}
		}
		
		images.clear();
		return resImages;
	}

}





