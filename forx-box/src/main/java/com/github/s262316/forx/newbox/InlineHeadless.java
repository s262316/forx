package com.github.s262316.forx.newbox;

import java.util.List;
import java.util.Optional;

import com.google.common.base.Preconditions;

public class InlineHeadless implements PropertiesEndPoint, Inline
{
	private Visual visual;
	private InterBoxOps interBoxOps;
	private List<Inline> inlineMembers;

	public InlineHeadless(Visual visual, InterBoxOps interBoxOps)
	{
		this.visual=visual;
		this.interBoxOps = interBoxOps;
	}

	@Override
	public void computeProperties()
	{

	}

	@Override
	public void flow(Box newMember)
	{
		throw new IllegalArgumentException("boxes cannot be added directly to a blockbox");
	}

	@Override
	public void flow(Inline newMember)
	{
		inlineMembers.add(newMember);
		interBoxOps.memberWasAdded(newMember);
		newMember.propertiesEndpoint().ifPresent(PropertiesEndPoint::computeProperties);
		newMember.layable().ifPresent(interBoxOps::doLoadingLayout);
	}

	@Override
	public Optional<PropertiesEndPoint> propertiesEndpoint()
	{
		return Optional.of(this);
	}

	@Override
	public Optional<Box> layable()
	{
		return Optional.empty();
	}
}

