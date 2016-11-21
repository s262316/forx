package com.github.s262316.forx.box.relayouter;

import com.github.s262316.forx.box.Layable;

public interface LayableContainer
{
	public LayoutResult calculatePosition(Layable member);
	public void uncalculatePosition(Layable member);
	public Layable getContainer();
}
