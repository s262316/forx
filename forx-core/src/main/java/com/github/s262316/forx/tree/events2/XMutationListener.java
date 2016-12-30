package com.github.s262316.forx.tree.events2;

import java.util.EnumSet;

import com.github.s262316.forx.tree.impl.XmlNode;

import com.google.common.base.Predicate;

public abstract class XMutationListener extends PredicatedListener<XmlMutationEvent>
{
	private EnumSet<MutationType> listenFor;
	
	public XMutationListener(Predicate<XmlNode> selector, PropagationType modelType, XmlNode listenee, EnumSet<MutationType> listenFor)
	{
		super(selector, modelType, listenee);
		this.listenFor=listenFor;
	}
	
	@Override
	public void selectedHandle(XmlMutationEvent event)
	{
		if(listenFor.contains(event.getType()))
		{
			switch(event.getType())
			{
			case ADD:
				added(event);
				break;
			case REMOVE:
				removed(event);
				break;
			case CONNECT:
				connected(event);
				break;
			case DISCONNECT:
				disconnected(event);
				break;			
			}
		}
	}

	public abstract void disconnected(XmlMutationEvent event);
	public abstract void connected(XmlMutationEvent event);
	public abstract void removed(XmlMutationEvent event);
	public abstract void added(XmlMutationEvent event);
	
	
}
