package com.github.s262316.forx.tree.events2.support;

import java.util.EnumSet;

import com.github.s262316.forx.tree.events2.MutationType;
import com.github.s262316.forx.tree.events2.PropagationType;
import com.github.s262316.forx.tree.events2.XMutationListener;
import com.github.s262316.forx.tree.events2.XmlMutationEvent;
import com.github.s262316.forx.tree.impl.XmlNode;

import com.google.common.base.Predicate;

public class XMutationListenerForTest extends XMutationListener
{
	public boolean addedCalled=false, removedCalled=false;
	public boolean connectedCalled=false, disconnectedCalled=false;
	
	public XMutationListenerForTest(Predicate<XmlNode> selector, PropagationType modelType, XmlNode listenee, EnumSet<MutationType> listenFor)
	{
		super(selector, modelType, listenee, listenFor);
	}

	@Override
	public void disconnected(XmlMutationEvent event)
	{
		disconnectedCalled=true;
	}

	@Override
	public void connected(XmlMutationEvent event)
	{
		connectedCalled=true;		
	}

	@Override
	public void removed(XmlMutationEvent event)
	{
		removedCalled=true;		
	}

	@Override
	public void added(XmlMutationEvent event)
	{
		addedCalled=true;
	}

}
