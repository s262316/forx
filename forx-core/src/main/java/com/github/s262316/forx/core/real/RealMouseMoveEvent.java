package com.github.s262316.forx.core.real;

public class RealMouseMoveEvent
{
	private int x, y;

	public RealMouseMoveEvent(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}
}
