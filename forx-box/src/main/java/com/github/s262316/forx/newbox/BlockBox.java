package com.github.s262316.forx.newbox;

import java.util.List;
import java.util.Optional;

import com.github.s262316.forx.newbox.relayouter.LayoutResult;
import com.github.s262316.forx.newbox.relayouter.Layouters;
import com.google.common.collect.Iterables;

public class BlockBox implements Box, PropertiesEndPoint
{
	private InterBoxOps interBoxOps;
	private List<Box> blockMembers;

	public BlockBox(InterBoxOps interBoxOps)
	{
		this.interBoxOps=interBoxOps;
	}

	@Override
	public void flow(Box newMember)
	{
		blockMembers.add(newMember);
		interBoxOps.memberWasAdded(this);
		newMember.propertiesEndpoint().ifPresent(PropertiesEndPoint::computeProperties);
		interBoxOps.doLoadingLayout(newMember);
	}
	
	@Override
	public void flow(Inline newMember)
	{
		throw new IllegalArgumentException("inline boxes cannot be added directly to a blockbox");
	}

	@Override
	public void computeProperties()
	{

	}

	@Override
	public SizeResult computeDimensions()
	{
		return null;
	}

	public int canIncreaseWidthBy(int delta)
	{
		return 0;
	}
	
	public int canIncreaseHeightBy(int delta)
	{
		return delta;
	}
	
	@Override
	public LayoutResult calculatePosition(Box member)
	{
		int x, y;
		SizeResult size;

		size = member.computeDimensions();
		if (size.getWidth() > width())
		{
			int canIncreaseBy=canIncreaseWidthBy(size.getWidth() - width());
			if(canIncreaseBy > 0)
				return new LayoutResult(false, Optional.of(Layouters.moreWidth(this, member, canIncreaseBy)));
		}
		
		member.setDimensions(size.getWidth(), size.getHeight());

		x=0;
		
		if(blockMembers.isEmpty())
			y=top();
		else
		{
			y=Iterables.getLast(blockMembers).bottom()+1;
		}
	
		if (y+member.height() > bottom())
		{
			int canIncreaseBy=canIncreaseHeightBy(y+member.height() - bottom());
			if(canIncreaseBy > 0)
				return new LayoutResult(false, Optional.of(Layouters.moreHeight(this, member, canIncreaseBy)));
		}

		member.setPosition(x, y);

		return new LayoutResult(true, Optional.empty());
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
		return Optional.of(this);
	}
}
