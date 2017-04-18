package com.github.s262316.forx.tree.visual;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;


import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import com.github.s262316.forx.tree.events2.MutationType;
import com.github.s262316.forx.tree.events2.XmlMutationEvent;
import com.github.s262316.forx.style.Stylesheet;
import org.springframework.test.util.ReflectionTestUtils;


public class TestStyleElementHandler
{	
	// add <style> to connected tree
	@Test
	public void addStyleToConnectedTree()
	{
		XmlVDocument doc=Mockito.mock(XmlVDocument.class);
		XmlVElement element=Mockito.spy(new XmlVElement("style", doc, 0, null, null, null));
		StyleElementHandler listener=new StyleElementHandler(element, null);

		listener.added(new XmlMutationEvent(MutationType.ADD, element));
		verify(doc, never()).demergeStylesFrom(any(XmlVElement.class));
		verify(doc, never()).mergeStyles(any(XmlVElement.class), any(Stylesheet.class));

		listener.connected(new XmlMutationEvent(MutationType.CONNECT, element));
		verify(doc, times(1)).mergeStyles(any(XmlVElement.class), any(Stylesheet.class));
		
		// just for fun
		try
		{
			listener.connected(new XmlMutationEvent(MutationType.CONNECT, element));
			fail();
		}
		catch(IllegalArgumentException iae)
		{}		
	}

	// 2. add text to <style> in connected tree
	@Test
	public void addTextToStyleToConnectedTree()
	{
		XmlVDocument doc=Mockito.mock(XmlVDocument.class);
		XmlVElement element=Mockito.spy(new XmlVElement("style", doc, 0, null, null, null));
		StyleElementHandler listener=new StyleElementHandler(element, null);

		listener.added(new XmlMutationEvent(MutationType.ADD, element));
		listener.connected(new XmlMutationEvent(MutationType.CONNECT, element));

		Stylesheet emptystylesheet=(Stylesheet) ReflectionTestUtils.getField(listener, "styleElementStylesheet");
		
		XmlVText text=new XmlVText("p { display : block }", doc, 1, null);
		((List)ReflectionTestUtils.getField(element, "members")).add(text);
		
		// setup finished testing starts here
		listener.added(new XmlMutationEvent(MutationType.ADD, text));
		listener.connected(new XmlMutationEvent(MutationType.CONNECT, text));

		Stylesheet stylesheetwithtext=(Stylesheet)ReflectionTestUtils.getField(listener, "styleElementStylesheet");
		
		verify(doc, times(1)).demergeStylesFrom(any(XmlVElement.class));
		verify(doc, times(1)).mergeStyles(any(XmlVElement.class), eq(emptystylesheet));
		verify(doc, times(1)).mergeStyles(any(XmlVElement.class), eq(stylesheetwithtext));
	}
	
	// 3. add <style> in disconnected tree
	@Test
	public void addStyleToDisconnectedTree()
	{
		XmlVDocument doc=Mockito.mock(XmlVDocument.class);
		XmlVElement element=Mockito.spy(new XmlVElement("style", doc, 0, null, null, null));
		StyleElementHandler listener=new StyleElementHandler(element, null);

		listener.added(new XmlMutationEvent(MutationType.ADD, element));
		verify(doc, never()).demergeStylesFrom(any(XmlVElement.class));
		verify(doc, never()).mergeStyles(any(XmlVElement.class), any(Stylesheet.class));
	}
	
	// 4. add text to <style> in disconnected tree
	@Test
	public void addTextToStyleToDisconnectedTree()
	{
		XmlVDocument doc=Mockito.mock(XmlVDocument.class);
		XmlVElement element=Mockito.spy(new XmlVElement("style", doc, 0, null, null, null));
		StyleElementHandler listener=new StyleElementHandler(element, null);

		listener.added(new XmlMutationEvent(MutationType.ADD, element));
		
		XmlVText text=new XmlVText("p { display : block }", doc, 1, null);
		((List)ReflectionTestUtils.getField(element, "members")).add(text);
		
		// setup finished testing starts here
		listener.added(new XmlMutationEvent(MutationType.ADD, text));

		verify(doc, never()).demergeStylesFrom(any(XmlVElement.class));
		verify(doc, never()).mergeStyles(any(XmlVElement.class), any(Stylesheet.class));
	}
	
