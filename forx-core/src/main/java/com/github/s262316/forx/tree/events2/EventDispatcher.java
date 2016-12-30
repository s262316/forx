package com.github.s262316.forx.tree.events2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.github.s262316.forx.tree.XNodes;
import com.github.s262316.forx.tree.impl.XmlNode;
import com.github.s262316.forx.tree.visual.util.XmlNodeTreeTraverser;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Iterables;

public class EventDispatcher
{
	private HashBasedTable<XmlNode, Class<? extends Event>, List<Listener<? extends Event>>> capturePhaseListeners=HashBasedTable.create();
	private HashBasedTable<XmlNode, Class<? extends Event>, List<Listener<? extends Event>>> bubblePhaseListeners=HashBasedTable.create();
	private HashBasedTable<XmlNode, Class<? extends Event>, List<Listener<? extends Event>>> onTargetPhaseListeners=HashBasedTable.create();
	
	public <E extends Event> void broadcast(XmlNode top, E event)
	{
		Iterator<XmlNode> fit=new XmlNodeTreeTraverser().preOrderTraversal(top).iterator();
		
		while(fit.hasNext())
		{
			XmlNode targetNode=fit.next();
			
			// capture phase
			List<Listener<E>> matchedListeners1=(List)capturePhaseListeners.get(targetNode, event.getClass());
			// bubble phase
			List<Listener<E>> matchedListeners2=(List)bubblePhaseListeners.get(targetNode, event.getClass());
			// on target phase
			List<Listener<E>> matchedListeners3=(List)onTargetPhaseListeners.get(targetNode, event.getClass());
			
			Iterable<Listener<E>> allMatchedListeners=Iterables.concat(
					CollectionUtils.emptyIfNull(matchedListeners1),
					CollectionUtils.emptyIfNull(matchedListeners2),
					CollectionUtils.emptyIfNull(matchedListeners3));

			for(Listener<E> l : allMatchedListeners)
				l.handle(event);
		}
	}
	
	public <E extends Event> void fire(XmlNode target, E event)
	{
		List<XmlNode> path=XNodes.pathToHere(target);

		Map<Class<? extends Event>, List<Listener<? extends Event>>> row1=capturePhaseListeners.row(target);
		Map<XmlNode, List<Listener<? extends Event>>> col1=capturePhaseListeners.column(event.getClass());
		
		// capture phase
		for(XmlNode nodeInPath : path)
		{
			List<Listener<E>> matchedListeners1=(List)capturePhaseListeners.get(nodeInPath, event.getClass());
			if(matchedListeners1!=null)
			{
				for(Listener<E> l : matchedListeners1)
				{
					l.handle(event);
				}
			}
		}
		
		// bubble phase
		for(XmlNode nodeInPath : path)
		{
			List<Listener<E>> matchedListeners2=(List)bubblePhaseListeners.get(nodeInPath, event.getClass());
			if(matchedListeners2!=null)
			{
				for(Listener<E> l : matchedListeners2)
					l.handle(event);
			}
		}

		// on target phase
		List<Listener<E>> matchedListeners3=(List)onTargetPhaseListeners.get(target, event.getClass());
		if(matchedListeners3!=null)
		{
			for(Listener<E> l : matchedListeners3)
				l.handle(event);
		}
	}
	
	public <T extends Event> void addListener(XmlNode listenee, PropagationType propagation, Class<? extends Event> eventType, Listener<? extends Event> listener)
	{
		switch(propagation)
		{
		case BUBBLE:
			addOrPutListAndAdd(bubblePhaseListeners, listenee, propagation, eventType, listener);
			break;
		case CAPTURE:
			addOrPutListAndAdd(capturePhaseListeners, listenee, propagation, eventType, listener);
			break;
		case ON_TARGET:
			addOrPutListAndAdd(onTargetPhaseListeners, listenee, propagation, eventType, listener);
			break;
		default:
			Preconditions.checkState(false);
			break;
		}
	}
	
	private static void addOrPutListAndAdd(
			HashBasedTable<XmlNode, Class<? extends Event>, List<Listener<? extends Event>>> table,
			XmlNode listenee,
			PropagationType propagation,
			Class<? extends Event> eventType,
			Listener<? extends Event> listener)
	{
		if(!table.contains(listenee, eventType))
			table.put(listenee, eventType, new ArrayList<Listener<? extends Event>>());

		List<Listener<? extends Event>> listenerValues=table.get(listenee, eventType);
		
		listenerValues.add(listener);
	}
}


