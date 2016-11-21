package com.github.s262316.forx.box.util;

import java.awt.Color;

public class Border implements Comparable<Border>
{
	public static int BORDER_LEFT=0;
	public static int BORDER_RIGHT=1;
	public static int BORDER_TOP=2;
	public static int BORDER_BOTTOM=3;

	public BorderEdge edge;
	public int width;
	public BorderStyle style;
	public Color colour;

	public Border()
	{}
	
    public Border(BorderEdge edge, int width, BorderStyle style, Color colour)
    {
		this.edge = edge;
		this.width = width;
		this.style = style;
		this.colour = colour;
	}

	@Override
	public int compareTo(Border b)
	{
		return width-b.width;
	}
}