	// 	5. connect <style> + text to connected tree
	@Test
	public void connectStyleAndTextToConnectedTree()
	{
		XmlVDocument doc=Mockito.mock(XmlVDocument.class);
		XmlVElement styleElement=Mockito.spy(new XmlVElement("style", doc, 0, null, null, null));
		XmlVText text=new XmlVText("p { display : block }", doc, 1, null);
		StyleElementHandler listener=new StyleElementHandler(styleElement, null);
		
		listener.added(new XmlMutationEvent(MutationType.ADD, styleElement));
		((List)ReflectionTestUtils.getField(styleElement, "members")).add(text);
		listener.added(new XmlMutationEvent(MutationType.ADD, text));
		
		Stylesheet stylesheet=(Stylesheet)ReflectionTestUtils.getField(listener, "styleElementStylesheet");
		
		listener.connected(new XmlMutationEvent(MutationType.CONNECT, styleElement));
		listener.connected(new XmlMutationEvent(MutationType.CONNECT, text));

		verify(doc, times(0)).demergeStylesFrom(any(XmlVElement.class));
		verify(doc, times(1)).mergeStyles(any(XmlVElement.class), eq(stylesheet));
	}
	
	// 6. connect <style> + text to disconnected tree
	@Test
	public void connectStyleAndTextToDisconnectedTree()
	{
		XmlVDocument doc=Mockito.mock(XmlVDocument.class);
		XmlVElement styleElement=Mockito.spy(new XmlVElement("style", doc, 0, null, null, null));
		XmlVText text=new XmlVText("p { display : block }", doc, 1, null);
		StyleElementHandler listener=new StyleElementHandler(styleElement, null);

		listener.added(new XmlMutationEvent(MutationType.ADD, styleElement));
		((List)ReflectionTestUtils.getField(styleElement, "members")).add(text);
		listener.added(new XmlMutationEvent(MutationType.ADD, text));
		
		Stylesheet stylesheet=(Stylesheet)ReflectionTestUtils.getField(listener, "styleElementStylesheet");

		verify(doc, times(0)).demergeStylesFrom(any(XmlVElement.class));
		verify(doc, times(0)).mergeStyles(any(XmlVElement.class), eq(stylesheet));
	}
	
	// 7. add 2nd set of text to <style> in connected tree
	@Test
	public void addMoreTextToStyleInConnectedTree()
	{
		XmlVDocument doc=Mockito.mock(XmlVDocument.class);
		XmlVElement styleElement=Mockito.spy(new XmlVElement("style", doc, 0, null, null, null));
		XmlVText text1=new XmlVText("p { display : block }", doc, 1, null);
		XmlVText text2=new XmlVText("div { display : block }", doc, 2, null);
		StyleElementHandler listener=new StyleElementHandler(styleElement, null);

		listener.added(new XmlMutationEvent(MutationType.ADD, styleElement));
		listener.connected(new XmlMutationEvent(MutationType.CONNECT, styleElement));

		Stylesheet stylesheetEmpty=(Stylesheet)ReflectionTestUtils.getField(listener, "styleElementStylesheet");
		
		((List)ReflectionTestUtils.getField(styleElement, "members")).add(text1);
		listener.added(new XmlMutationEvent(MutationType.ADD, text1));
		listener.connected(new XmlMutationEvent(MutationType.CONNECT, text1));
		
		Stylesheet stylesheetBefore=(Stylesheet)ReflectionTestUtils.getField(listener, "styleElementStylesheet");
		
		((List)ReflectionTestUtils.getField(styleElement, "members")).add(text2);
		listener.added(new XmlMutationEvent(MutationType.ADD, text2));
		listener.connected(new XmlMutationEvent(MutationType.CONNECT, text2));
		
		Stylesheet stylesheetAfter=(Stylesheet)ReflectionTestUtils.getField(listener, "styleElementStylesheet");
		
		verify(doc, times(2)).demergeStylesFrom(any(XmlVElement.class));
		verify(doc, times(1)).mergeStyles(any(XmlVElement.class), eq(stylesheetEmpty));
		verify(doc, times(1)).mergeStyles(any(XmlVElement.class), eq(stylesheetBefore));
		verify(doc, times(1)).mergeStyles(any(XmlVElement.class), eq(stylesheetAfter));
	}
	
