package com.github.s262316.forx.tree.style.util;

import org.junit.Test;

import com.github.s262316.forx.tree.impl.XmlElement;
import com.github.s262316.forx.tree.style.selectors.PseudoElementType;
import com.github.s262316.forx.tree.style.selectors.Selector;
import static org.junit.Assert.*;

public class TestSelectors
{
	@Test
	public void testCreateSimpleElementNameSelectorMatches()
	{
		Selector s=Selectors.createSimpleElementNameSelector("e1");
		
		boolean b=s.isMatch(new XmlElement("e1", null, 0, null), PseudoElementType.PE_NOT_PSEUDO);
		
		assertEquals(true, b);
	}

	@Test
	public void testCreateSimpleElementNameSelectorDoesntMatch()
	{
		Selector s=Selectors.createSimpleElementNameSelector("e1");
		
		boolean b=s.isMatch(new XmlElement("e2", null, 0, null), PseudoElementType.PE_NOT_PSEUDO);
		
		assertEquals(false, b);
	}
	
	@Test
	public void testCreateAttributeNameSelectorExists()
	{
		Selector s=Selectors.createAttributeNameSelector("attr1");
		
		XmlElement e=new XmlElement("e1", null, 0, null);
		e.setAttr("attr1", "value1");
		
		boolean b=s.isMatch(e, PseudoElementType.PE_NOT_PSEUDO);

		assertEquals(true, b);
	}
	
	@Test
	public void testCreateAttributeNameSelectorNotExists()
	{
		Selector s=Selectors.createAttributeNameSelector("attr1");
		
		XmlElement e=new XmlElement("e1", null, 0, null);
		e.setAttr("attr2", "value1");
		
		boolean b=s.isMatch(e, PseudoElementType.PE_NOT_PSEUDO);

		assertEquals(false, b);
	}
	
	@Test
	public void testTrueSelector()
	{
		Selector s=Selectors.trueSelector();

		XmlElement e=new XmlElement("e1", null, 0, null);
		
		boolean b=s.isMatch(e, PseudoElementType.PE_NOT_PSEUDO);

		assertEquals(true, b);
		
	}
	
	@Test
	public void testFalseSelector()
	{
		Selector s=Selectors.falseSelector();
		
		XmlElement e=new XmlElement("e1", null, 0, null);
		
		boolean b=s.isMatch(e, PseudoElementType.PE_NOT_PSEUDO);

		assertEquals(false, b);		
	}

}
