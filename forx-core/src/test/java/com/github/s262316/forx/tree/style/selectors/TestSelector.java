package com.github.s262316.forx.tree.style.selectors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.github.s262316.forx.tree.visual.PseudoElement;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.s262316.forx.tree.NodeType;
import com.github.s262316.forx.tree.impl.XmlDocument;
import com.github.s262316.forx.tree.impl.XmlElement;
import com.github.s262316.forx.tree.style.selectors.Operator;
import com.github.s262316.forx.tree.style.selectors.PseudoElementType;
import com.github.s262316.forx.tree.style.selectors.Selector;
import com.github.s262316.forx.tree.style.selectors.SelectorElement;
import com.github.s262316.forx.tree.style.selectors.SelectorPart;
import com.github.s262316.forx.tree.style.util.Selectors;

@RunWith(MockitoJUnitRunner.class)
public class TestSelector
{	
	@Mock
	XmlDocument doc;
	@Mock
	XmlElement html;
	@Mock
	XmlElement body;
	@Mock
	XmlElement p;
	@Mock
	XmlElement p2;
	@Mock
	XmlElement p3;
	
	@Before
	public void setup()
	{
		when(html.parentNode()).thenReturn(doc);
		when(body.parentNode()).thenReturn(html);
		when(p.parentNode()).thenReturn(body);
		when(p2.parentNode()).thenReturn(body);
		when(p3.parentNode()).thenReturn(body);

		when(doc.getName()).thenReturn("#document");
		when(html.getName()).thenReturn("html");
		when(body.getName()).thenReturn("body");				
		when(p.getName()).thenReturn("p");		
		when(p2.getName()).thenReturn("p2");		
		when(p3.getName()).thenReturn("p3");		
		
		when(doc.type()).thenReturn(NodeType.X_DOCUMENT);
		when(html.type()).thenReturn(NodeType.X_ELEMENT);
		when(body.type()).thenReturn(NodeType.X_ELEMENT);				
		when(p.type()).thenReturn(NodeType.X_ELEMENT);		
		when(p2.type()).thenReturn(NodeType.X_ELEMENT);		
		when(p3.type()).thenReturn(NodeType.X_ELEMENT);		
		
		when(p2.previousNode()).thenReturn(p);		
		when(p3.previousNode()).thenReturn(p2);		
	}
	
	@Test
	public void testSingleMatch()
	{
		Selector s=Selectors.createSimpleElementNameSelector("body");
		
		boolean result=s.isMatch(body, PseudoElementType.PE_NOT_PSEUDO);
		assertTrue(result);
	}
	
	@Test
	public void testSingleNoMatch()
	{
		Selector s=Selectors.createSimpleElementNameSelector("p");
		
		boolean result=s.isMatch(body, PseudoElementType.PE_NOT_PSEUDO);
		assertFalse(result);
	}
	
	@Test
	public void testDescendent_DirectChildMatch()
	{
		Selector s=createSelector(" ", "body", "p");
		
		boolean result=s.isMatch(p, PseudoElementType.PE_NOT_PSEUDO);
		assertTrue(result);		
	}
	
	@Test
	public void testDescendent_DirectChildNoMatch()
	{
		Selector s=createSelector(" ", "body", "p");
		
		boolean result=s.isMatch(body, PseudoElementType.PE_NOT_PSEUDO);
		assertFalse(result);		
	}
	
	@Test
	public void testDescendent_GParentMatch()
	{
		Selector s=createSelector(" ", "html", "p");
		
		boolean result=s.isMatch(p, PseudoElementType.PE_NOT_PSEUDO);
		assertTrue(result);		
	}
	
	@Test
	public void testDescendent_GParentNoMatch()
	{
		Selector s=createSelector(" ", "html2", "p");
		
		boolean result=s.isMatch(p, PseudoElementType.PE_NOT_PSEUDO);
		assertFalse(result);		
	}
	