	// 8. add 2nd set of text to <style> in disconnected tree
	@Test
	public void addMoreTextToDisconnectedTree()
	{
		XmlVDocument doc=Mockito.mock(XmlVDocument.class);
		XmlVElement styleElement=Mockito.spy(new XmlVElement("style", doc, 0, null, null, null));
		XmlVText text1=new XmlVText("p { display : block }", doc, 1, null);
		XmlVText text2=new XmlVText("div { display : block }", doc, 2, null);
		StyleElementHandler listener=new StyleElementHandler(styleElement, null);

		listener.added(new XmlMutationEvent(MutationType.ADD, styleElement));
		((List)ReflectionTestUtils.getField(styleElement, "members")).add(text1);
		listener.added(new XmlMutationEvent(MutationType.ADD, text1));
		
		Stylesheet stylesheetBefore=(Stylesheet)ReflectionTestUtils.getField(listener, "styleElementStylesheet");
		
		((List)ReflectionTestUtils.getField(styleElement, "members")).add(text2);
		listener.added(new XmlMutationEvent(MutationType.ADD, text2));
		
		Stylesheet stylesheetAfter=(Stylesheet)ReflectionTestUtils.getField(listener, "styleElementStylesheet");
		
		verify(doc, times(0)).demergeStylesFrom(any(XmlVElement.class));
		verify(doc, times(0)).mergeStyles(any(XmlVElement.class), eq(stylesheetBefore));
		verify(doc, times(0)).mergeStyles(any(XmlVElement.class), eq(stylesheetAfter));
		
	}
	
	//9. disconnect <style> from connected tree
	@Test
	public void testDisconnectStyleFromConnectedTree()
	{
		XmlVDocument doc=Mockito.mock(XmlVDocument.class);
		XmlVElement element=Mockito.spy(new XmlVElement("style", doc, 0, null, null, null));
		StyleElementHandler listener=new StyleElementHandler(element, null);

		listener.added(new XmlMutationEvent(MutationType.ADD, element));
		listener.connected(new XmlMutationEvent(MutationType.CONNECT, element));

		listener.disconnected(new XmlMutationEvent(MutationType.DISCONNECT, element));
		listener.removed(new XmlMutationEvent(MutationType.REMOVE, element));
		
		verify(doc, times(1)).demergeStylesFrom(any(XmlVElement.class));
		verify(doc, times(1)).mergeStyles(any(XmlVElement.class), any(Stylesheet.class));
	}
	
	//10. disconnect <style> from disconnected tree
	@Test
	public void testDisconnectStyleFromDisconnectedTree()
	{
		XmlVDocument doc=Mockito.mock(XmlVDocument.class);
		XmlVElement element=Mockito.spy(new XmlVElement("style", doc, 0, null, null, null));
		StyleElementHandler listener=new StyleElementHandler(element, null);

		listener.added(new XmlMutationEvent(MutationType.ADD, element));
		listener.removed(new XmlMutationEvent(MutationType.REMOVE, element));
		
		verify(doc, times(0)).demergeStylesFrom(any(XmlVElement.class));
		verify(doc, times(0)).mergeStyles(any(XmlVElement.class), any(Stylesheet.class));		
	}
	
