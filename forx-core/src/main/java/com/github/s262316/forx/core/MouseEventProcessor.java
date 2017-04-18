package com.github.s262316.forx.core;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.github.s262316.forx.tree.events2.EventDispatcher;
import com.github.s262316.forx.tree.events2.Events;
import com.github.s262316.forx.tree.events2.MouseEventType;
import com.github.s262316.forx.tree.events2.XmlMouseEvent;
import com.github.s262316.forx.tree.XmlNode;
import com.github.s262316.forx.util.Functions2;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class MouseEventProcessor
{
	private EventDispatcher eventDispatcher;
	private Set<XmlNode> mouseOver=new HashSet<>();
	
	public MouseEventProcessor(EventDispatcher eventDispatcher)
	{
		this.eventDispatcher=eventDispatcher;
	}

	public void process(Set<XmlNode> nodesHit, int mouseX, int mouseY)
	{
		processMouseOuts(nodesHit, mouseX, mouseY);
		processMouseIns(nodesHit, mouseX, mouseY);
		processMouseMoves(nodesHit, mouseX, mouseY);
	}
	
	/**
	 * note. this ONLY REMOVES things from the mouseOver set
	 * 
	 * @param nodesHit
	 * @param mouseX
	 * @param mouseY
	 */
	public void processMouseOuts(Set<XmlNode> nodesHit, int mouseX, int mouseY)
	{
		// nodes that are in mouseOver but not nodePath
		SetView<XmlNode> mouseOutNodes=Sets.difference(mouseOver, nodesHit);
		List<LinkedHashSet<XmlNode>> p=Events.findEventPaths(Lists.newArrayList(mouseOutNodes));
		mouseOver.removeAll(mouseOutNodes.immutableCopy());
		
		// fire mouseout events. get bottom of each path
		Iterable<XmlNode> bottomNodes=Iterables.transform(p, Functions2.<XmlNode>last());
		
		for(XmlNode node : bottomNodes)
			eventDispatcher.fire(node, new XmlMouseEvent(MouseEventType.MOUSE_LEFT, mouseX, mouseY));
	}
	
	/**
	 * note. this only ADDS things to mouseOver set
	 * 
	 * @param nodesHit
	 * @param mouseX
	 * @param mouseY
	 */
	public void processMouseIns(Set<XmlNode> nodesHit, int mouseX, int mouseY)
	{
		// nodes that aren't in mouseOver
		SetView<XmlNode> mouseInNodes=Sets.difference(nodesHit, mouseOver);
		List<LinkedHashSet<XmlNode>> p=Events.findEventPaths(Lists.newArrayList(mouseInNodes));
		mouseOver.addAll(mouseInNodes.immutableCopy());
		
		// fire mousein events. get bottom of each path
		Iterable<XmlNode> bottomNodes=Iterables.transform(p, Functions2.<XmlNode>last());
		
		for(XmlNode node : bottomNodes)
			eventDispatcher.fire(node, new XmlMouseEvent(MouseEventType.MOUSE_ENTERED, mouseX, mouseY));
	}
	
	public void processMouseMoves(Set<XmlNode> nodesHit, int mouseX, int mouseY)
	{
		// fire mousemove events
	}

	public void processPress(Set<XmlNode> nodesHit, int x, int y)
	{
	}
}
