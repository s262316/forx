package com.github.s262316.forx.newbox;

import java.util.List;
import java.util.Optional;

import com.github.s262316.forx.newbox.relayouter.LayoutResult;
import com.google.common.base.Preconditions;

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
		return Optional.of(this);
	}
}