	//11. disconnect text from style in connected tree
	@Test
	public void testDisconnectTextFromStyleInConnectedTree()
	{
		XmlVDocument doc=Mockito.mock(XmlVDocument.class);
		XmlVElement element=Mockito.spy(new XmlVElement("style", doc, 0, null, null, null));
		StyleElementHandler listener=new StyleElementHandler(element, null);

		listener.added(new XmlMutationEvent(MutationType.ADD, element));
		listener.connected(new XmlMutationEvent(MutationType.CONNECT, element));

		Stylesheet emptystylesheet=(Stylesheet)ReflectionTestUtils.getField(listener, "styleElementStylesheet");
		
		XmlVText text=new XmlVText("p { display : block }", doc, 1, null);
		((List)ReflectionTestUtils.getField(element, "members")).add(text);
		
		listener.added(new XmlMutationEvent(MutationType.ADD, text));
		listener.connected(new XmlMutationEvent(MutationType.CONNECT, text));

		Stylesheet stylesheetwithtext=(Stylesheet)ReflectionTestUtils.getField(listener, "styleElementStylesheet");

		// setup finished testing starts here
		listener.disconnected(new XmlMutationEvent(MutationType.DISCONNECT, element));
		listener.removed(new XmlMutationEvent(MutationType.REMOVE, element));
		
		verify(doc, times(2)).demergeStylesFrom(any(XmlVElement.class));
		verify(doc, times(1)).mergeStyles(any(XmlVElement.class), eq(emptystylesheet));
		verify(doc, times(1)).mergeStyles(any(XmlVElement.class), eq(stylesheetwithtext));
	}
	
	//12. disconnect text from style in disconnected tree
	@Test
	public void testDisconnectTextFromStyleInDisconnectedTree()
	{
		XmlVDocument doc=Mockito.mock(XmlVDocument.class);
		XmlVElement element=Mockito.spy(new XmlVElement("style", doc, 0, null, null, null));
		StyleElementHandler listener=new StyleElementHandler(element, null);

		listener.added(new XmlMutationEvent(MutationType.ADD, element));

		Stylesheet emptystylesheet=(Stylesheet)ReflectionTestUtils.getField(listener, "styleElementStylesheet");
		
		XmlVText text=new XmlVText("p { display : block }", doc, 1, null);
		((List)ReflectionTestUtils.getField(element, "members")).add(text);
		
		listener.added(new XmlMutationEvent(MutationType.ADD, text));

		Stylesheet stylesheetwithtext=(Stylesheet)ReflectionTestUtils.getField(listener, "styleElementStylesheet");

		// setup finished testing starts here
		listener.removed(new XmlMutationEvent(MutationType.REMOVE, element));
		
		verify(doc, times(0)).demergeStylesFrom(any(XmlVElement.class));
		verify(doc, times(0)).mergeStyles(any(XmlVElement.class), eq(emptystylesheet));
		verify(doc, times(0)).mergeStyles(any(XmlVElement.class), eq(stylesheetwithtext));		
	}
	
