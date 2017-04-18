package com.github.s262316.forx.tree.events2;

import com.github.s262316.forx.tree.XmlNode;

public abstract class Listener<E extends Event>
{
	private PropagationType modelType;
	private XmlNode listenee;

	public Listener(PropagationType modelType, XmlNode listenee)
	{
		this.modelType = modelType;
		this.listenee = listenee;
	}

	public PropagationType getModelType()
	{
		return modelType;
	}

	public XmlNode getListenee()
	{
		return listenee;
	}

	public abstract void handle(E event);
	
}
