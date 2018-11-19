package com.github.s262316.forx.newbox.relayouter;

import java.util.List;

import com.github.s262316.forx.newbox.Box;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class FullLayouter extends AbstractRelayouter
{
	private Box root;
	
	public FullLayouter(Box root)
	{
		this.root=root;
	}

	@Override
	public List<Box> getAffected()
	{
		return new LayableTreeTraverser()
			.preOrderTraversal(root)
			.skip(1)
			.toList();
	}

	@Override
	public void preLayout()
	{
		root.setPosition(0, 0);
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof FullLayouter))
			return false;

		FullLayouter rhs=(FullLayouter)other;
		Function<Box, Integer> extractBoxId=Box::getId;
		
		List<Integer> a=Lists.transform(getAffected(), extractBoxId);
		List<Integer> b=Lists.transform(rhs.getAffected(), extractBoxId);
		
		return a.equals(b);
	}
}

