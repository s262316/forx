package com.github.s262316.forx.box.relayouter;

import java.util.List;

import com.github.s262316.forx.box.Layable;
import com.github.s262316.forx.box.RootBox;
import com.github.s262316.forx.util.ExtractBoxId;

import com.google.common.collect.Lists;

public class FullLayouter extends AbstractRelayouter
{
	private RootBox root;
	
	public FullLayouter(RootBox root)
	{
		this.root=root;
	}

	@Override
	public List<Layable> getAffected()
	{
		return new LayableTreeTraverser()
			.preOrderTraversal(root)
			.skip(1)
			.toList();
	}

	@Override
	public void preLayout()
	{
		root.set_position(0, 0);
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof FullLayouter))
			return false;

		FullLayouter rhs=(FullLayouter)other;
		ExtractBoxId extractBoxId=new ExtractBoxId();
		
		List<Integer> a=Lists.transform(getAffected(), extractBoxId);
		List<Integer> b=Lists.transform(rhs.getAffected(), extractBoxId);
		
		return a.equals(b);
	}
}

