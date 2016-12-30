package com.github.s262316.forx.tree.style.selectors;

import org.junit.Before;
import org.junit.Test;

import com.github.s262316.forx.tree.impl.XmlElement;
import com.github.s262316.forx.tree.style.selectors.SelectorAttr;
import static org.junit.Assert.*;

public class TestSelectorAttr
{
	XmlElement element1;
	XmlElement element2;
	
	@Before
	public void setup()
	{
		element1=new XmlElement("div", null, 1, null);
		element1.setAttr("attr1", "value1");

		element2=new XmlElement("div", null, 1, null);
		element2.setAttr("attr2", "value2");
	}
	
	@Test
	public void testMatch1()
	{
		SelectorAttr sa=new SelectorAttr("attr1", "=", "value1");
		assertEquals(true, sa.apply(element1));
		assertEquals(false, sa.apply(element2));
	}
	
	@Test
	public void testMatch2()
	{
		SelectorAttr sa=new SelectorAttr("attr1", "", "");
		assertEquals(true, sa.apply(element1));
		assertEquals(false, sa.apply(element2));
	}
	
	/*
	 * TODO remove this
		.one[lang |= "en"] { color: purple; }
		.two[lang |= "en-US"] { color: green; }

		<p class="one" lang="en">This should be purple.</p>
		<p class="one" lang="en-US">This should be purple.</p>
		<p class="one" lang="en-US-PA">This should be purple.</p>
		<p class="one" lang="english">Should this be purple?</p>
		<p class="one" lang="US-en">This should NOT be purple.</p>	
		<p class="two" lang="en-USONA">Should this be green?</p>		
	*/
	@Test
	public void testDashmatch1()
	{
		XmlElement e1=new XmlElement("div", null, 1, null);
		e1.setAttr("lang", "en");
		XmlElement e2=new XmlElement("div", null, 1, null);
		e2.setAttr("lang", "en-US");
		XmlElement e3=new XmlElement("div", null, 1, null);
		e3.setAttr("lang", "en-US-PA");
		XmlElement e4=new XmlElement("div", null, 1, null);
		e4.setAttr("lang", "english");
		XmlElement e5=new XmlElement("div", null, 1, null);
		e5.setAttr("lang", "US-en");
		XmlElement e6=new XmlElement("div", null, 1, null);
		e6.setAttr("lang", "en-USONA");

		SelectorAttr sa1=new SelectorAttr("lang", "|=", "en");
		SelectorAttr sa2=new SelectorAttr("lang", "|=", "en-US");

		assertEquals(true, sa1.apply(e1));
		assertEquals(true, sa1.apply(e2));
		assertEquals(true, sa1.apply(e3));
		assertEquals(false, sa1.apply(e4));
		assertEquals(false, sa1.apply(e5));
		assertEquals(true, sa1.apply(e6));
	
		assertEquals(false, sa2.apply(e1));
		assertEquals(true, sa2.apply(e2));
		assertEquals(true, sa2.apply(e3));
		assertEquals(false, sa2.apply(e4));
		assertEquals(false, sa2.apply(e5));
		assertEquals(false, sa2.apply(e6));
	}	
	
	@Test
	public void testSpaceMatch1()
	{
		XmlElement e1=new XmlElement("div", null, 1, null);
		e1.setAttr("can", "pop");
		XmlElement e2=new XmlElement("div", null, 1, null);
		e2.setAttr("can", "pop alu lemon");
		XmlElement e3=new XmlElement("div", null, 1, null);
		e3.setAttr("can", "alu pop lemon");
		XmlElement e4=new XmlElement("div", null, 1, null);
		e4.setAttr("can", "alu lemon pop");
		XmlElement e5=new XmlElement("div", null, 1, null);
		e5.setAttr("can", " alu  pop  lemon ");
		XmlElement e6=new XmlElement("div", null, 1, null);
		e6.setAttr("can", " alu    lemon  pop  ");
		XmlElement e7=new XmlElement("div", null, 1, null);
		e7.setAttr("can", " alu    lemon    ");
		
		SelectorAttr sa1=new SelectorAttr("can", "~=", "pop");

		assertEquals(true, sa1.apply(e1));
		assertEquals(true, sa1.apply(e2));
		assertEquals(true, sa1.apply(e3));
		assertEquals(true, sa1.apply(e4));
		assertEquals(true, sa1.apply(e5));
		assertEquals(true, sa1.apply(e6));
		assertEquals(false, sa1.apply(e7));
	}
	
	@Test(expected=IllegalStateException.class)
	public void testInvalidOp()
	{
		SelectorAttr sa=new SelectorAttr("attr1", "ppp", "");
		sa.apply(element1);
	}
	
	@Test
	public void testEquals()
	{
		SelectorAttr sa1=new SelectorAttr("lang", "|=", "en");
		SelectorAttr sa2=new SelectorAttr("lang", "|=", "en");
		
		assertEquals(sa1, sa2);
	}
	
	@Test
	public void testToString()
	{
		SelectorAttr sa2=new SelectorAttr("lang", "|=", "en-US");
		System.out.println(sa2.toString());
	}
	
}
