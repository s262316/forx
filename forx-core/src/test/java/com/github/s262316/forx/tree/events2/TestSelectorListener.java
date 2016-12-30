package com.github.s262316.forx.tree.events2;

import org.junit.Test;

import com.github.s262316.forx.tree.events2.support.SelectorListenerForTest;
import com.github.s262316.forx.tree.impl.XmlElement;
import com.github.s262316.forx.tree.style.util.Selectors;

import static org.junit.Assert.*;

public class TestSelectorListener
{
	@Test
	public void testTrue()
	{
		XmlElement element=new XmlElement("name", null, 0, null);
		
		SelectorListenerForTest selectorListener=new SelectorListenerForTest(Selectors.trueSelector(), PropagationType.ON_TARGET, element);
		
		assertTrue(selectorListener.selectedCalled);
	}
	
	@Test
	public void testFalse()
	{
		XmlElement element=new XmlElement("name", null, 0, null);
		
		SelectorListenerForTest selectorListener=new SelectorListenerForTest(Selectors.falseSelector(), PropagationType.ON_TARGET, element);
		
		assertFalse(selectorListener.selectedCalled);
	}
	
	
}


