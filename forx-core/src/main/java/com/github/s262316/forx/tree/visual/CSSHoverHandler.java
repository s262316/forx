package com.github.s262316.forx.tree.visual;

import java.util.EnumSet;

import com.github.s262316.forx.tree.events2.MouseEventType;
import com.github.s262316.forx.tree.events2.PropagationType;
import com.github.s262316.forx.tree.events2.XMouseListener;
import com.github.s262316.forx.tree.events2.XmlMouseEvent;
import com.github.s262316.forx.tree.impl.XmlNode;

public class CSSHoverHandler extends XMouseListener
{
	public CSSHoverHandler(XmlNode listenee)
	{
		super(EnumSet.of(MouseEventType.MOUSE_ENTERED, MouseEventType.MOUSE_LEFT), PropagationType.BUBBLE, listenee);
	}

	@Override
	public void mouseEntered(XmlMouseEvent event)
	{
		System.out.println("mouseEntered");
	}

	@Override
	public void mouseLeft(XmlMouseEvent event)
	{
		System.out.println("mouseLeft");
	}

	@Override
	public void mouseMoved(XmlMouseEvent event)
	{
	
	}

}
