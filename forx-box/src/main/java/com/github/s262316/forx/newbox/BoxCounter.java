package com.github.s262316.forx.newbox;

public class BoxCounter
{
	private static int count=0;

	public static int next()
	{
		return count++;
	}
	
	public static void reset()
	{
		count=0;
	}
}
