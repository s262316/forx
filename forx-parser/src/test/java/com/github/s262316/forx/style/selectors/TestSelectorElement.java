package com.github.s262316.forx.style.selectors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.github.s262316.forx.css.CSSParser;
import com.github.s262316.forx.css.Tokenizer;
import org.junit.Test;
import org.mockito.Mockito;
import com.github.s262316.forx.tree.events2.EventDispatcher;
import com.github.s262316.forx.tree.XmlDocument;
import com.github.s262316.forx.tree.XmlElement;
import com.github.s262316.forx.css.VisualConstants;
import com.github.s262316.forx.css.VisualState;
import org.springframework.test.util.ReflectionTestUtils;

public class TestSelectorElement
{
	@Test
	public void universal()
	{
		XmlElement e=new XmlElement("div", null, 0, null);
		SelectorElement se=parseSimpleSelector("*");
		boolean match=se.isMatch(e, PseudoElementType.PE_NOT_PSEUDO);
		assertTrue(match);
	}
	
	@Test
	public void name()
	{
		boolean match;
		SelectorElement se=parseSimpleSelector("div");
		XmlElement e1=new XmlElement("div", null, 0, null);
		XmlElement e2=new XmlElement("p", null, 0, null);
		
		match=se.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertTrue(match);
		
		match=se.isMatch(e2, PseudoElementType.PE_NOT_PSEUDO);
		assertFalse(match);
	}
	
	@Test
	public void oneMatchingAttr()
	{
		boolean match;
		SelectorElement se=parseSimpleSelector("div[class]");
		XmlElement e1=new XmlElement("div", null, 0, null);
		e1.setAttr("class", "anyvalue");

		match=se.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertTrue(match);
	}

	@Test
	public void twoMatchingAttrs()
	{
		boolean match;
		SelectorElement se=parseSimpleSelector("div[class][id]");
		XmlElement e1=new XmlElement("div", null, 0, null);
		e1.setAttr("class", "anyvalue");
		e1.setAttr("id", "anyvalue");

		match=se.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertTrue(match);
	}
	
	@Test
	public void oneOfTwoMatchingAttrs()
	{
		boolean match;
		SelectorElement se=parseSimpleSelector("div[class][id]");
		XmlElement e1=new XmlElement("div", null, 0, null);
		e1.setAttr("class", "anyvalue");

		match=se.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertFalse(match);
	}

	@Test
	public void attrEqValue()
	{
		boolean match;
		SelectorElement se=parseSimpleSelector("p[id=\"foo\"]");
		XmlElement e1=new XmlElement("p", null, 0, null);
		e1.setAttr("id", "foo");

		match=se.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertTrue(match);
	}
	
	@Test
	public void pseudoLink()
	{
		boolean match;
		SelectorElement se=parseSimpleSelector("div:link");
		XmlElement e1=new XmlElement("div", null, 0, null);
		e1.setProperty(VisualConstants.VISUAL_STATE, new VisualState());
		
    	e1.getProperty(VisualConstants.VISUAL_STATE, VisualState.class).setLink(true);
		match=se.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertTrue(match);
		
    	e1.getProperty(VisualConstants.VISUAL_STATE, VisualState.class).setLink(false);
		match=se.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertFalse(match);
	}

	@Test
	public void pseudoVisited()
	{
		boolean match;
		SelectorElement se=parseSimpleSelector("div:visited");
		XmlElement e1=new XmlElement("div", null, 0, null);
		e1.setProperty(VisualConstants.VISUAL_STATE, new VisualState());
		
    	e1.getProperty(VisualConstants.VISUAL_STATE, VisualState.class).setVisited(true);
		match=se.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertTrue(match);
		
    	e1.getProperty(VisualConstants.VISUAL_STATE, VisualState.class).setVisited(false);
		match=se.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertFalse(match);		
	}
	
	@Test
	public void pseudoHover()
	{
		boolean match;
		SelectorElement se=parseSimpleSelector("div:hover");
		XmlElement e1=new XmlElement("div", null, 0, null);
		e1.setProperty(VisualConstants.VISUAL_STATE, new VisualState());
		
    	e1.getProperty(VisualConstants.VISUAL_STATE, VisualState.class).setHover(true);
		match=se.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertTrue(match);
		
    	e1.getProperty(VisualConstants.VISUAL_STATE, VisualState.class).setHover(false);
		match=se.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertFalse(match);			
	}
	
