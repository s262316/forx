package com.github.s262316.forx.core.real;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Stack;

import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.util.AreaType;

public class DrawArea
{
    AreaType type;
    Graphics2D canvas;
    Box area_root;
    Stack<Rectangle> clippers;

	void change_height(int amount)
	{
		if(area_root.container()==null)
		{
			area_root.change_height(amount);
		}
		else
		{

		}
	}

	void change_width(int amount)
	{
		if(area_root.container()==null)
		{
			area_root.change_width(amount);
		}
		else
		{

		}
	}

	public static DrawArea make_drawarea(AreaType at, Graphics2D canvas, Box root)
	{
		DrawArea da=new DrawArea();
		da.type=at;
		da.canvas=canvas;
		da.area_root=root;
		da.clippers=new Stack<Rectangle>();

		return da;
	}
}
