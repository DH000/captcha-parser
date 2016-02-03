package com.lin.captcha;

import java.awt.image.BufferedImage;

public class CharacterTemplate {
	
	private String charcter;
	private BufferedImage image;
	
	public CharacterTemplate() {
		super();
	}

	public CharacterTemplate(String charcter, BufferedImage image) {
		super();
		this.charcter = charcter;
		this.image = image;
	}

	public String getCharcter() {
		return charcter;
	}
	
	public void setCharcter(String charcter) {
		this.charcter = charcter;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
}