	@Test
	public void pseudoActive()
	{
		boolean match;
		SelectorElement se=parseSimpleSelector("div:active");
		XmlElement e1=new XmlElement("div", null, 0, null);
		e1.setProperty(VisualConstants.VISUAL_STATE, new VisualState());
		
    	e1.getProperty(VisualConstants.VISUAL_STATE, VisualState.class).setActive(true);
		match=se.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertTrue(match);
		
    	e1.getProperty(VisualConstants.VISUAL_STATE, VisualState.class).setActive(false);
		match=se.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertFalse(match);			
	}
	
	@Test
	public void pseudoFocus()
	{
		boolean match;
		SelectorElement se=parseSimpleSelector("div:focus");
		XmlElement e1=new XmlElement("div", null, 0, null);
		e1.setProperty(VisualConstants.VISUAL_STATE, new VisualState());
		
    	e1.getProperty(VisualConstants.VISUAL_STATE, VisualState.class).setFocus(true);
		match=se.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertTrue(match);
		
    	e1.getProperty(VisualConstants.VISUAL_STATE, VisualState.class).setFocus(false);
		match=se.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertFalse(match);			
	}
	
	@Test
	public void pseudoLang()
	{
		boolean match;
		XmlElement e1=new XmlElement("div", null, 1, null);
		e1.language="en";
		XmlElement e2=new XmlElement("div", null, 1, null);
		e2.language="en-US";
		XmlElement e3=new XmlElement("div", null, 1, null);
		e3.language="en-US-PA";
		XmlElement e4=new XmlElement("div", null, 1, null);
		e4.language="english";
		XmlElement e5=new XmlElement("div", null, 1, null);
		e5.language="US-en";
		XmlElement e6=new XmlElement("div", null, 1, null);
		e6.language="en-USONA";

		SelectorElement se1=parseSimpleSelector("div:lang(en)");
		SelectorElement se2=parseSimpleSelector("div:lang(en-US)");
		
		match=se1.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertTrue(match);	
		match=se1.isMatch(e2, PseudoElementType.PE_NOT_PSEUDO);
		assertTrue(match);	
		match=se1.isMatch(e3, PseudoElementType.PE_NOT_PSEUDO);
		assertTrue(match);	
		match=se1.isMatch(e4, PseudoElementType.PE_NOT_PSEUDO);
		assertFalse(match);	
		match=se1.isMatch(e5, PseudoElementType.PE_NOT_PSEUDO);
		assertFalse(match);	
		match=se1.isMatch(e6, PseudoElementType.PE_NOT_PSEUDO);
		assertTrue(match);	

		match=se2.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertFalse(match);	
		match=se2.isMatch(e2, PseudoElementType.PE_NOT_PSEUDO);
		assertTrue(match);	
		match=se2.isMatch(e3, PseudoElementType.PE_NOT_PSEUDO);
		assertTrue(match);	
		match=se2.isMatch(e4, PseudoElementType.PE_NOT_PSEUDO);
		assertFalse(match);	
		match=se2.isMatch(e5, PseudoElementType.PE_NOT_PSEUDO);
		assertFalse(match);	
		match=se2.isMatch(e6, PseudoElementType.PE_NOT_PSEUDO);
		assertFalse(match);
	}
	
	@Test
	public void pseudo2()
	{
		boolean match;
		SelectorElement se=parseSimpleSelector("div:focus:active");
		XmlElement e1=new XmlElement("div", null, 0, null);
		e1.setProperty(VisualConstants.VISUAL_STATE, new VisualState());
		
    	e1.getProperty(VisualConstants.VISUAL_STATE, VisualState.class).setFocus(true);
    	e1.getProperty(VisualConstants.VISUAL_STATE, VisualState.class).setActive(true);
		match=se.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertTrue(match);
		
    	e1.getProperty(VisualConstants.VISUAL_STATE, VisualState.class).setFocus(false);
		match=se.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertFalse(match);	

    	e1.getProperty(VisualConstants.VISUAL_STATE, VisualState.class).setActive(false);
		match=se.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertFalse(match);	
	}

	@Test
	public void pseudoTypeElementMatchesNonPseudoSelector()
	{
		boolean match;
		SelectorElement se=parseSimpleSelector("div");
		XmlElement e1=new XmlElement("div", null, 0, null);
		e1.setProperty(VisualConstants.VISUAL_STATE, new VisualState());
    	e1.getProperty(VisualConstants.VISUAL_STATE, VisualState.class).setFocus(true);

		match=se.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertTrue(match);	
	}
	
