package com.github.s262316.forx.newbox;

import java.util.Optional;

public class Line
{
	private Dimensionable dimensions;

	public Line(int left, int top, int width, int height)
	{
		dimensions=new Dimensionable(left, top, width, height);
	}

	public Optional<Box> lastBox()
	{
		return null;
	}

	public int right()
	{
		return 7;
	}

	public int top()
	{
		return 0;
	}

	public int left()
	{
		return 0;
	}
}
