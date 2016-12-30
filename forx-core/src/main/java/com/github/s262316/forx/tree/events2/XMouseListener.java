package com.github.s262316.forx.tree.events2;

import java.util.EnumSet;

import com.github.s262316.forx.tree.impl.XmlNode;


public abstract class XMouseListener extends Listener<XmlMouseEvent>
{
	private EnumSet<MouseEventType> listenFor;
	
	public XMouseListener(EnumSet<MouseEventType> listenFor, PropagationType modelType, XmlNode listenee)
	{
		super(modelType, listenee);
		this.listenFor=listenFor;
	}

	@Override
	public void handle(XmlMouseEvent event)
	{
		if(listenFor.contains(event.getEventType()))
		{
			switch(event.getEventType())
			{
			case MOUSE_ENTERED:
				mouseEntered(event);
				break;
			case MOUSE_LEFT:
				mouseLeft(event);
				break;
			case MOUSE_MOVED:
				mouseMoved(event);
				break;
			
			}
		}
	}
	
	public abstract void mouseEntered(XmlMouseEvent event);
	public abstract void mouseLeft(XmlMouseEvent event);
	public abstract void mouseMoved(XmlMouseEvent event);
}
