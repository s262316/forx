package com.github.s262316.forx.box.relayouter;

import com.github.s262316.forx.box.Layable;
import com.github.s262316.forx.box.RootBox;

import com.google.common.base.Optional;

public class RootBoxContainer implements LayableContainer
{
	private RootBox rootBox;
	
	public RootBoxContainer(RootBox rootBox)
	{
		this.rootBox=rootBox;
	}

	@Override
	public LayoutResult calculatePosition(Layable member)
	{
		rootBox.selfCalculatePosition();
		
		return new LayoutResult(true, Optional.<Relayouter>absent());
	}

	@Override
	public void uncalculatePosition(Layable member)
	{
		rootBox.selfUncalculatePosition();
	}
	
	@Override
	public Layable getContainer()
	{
		return rootBox;
	}	
}
