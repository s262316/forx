package com.github.s262316.forx.newbox;

import java.util.List;
import java.util.Optional;

import com.github.s262316.forx.newbox.relayouter.LayoutResult;

public class InlineContainerBox implements Box
{
	private List<Line> lines;

	@Override
	public SizeResult computeDimensions()
	{
		return null;
	}

	@Override
	public LayoutResult calculatePosition(Box member)
	{
		return null;
	}

	@Override
	public void uncalculatePosition(Box member)
	{
	}

	@Override
	public int getId()
	{
		return 0;
	}

	@Override
	public Optional<PropertiesEndPoint> propertiesEndpoint()
	{
		return Optional.empty();
	}
}
