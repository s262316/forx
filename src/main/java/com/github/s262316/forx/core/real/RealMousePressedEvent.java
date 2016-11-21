package com.github.s262316.forx.core.real;

public class RealMousePressedEvent
{
	private int x, y;

	public RealMousePressedEvent(int x, int y)
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