	@Test
	public void testDescendent_ManyParts()
	{
		Selector s=createSelector(" ", "html", "body", "p");
		
		boolean result=s.isMatch(p, PseudoElementType.PE_NOT_PSEUDO);
		assertTrue(result);		
	}

	@Test
	public void testDescendentWithPseudoBefore()
	{
		Selector s=createSelectorWithPseudoElement(" ", PseudoElementType.PE_BEFORE, "body", "p");

		boolean result=s.isMatch(p, PseudoElementType.PE_BEFORE);
		assertTrue(result);
	}

	@Test
	public void testDescendentWithPseudoAfter()
	{
		Selector s=createSelectorWithPseudoElement(" ", PseudoElementType.PE_AFTER, "body", "p");

		boolean result=s.isMatch(p, PseudoElementType.PE_AFTER);
		assertTrue(result);
	}

	@Test
	public void testChildSelectorMatch()
	{
		Selector s=createSelector(">", "body", "p");
		
		boolean result=s.isMatch(p, PseudoElementType.PE_NOT_PSEUDO);
		assertTrue(result);		
	}
	
	@Test
	public void testChildSelectorNoMatch()
	{
		Selector s=createSelector(">", "html", "p");
		
		boolean result=s.isMatch(p, PseudoElementType.PE_NOT_PSEUDO);
		assertFalse(result);
	}

	@Test
	public void testChildWithPseudoBefore()
	{
		Selector s=createSelectorWithPseudoElement(">", PseudoElementType.PE_BEFORE, "body", "p");

		boolean result=s.isMatch(p, PseudoElementType.PE_BEFORE);
		assertTrue(result);
	}

	@Test
	public void testChildWithPseudoAfter()
	{
		Selector s=createSelectorWithPseudoElement(">", PseudoElementType.PE_AFTER, "body", "p");

		boolean result=s.isMatch(p, PseudoElementType.PE_AFTER);
		assertTrue(result);
	}

	@Test
	public void testSiblingSelectorMatch2()
	{
		Selector s=createSelector("+", "p", "p2");
		
		boolean result=s.isMatch(p2, PseudoElementType.PE_NOT_PSEUDO);
		assertTrue(result);		
	}	
	
	@Test
	public void testSiblingSelectorMatch3()
	{
		Selector s=createSelector("+", "p", "p2", "p3");
		
		boolean result=s.isMatch(p3, PseudoElementType.PE_NOT_PSEUDO);
		assertTrue(result);		
	}

	@Test
	public void testSiblingWithPseudoBefore()
	{
		Selector s=createSelectorWithPseudoElement("+", PseudoElementType.PE_BEFORE, "p", "p2");

		boolean result=s.isMatch(p2, PseudoElementType.PE_BEFORE);
		assertTrue(result);
	}

	@Test
	public void testSiblingWithPseudoAfter()
	{
		Selector s=createSelectorWithPseudoElement("+", PseudoElementType.PE_AFTER, "p", "p2");

		boolean result=s.isMatch(p2, PseudoElementType.PE_AFTER);
		assertTrue(result);
	}

	public static Selector createSelector(String operator, String... names)
	{
		List<SelectorPart> parts=new ArrayList<>();

		for(String name : names)
			parts.add(new SelectorElement(name));

		for(int i=0; i<parts.size(); i++)
		{
			if(i%2==1)
				parts.add(i, new Operator(operator));
		}

		Selector s=new Selector(parts);

		return s;
	}

	public static Selector createSelectorWithPseudoElement(String operator, PseudoElementType pseudo, String... names)
	{
		List<SelectorPart> parts=new ArrayList<>();
		SelectorElement se=null;

		for(String name : names)
		{
			se=new SelectorElement(name);
			parts.add(se);
		}

		// se is the last one
		se.pseudoElements=Lists.newArrayList(pseudo);

		for(int i=0; i<parts.size(); i++)
		{
			if(i%2==1)
				parts.add(i, new Operator(operator));
		}

		Selector s=new Selector(parts);

		return s;
	}
}


