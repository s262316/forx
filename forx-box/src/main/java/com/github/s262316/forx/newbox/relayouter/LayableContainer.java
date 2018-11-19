package com.github.s262316.forx.newbox.relayouter;

import com.github.s262316.forx.newbox.Box;

public interface LayableContainer
{
	public LayoutResult calculatePosition(Box member);
	public void uncalculatePosition(Box member);
	public Box getContainer();
}
