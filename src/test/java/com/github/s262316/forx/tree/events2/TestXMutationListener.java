package com.github.s262316.forx.tree.events2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.EnumSet;

import org.junit.Test;

import com.github.s262316.forx.tree.events2.support.XMutationListenerForTest;
import com.github.s262316.forx.tree.impl.XmlElement;
import com.github.s262316.forx.tree.impl.XmlNode;
import com.github.s262316.forx.tree.style.util.Selectors;

import com.google.common.base.Predicates;

public class TestXMutationListener
{
	@Test
	public void testListenerForAllEvents()
	{
		XmlElement element=new XmlElement("name", null, 0, null);
		
		XMutationListenerForTest listener=new XMutationListenerForTest(Predicates.<XmlNode>alwaysTrue(), PropagationType.ON_TARGET, element, EnumSet.allOf(MutationType.class));

		listener.handle(new XmlMutationEvent(MutationType.ADD, element));
		assertTrue(listener.addedCalled);
		
		listener.handle(new XmlMutationEvent(MutationType.REMOVE, element));
		assertTrue(listener.removedCalled);

		listener.handle(new XmlMutationEvent(MutationType.CONNECT, element));
		assertTrue(listener.connectedCalled);

		listener.handle(new XmlMutationEvent(MutationType.DISCONNECT, element));
		assertTrue(listener.disconnectedCalled);
	}

	@Test
	public void testListenerForAddRemoveEvents()
	{
		XmlElement element=new XmlElement("name", null, 0, null);
		
		XMutationListenerForTest listener=new XMutationListenerForTest(Predicates.<XmlNode>alwaysTrue(), PropagationType.ON_TARGET, element, EnumSet.of(MutationType.ADD, MutationType.REMOVE));

		listener.handle(new XmlMutationEvent(MutationType.ADD, element));
		assertTrue(listener.addedCalled);
		
		listener.handle(new XmlMutationEvent(MutationType.REMOVE, element));
		assertTrue(listener.removedCalled);

		listener.handle(new XmlMutationEvent(MutationType.CONNECT, element));
		assertFalse(listener.connectedCalled);

		listener.handle(new XmlMutationEvent(MutationType.DISCONNECT, element));
		assertFalse(listener.disconnectedCalled);
	}
}


