package com.github.s262316.forx.newbox.relayouter;

import com.github.s262316.forx.newbox.Box;

public class Layouters
{
	public static Relayouter moreWidth(Box subject, Box cause, int amount)
	{
		return new MoreWidthLayouter(subject, cause, amount);
	}

	public static Relayouter moreHeight(Box subject, Box cause, int amount)
	{
		return new MoreHeightLayouter(subject, cause, amount);
	}
}
