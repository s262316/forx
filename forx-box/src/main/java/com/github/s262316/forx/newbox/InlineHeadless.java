package com.github.s262316.forx.newbox;

import java.util.List;

import com.google.common.base.Preconditions;

public class InlineHeadless implements PropertiesEndPoint
{
	private InterBoxOps interBoxOps;
	private List<Object> inlineMembers;

	@Override
	public void computeProperties()
	{

	}

	public void flow(Box newMember)
	{
		Preconditions.checkArgument(newMember instanceof Text);
		
		inlineMembers.add(newMember);
		interBoxOps.memberWasAdded(newMember);
		interBoxOps.doLoadingLayout(newMember);
	}

	public void flow(InlineHeadless newMember)
	{
		inlineMembers.add(newMember);
		interBoxOps.memberWasAdded(newMember);
		
		newMember.computeProperties();
	}
}

