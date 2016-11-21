package com.github.s262316.forx.tree.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.s262316.forx.tree.NodeType;
import com.github.s262316.forx.tree.events2.EventDispatcher;
import com.github.s262316.forx.tree.events2.Events;
import com.github.s262316.forx.tree.events2.MutationType;
import com.github.s262316.forx.tree.events2.XmlMutationEvent;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Events.class})
public class TestXmlElement
{
	@Mock
	EventDispatcher eventDispatcher;
	
	@Before
	public void setup()
	{
		PowerMockito.mockStatic(Events.class);			
		
	}
	
	@Test
	public void testAddText()
	{
		XmlDocument doc=Mockito.mock(XmlDocument.class);
		XmlElement element=Mockito.spy(new XmlElement("name", doc, 1, eventDispatcher));
		XmlText text=new XmlText("text1", doc, 2);

		when(doc.type()).thenReturn(NodeType.X_DOCUMENT);
		when(element.parentNode()).thenReturn(doc);
		
		element.add(text);
		
		assertSame(element, text.parentNode());
		assertNull(text.previousNode());
		assertNull(text.nextNode());
		assertTrue(text.isConnected());
		assertEquals(1, element.getMembers().size());
		assertSame(text, element.getMembers().get(0));
		
		InOrder inOrder = inOrder(doc);
		inOrder.verify(doc).dispatchEvent(eq(text), Mockito.argThat(new XmlMutationEventMatcher(new XmlMutationEvent(MutationType.ADD, text))));
		
		verify(eventDispatcher).broadcast(eq(text), Mockito.argThat(new XmlMutationEventMatcher(new XmlMutationEvent(MutationType.CONNECT, text))));
	}
	
	@Test
	public void testAddTextNotConnected()
	{
		XmlDocument doc=Mockito.mock(XmlDocument.class);
		XmlElement element=Mockito.spy(new XmlElement("name", doc, 1, eventDispatcher));
		XmlText text=new XmlText("text1", doc, 2);

		when(element.parentNode()).thenReturn(doc);
		
		element.add(text);
		
		assertSame(element, text.parentNode());
		assertNull(text.previousNode());
		assertNull(text.nextNode());
		assertFalse(text.isConnected());
		assertEquals(1, element.getMembers().size());
		assertSame(text, element.getMembers().get(0));
		
		InOrder inOrder = inOrder(doc);
		inOrder.verify(doc).dispatchEvent(eq(text), Mockito.argThat(new XmlMutationEventMatcher(new XmlMutationEvent(MutationType.ADD, text))));
		
		verify(eventDispatcher, never()).broadcast(eq(text), Mockito.argThat(new XmlMutationEventMatcher(new XmlMutationEvent(MutationType.CONNECT, text))));
	}	
	
}

class XmlMutationEventMatcher extends ArgumentMatcher<XmlMutationEvent>
{
	XmlMutationEvent ref;
	
	public XmlMutationEventMatcher(XmlMutationEvent ref)
	{
		this.ref=ref;
	}
	
	@Override
    public boolean matches(Object other)
    {
    	XmlMutationEvent right=(XmlMutationEvent)other;
    	
    	return (ref.getType()==right.getType() && ref.getSubject()==right.getSubject());
    }
}



