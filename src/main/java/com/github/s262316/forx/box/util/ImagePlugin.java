package com.github.s262316.forx.box.util;

import java.awt.Image;
import java.awt.Graphics2D;

import com.github.s262316.forx.box.ReplaceableBoxPlugin;

public class ImagePlugin implements ReplaceableBoxPlugin
{
	private Image image;
	private String location;
	private int _uwidth, _uheight;
	private int x, y;

	public ImagePlugin(String loc)
	{
		location=loc;

		image=loadImage(location);
		_uwidth=image.getWidth(null);
		_uheight=image.getHeight(null);
		x=0;
		y=0;
	}

    @Override
	public Length swidth()
	{
		return new Length(image.getWidth(null));
	}

    @Override
	public Length sheight()
	{
		return new Length(image.getHeight(null));
	}

    @Override
	public double ratio()
	{
		return image.getWidth(null)/image.getHeight(null);
	}

    @Override
	public void use_width(int w)
	{
		_uwidth=w;
	}

    @Override
	public void use_height(int h)
	{
		_uheight=h;
	}

    @Override
	public void set_position(int left, int top)
	{
		x=left;
		y=top;
	}

    @Override
	public void draw(Graphics2D c, int offx, int offy)
	{
		System.out.println("drawing at " + x + "," + y + " - " + offx + "," + offy);
		c.drawImage(image, x-offx, y-offy, _uwidth, _uheight, null);
	}

	private static Image loadImage(String location)
	{
		return null;
	}
}
