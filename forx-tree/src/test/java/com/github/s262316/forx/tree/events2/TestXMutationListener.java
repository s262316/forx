package com.github.s262316.forx.tree.events2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.EnumSet;

import com.github.s262316.forx.tree.XmlElement;
import org.junit.Assert;
import org.junit.Test;

import com.github.s262316.forx.tree.events2.support.XMutationListenerForTest;
import com.github.s262316.forx.tree.XmlNode;

import com.google.common.base.Predicates;

public class TestXMutationListener
{
	@Test
	public void testListenerForAllEvents()
	{
		XmlElement element=new XmlElement("name", null, 0, null);
		
		XMutationListenerForTest listener=new XMutationListenerForTest(Predicates.<XmlNode>alwaysTrue(), PropagationType.ON_TARGET, element, EnumSet.allOf(MutationType.class));

		listener.handle(new XmlMutationEvent(MutationType.ADD, element));
		Assert.assertTrue(listener.addedCalled);
		
		listener.handle(new XmlMutationEvent(MutationType.REMOVE, element));
		Assert.assertTrue(listener.removedCalled);

		listener.handle(new XmlMutationEvent(MutationType.CONNECT, element));
		Assert.assertTrue(listener.connectedCalled);

		listener.handle(new XmlMutationEvent(MutationType.DISCONNECT, element));
		Assert.assertTrue(listener.disconnectedCalled);
	}

	@Test
	public void testListenerForAddRemoveEvents()
	{
		XmlElement element=new XmlElement("name", null, 0, null);
		
		XMutationListenerForTest listener=new XMutationListenerForTest(Predicates.<XmlNode>alwaysTrue(), PropagationType.ON_TARGET, element, EnumSet.of(MutationType.ADD, MutationType.REMOVE));

		listener.handle(new XmlMutationEvent(MutationType.ADD, element));
		Assert.assertTrue(listener.addedCalled);
		
		listener.handle(new XmlMutationEvent(MutationType.REMOVE, element));
		Assert.assertTrue(listener.removedCalled);

		listener.handle(new XmlMutationEvent(MutationType.CONNECT, element));
		Assert.assertFalse(listener.connectedCalled);

		listener.handle(new XmlMutationEvent(MutationType.DISCONNECT, element));
		Assert.assertFalse(listener.disconnectedCalled);
	}
}


