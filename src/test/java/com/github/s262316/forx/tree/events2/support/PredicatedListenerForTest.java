package com.github.s262316.forx.tree.events2.support;

import com.github.s262316.forx.tree.events2.PredicatedListener;
import com.github.s262316.forx.tree.events2.PropagationType;
import com.github.s262316.forx.tree.impl.XmlNode;

import com.google.common.base.Predicate;

public class PredicatedListenerForTest extends PredicatedListener<EventForTesting>
{
	public boolean selectedCalled=false;
	
	public PredicatedListenerForTest(Predicate<XmlNode> selector, PropagationType modelType, XmlNode listenee)
	{
		super(selector, modelType, listenee);
	}

	@Override
	public void selectedHandle(EventForTesting event)
	{
		selectedCalled=true;
	}

}
