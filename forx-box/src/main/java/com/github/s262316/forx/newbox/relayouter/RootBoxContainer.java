package com.github.s262316.forx.newbox.relayouter;

import java.util.Optional;

import com.github.s262316.forx.newbox.Box;

public class RootBoxContainer implements LayableContainer
{
	private Box rootBox;
	
	public RootBoxContainer(Box rootBox)
	{
		this.rootBox=rootBox;
	}

	@Override
	public LayoutResult calculatePosition(Box member)
	{
//		rootBox.selfCalculatePosition();
		
		return new LayoutResult(true, Optional.<Relayouter>empty());
	}

	@Override
	public void uncalculatePosition(Box member)
	{
//		rootBox.selfUncalculatePosition();
	}
	
	@Override
	public Box getContainer()
	{
		return rootBox;
	}	
}
