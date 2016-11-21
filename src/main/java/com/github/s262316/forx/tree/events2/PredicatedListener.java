package com.github.s262316.forx.tree.events2;

import com.github.s262316.forx.tree.impl.XmlNode;

import com.google.common.base.Predicate;

public abstract class PredicatedListener<E extends Event> extends Listener<E>
{
	private Predicate<XmlNode> predicate;
	
	public PredicatedListener(Predicate<XmlNode> predicate, PropagationType modelType, XmlNode listenee)
	{
		super(modelType, listenee);

		this.predicate=predicate;
	}

	@Override
	public void handle(E event)
	{
		if(predicate.apply(getListenee()))
			selectedHandle(event);
	}
	
	public abstract void selectedHandle(E event);

}

