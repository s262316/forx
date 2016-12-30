package com.github.s262316.forx.tree.events2;

import com.github.s262316.forx.tree.XElement;
import com.github.s262316.forx.tree.impl.XmlNode;
import com.github.s262316.forx.tree.style.selectors.PseudoElementType;
import com.github.s262316.forx.tree.style.selectors.Selector;

import com.google.common.base.Preconditions;

public abstract class SelectorListener<E extends Event> extends Listener<E>
{
	private Selector selector;
	
	public SelectorListener(Selector selector, PropagationType modelType, XmlNode listenee)
	{
		super(modelType, listenee);

		this.selector=selector;
	}

	@Override
	public void handle(E event)
	{
		// TODO selectors not handle text????
		if(getListenee() instanceof XElement)
		{
			if(selector.isMatch((XElement)getListenee(), PseudoElementType.PE_NOT_PSEUDO))
				selectedHandle(event);
		}
		else
			selectedHandle(event);
			
	}
	
	public abstract void selectedHandle(E event);

}

