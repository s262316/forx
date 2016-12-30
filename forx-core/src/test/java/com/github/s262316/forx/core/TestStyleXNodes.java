package com.github.s262316.forx.core;

import com.github.s262316.forx.tree.XNode;
import com.github.s262316.forx.tree.events2.EventDispatcher;
import com.github.s262316.forx.tree.impl.XmlDocument;
import com.github.s262316.forx.tree.impl.XmlElement;
import com.github.s262316.forx.tree.impl.XmlText;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertSame;

public class TestStyleXNodes
{
	@Test
	public void testFirstMember1()
	{
		XNode first;
		XmlDocument mockDoc=Mockito.mock(XmlDocument.class);
		EventDispatcher mockEventDispatcher=Mockito.mock(EventDispatcher.class);
		XmlElement e1=new XmlElement("div", mockDoc, 0, mockEventDispatcher);
		XmlElement e2=new XmlElement("div", mockDoc, 0, mockEventDispatcher);
		XmlElement e3=new XmlElement("div", mockDoc, 0, mockEventDispatcher);		

		e1.add(e2);
		e1.add(e3);

		first=StyleXNodes.firstMember(e1);
		assertSame(e2, first);
	}
	
	@Test
	public void testFirstMember2()
	{
		XNode first;
		XmlDocument mockDoc=Mockito.mock(XmlDocument.class);
		EventDispatcher mockEventDispatcher=Mockito.mock(EventDispatcher.class);
		XmlElement e1=new XmlElement("div", mockDoc, 0, mockEventDispatcher);
		XmlText t2=new XmlText("mytext", mockDoc, 0);
		XmlElement e3=new XmlElement("div", mockDoc, 0, mockEventDispatcher);		

		e1.add(t2);
		e1.add(e3);

		first=StyleXNodes.firstMember(e1);
		assertSame(e3, first);
	}
	

	
}



