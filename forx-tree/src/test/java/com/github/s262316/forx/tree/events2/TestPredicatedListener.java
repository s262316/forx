package com.github.s262316.forx.tree.events2;

import org.junit.Test;

import com.github.s262316.forx.tree.events2.support.PredicatedListenerForTest;
import com.github.s262316.forx.tree.XmlElement;
import com.github.s262316.forx.tree.XmlNode;

import com.google.common.base.Predicates;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestPredicatedListener
{
	@Test
	public void testTrue()
	{
		XmlElement element=new XmlElement("name", null, 0, null);
		
		PredicatedListenerForTest selectorListener=new PredicatedListenerForTest(Predicates.<XmlNode>alwaysTrue(), PropagationType.ON_TARGET, element);
		
		assertTrue(selectorListener.selectedCalled);
	}
	
	@Test
	public void testFalse()
	{
		XmlElement element=new XmlElement("name", null, 0, null);
		
		PredicatedListenerForTest selectorListener=new PredicatedListenerForTest(Predicates.<XmlNode>alwaysFalse(), PropagationType.ON_TARGET, element);
		
		assertFalse(selectorListener.selectedCalled);
	}
	
	
}


