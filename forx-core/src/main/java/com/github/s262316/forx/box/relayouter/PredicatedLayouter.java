package com.github.s262316.forx.box.relayouter;

import java.util.List;

import com.github.s262316.forx.box.Layable;
import com.github.s262316.forx.box.RootBox;

import com.google.common.base.Predicate;

public class PredicatedLayouter extends AbstractRelayouter
{
	private RootBox root;
	private Predicate<Layable> includeThese;
	
	public PredicatedLayouter(RootBox root, Predicate<Layable> includeThese)
	{
		this.root=root;
		this.includeThese=includeThese;
	}
	
	@Override
	public List<Layable> getAffected()
	{
		return new LayableTreeTraverser()
			.preOrderTraversal(root)
			.filter(includeThese)
			.toList();
	}

	// TODO not sure what to do here.
	@Override
	public boolean equals(Object other)
	{
		return false;
	}
}
