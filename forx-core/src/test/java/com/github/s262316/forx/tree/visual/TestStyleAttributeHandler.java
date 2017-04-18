package com.github.s262316.forx.tree.visual;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mockito.Mockito;
import com.github.s262316.forx.tree.events2.MutationType;
import com.github.s262316.forx.tree.events2.XmlMutationEvent;
import com.github.s262316.forx.style.Identifier;
import com.github.s262316.forx.style.MediaType;
import com.github.s262316.forx.style.Value;

import static org.mockito.Mockito.*;

public class TestStyleAttributeHandler
{
	@Test
	public void testOnTargetAdded()
	{
		XmlVElement element=new XmlVElement("name", null, 0, null, null, null);
		element.setAttr("style", "display : block");
		
		StyleAttributeHandler handler=new StyleAttributeHandler(element, null);
		handler.selectedHandle(new XmlMutationEvent(MutationType.ADD, element));
		Value value=element.getPropertyValue("display", MediaType.MT_ALL);
		assertEquals(new Identifier("block"), value);
	}
	
	@Test
	public void testWithoutStyleAttr()
	{
		XmlVElement element=new XmlVElement("name", null, 0, null, null, null);
		
		StyleAttributeHandler listener=Mockito.spy(new StyleAttributeHandler(element, null));
		
		// ensure can handle no style attribute. not crashing is pass
		listener.selectedHandle(new XmlMutationEvent(MutationType.ADD, element));
		verify(listener, times(1)).added(any(XmlMutationEvent.class));
		
		// ensure is not called when passed through handle()
		listener.handle(new XmlMutationEvent(MutationType.ADD, element));
		verify(listener, times(1)).added(any(XmlMutationEvent.class));
	}
	
	@Test
	public void testAllMutationTypes()
	{
		XmlVElement element=new XmlVElement("name", null, 0, null, null, null);
		element.setAttr("style", "display : block");
		
		StyleAttributeHandler listener=Mockito.spy(new StyleAttributeHandler(element, null));
		
		XmlMutationEvent addedEvent=new XmlMutationEvent(MutationType.ADD, element);
		XmlMutationEvent removedEvent=new XmlMutationEvent(MutationType.REMOVE, element);
		XmlMutationEvent connectedEvent=new XmlMutationEvent(MutationType.CONNECT, element);
		XmlMutationEvent disconnectedEvent=new XmlMutationEvent(MutationType.DISCONNECT, element);
		
		listener.handle(addedEvent);
		listener.handle(removedEvent);
		listener.handle(connectedEvent);
		listener.handle(disconnectedEvent);
		
		verify(listener, times(1)).added(any(XmlMutationEvent.class));
		verify(listener, never()).removed(any(XmlMutationEvent.class));
		verify(listener, never()).connected(any(XmlMutationEvent.class));
		verify(listener, never()).disconnected(any(XmlMutationEvent.class));
	}

}