	@Test
	public void idSelector()
	{
		boolean match;
		SelectorElement se=parseSimpleSelector("div#myid");
		XmlElement e1=new XmlElement("div", null, 0, null);
		e1.setAttr("id", "myid");

		match=se.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertTrue(match);
	}
	
	@Test
	public void idSelectorNone()
	{
		boolean match;
		SelectorElement se=parseSimpleSelector("div#myid");
		XmlElement e1=new XmlElement("div", null, 0, null);

		match=se.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertFalse(match);
	}	

	@Test
	public void idSelectorNotMatch()
	{
		boolean match;
		SelectorElement se=parseSimpleSelector("div#myid");
		XmlElement e1=new XmlElement("div", null, 0, null);
		e1.setAttr("id", "myid2");

		match=se.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertFalse(match);
	}
	
	@Test
	public void idSelectorMultiples()
	{
		boolean match;
		SelectorElement se=parseSimpleSelector("div#myid#otherid");
		XmlElement e1=new XmlElement("div", null, 0, null);
		e1.setAttr("id", "myid");

		match=se.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertFalse(match);
	}	

	@Test
	public void pseudoFirstLine()
	{
		boolean match;
		SelectorElement se=parseSimpleSelector("div:first-line");
		XmlElement e1=new XmlElement("div", null, 0, null);

		match=se.isMatch(e1, PseudoElementType.PE_FIRST_LINE);
		assertTrue(match);

		match=se.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertFalse(match);
	}	
    
	@Test
	public void pseudoFirstLetter()
	{
		boolean match;
		SelectorElement se=parseSimpleSelector("div:first-letter");
		XmlElement e1=new XmlElement("div", null, 0, null);

		match=se.isMatch(e1, PseudoElementType.PE_FIRST_LETTER);
		assertTrue(match);
		
		match=se.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertFalse(match);
	}	
	
	@Test
	public void pseudoFirstBefore()
	{
		boolean match;
		SelectorElement se=parseSimpleSelector("div:before");
		XmlElement e1=new XmlElement("div", null, 0, null);

		match=se.isMatch(e1, PseudoElementType.PE_BEFORE);
		assertTrue(match);
		
		match=se.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertFalse(match);
	}	
	
	@Test
	public void pseudoFirstAfter()
	{
		boolean match;
		SelectorElement se=parseSimpleSelector("div:after");
		XmlElement e1=new XmlElement("div", null, 0, null);

		match=se.isMatch(e1, PseudoElementType.PE_AFTER);
		assertTrue(match);
		
		match=se.isMatch(e1, PseudoElementType.PE_NOT_PSEUDO);
		assertFalse(match);
	}		
	
	@Test
	public void pseudoFirstChild()
	{
		boolean match;
		XmlDocument mockDoc=Mockito.mock(XmlDocument.class);
		EventDispatcher mockEventDispatcher=Mockito.mock(EventDispatcher.class);
		SelectorElement se=parseSimpleSelector("div:first-child");
		XmlElement e1=new XmlElement("div", mockDoc, 0, mockEventDispatcher);
		XmlElement e2=new XmlElement("div", mockDoc, 0, mockEventDispatcher);
		XmlElement e3=new XmlElement("div", mockDoc, 0, mockEventDispatcher);
		
		e1.add(e2);
		e1.add(e3);
		
		match=se.isMatch(e2, PseudoElementType.PE_NOT_PSEUDO);
		assertTrue(match);

		match=se.isMatch(e3, PseudoElementType.PE_NOT_PSEUDO);
		assertFalse(match);
	}		
	
	@Test
	public void testToString()
	{
		SelectorElement se=parseSimpleSelector("div[class][er='erer']:first-letter:after");
		System.out.println(se.toString());
	}
	
	static SelectorElement parseSimpleSelector(String selector)
	{
		CSSParser parser=new CSSParser("file:/whatever", null, null);

		Tokenizer tokenizer=(Tokenizer)ReflectionTestUtils.getField(parser, "tok");
		tokenizer.advance();

		SelectorElement se=ReflectionTestUtils.invokeMethod(parser, "parse_simple_selector");
		return se;
	}
}