	//13. disconnect 2nd text from style in connected tree
	@Test
	public void testDisconnectText2FromStyleInConnectedTree()
	{
		XmlVDocument doc=Mockito.mock(XmlVDocument.class);
		XmlVElement styleElement=Mockito.spy(new XmlVElement("style", doc, 0, null, null, null));
		XmlVText text1=new XmlVText("p { display : block }", doc, 1, null);
		XmlVText text2=new XmlVText("div { display : block }", doc, 2, null);
		StyleElementHandler listener=new StyleElementHandler(styleElement, null);

		listener.added(new XmlMutationEvent(MutationType.ADD, styleElement)); 
		listener.connected(new XmlMutationEvent(MutationType.CONNECT, styleElement)); // merge=1

		Stylesheet stylesheetEmpty=(Stylesheet)ReflectionTestUtils.getField(listener, "styleElementStylesheet");
		
		((List)ReflectionTestUtils.getField(styleElement, "members")).add(text1);
		listener.added(new XmlMutationEvent(MutationType.ADD, text1)); // demerge=1, merge=2
		listener.connected(new XmlMutationEvent(MutationType.CONNECT, text1));
		
		Stylesheet stylesheetBefore=(Stylesheet)ReflectionTestUtils.getField(listener, "styleElementStylesheet");
		
		((List)ReflectionTestUtils.getField(styleElement, "members")).add(text2);
		listener.added(new XmlMutationEvent(MutationType.ADD, text2));// demerge=2, merge=3
		listener.connected(new XmlMutationEvent(MutationType.CONNECT, text2));
		
		Stylesheet stylesheetAfter=(Stylesheet)ReflectionTestUtils.getField(listener, "styleElementStylesheet");
		
		listener.disconnected(new XmlMutationEvent(MutationType.DISCONNECT, text2));
		listener.removed(new XmlMutationEvent(MutationType.REMOVE, text2));// demerge=3

		Stylesheet stylesheetAfter2=(Stylesheet)ReflectionTestUtils.getField(listener, "styleElementStylesheet");
		
		assertNotSame(stylesheetEmpty, stylesheetBefore);
		assertNotSame(stylesheetBefore, stylesheetAfter);
		assertNotSame(stylesheetAfter, stylesheetAfter2);
		
		verify(doc, times(3)).demergeStylesFrom(any(XmlVElement.class));
		verify(doc, times(1)).mergeStyles(any(XmlVElement.class), eq(stylesheetEmpty));
		verify(doc, times(1)).mergeStyles(any(XmlVElement.class), eq(stylesheetBefore));
		verify(doc, times(1)).mergeStyles(any(XmlVElement.class), eq(stylesheetAfter));
		verify(doc, times(1)).mergeStyles(any(XmlVElement.class), eq(stylesheetAfter2));

	}
	
	//14. disconnect 2nd text from style in disconnected tree
	@Test
	public void testDisconnectText2FromStyleInDisconnectedTree()
	{
		XmlVDocument doc=Mockito.mock(XmlVDocument.class);
		XmlVElement styleElement=Mockito.spy(new XmlVElement("style", doc, 0, null, null, null));
		XmlVText text1=new XmlVText("p { display : block }", doc, 1, null);
		XmlVText text2=new XmlVText("div { display : block }", doc, 2, null);
		StyleElementHandler listener=new StyleElementHandler(styleElement, null);

		listener.added(new XmlMutationEvent(MutationType.ADD, styleElement));

		Stylesheet stylesheetEmpty=(Stylesheet)ReflectionTestUtils.getField(listener, "styleElementStylesheet");
		
		((List)ReflectionTestUtils.getField(styleElement, "members")).add(text1);
		listener.added(new XmlMutationEvent(MutationType.ADD, text1));
		
		Stylesheet stylesheetBefore=(Stylesheet)ReflectionTestUtils.getField(listener, "styleElementStylesheet");
		
		((List)ReflectionTestUtils.getField(styleElement, "members")).add(text2);
		listener.added(new XmlMutationEvent(MutationType.ADD, text2));
		
		Stylesheet stylesheetAfter=(Stylesheet)ReflectionTestUtils.getField(listener, "styleElementStylesheet");
		
		listener.removed(new XmlMutationEvent(MutationType.REMOVE, text2));
		
		verify(doc, times(0)).demergeStylesFrom(any(XmlVElement.class));
		verify(doc, times(0)).mergeStyles(any(XmlVElement.class), eq(stylesheetEmpty));
		verify(doc, times(0)).mergeStyles(any(XmlVElement.class), eq(stylesheetBefore));
		verify(doc, times(0)).mergeStyles(any(XmlVElement.class), eq(stylesheetAfter));				
	}
	
	@Test
	public void testAllMutationTypes() throws Exception
	{
		XmlVDocument doc=Mockito.mock(XmlVDocument.class);
		XmlVElement element=Mockito.spy(new XmlVElement("style", null, 0, null, null, null));
		when(element.getDocument()).thenReturn(doc);
		
		StyleElementHandler listener=Mockito.spy(new StyleElementHandler(element, null));

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
		verify(listener, times(1)).connected(any(XmlMutationEvent.class));
		verify(listener, times(1)).disconnected(any(XmlMutationEvent.class));
	}
}

