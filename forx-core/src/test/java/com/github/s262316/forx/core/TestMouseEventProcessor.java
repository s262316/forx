package com.github.s262316.forx.core;

import com.github.s262316.forx.tree.events2.Event;
import com.github.s262316.forx.tree.events2.EventDispatcher;
import com.github.s262316.forx.tree.events2.MouseEventType;
import com.github.s262316.forx.tree.events2.XmlMouseEvent;
import com.github.s262316.forx.tree.XmlNode;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TestMouseEventProcessor
{
	@Mock
	XmlNode node1, node2, node3, node4;
	@Mock
	XmlNode hnode0, hnode0_1, hnode0_2, hnode0_1_1, hnode0_1_2, hnode0_2_1, hnode0_2_2, hnode0_2_3;
	
	@Mock
	EventDispatcher eventDispatcher;
	
	@Before
	public void setup()
	{
		when(hnode0_1.parentNode()).thenReturn(hnode0);
		when(hnode0_2.parentNode()).thenReturn(hnode0);
		when(hnode0_1_1.parentNode()).thenReturn(hnode0_1);
		when(hnode0_1_2.parentNode()).thenReturn(hnode0_1);
		when(hnode0_2_1.parentNode()).thenReturn(hnode0_2);
	}
	
	@Test
	public void testProcessMouseOuts1()
	{
		MouseEventProcessor proc=new MouseEventProcessor(eventDispatcher);
		Set<XmlNode> mouseOverNodes=(Set)ReflectionTestUtils.getField(proc, "mouseOver");
		
		// 1 node currently over. 1 node still over
		mouseOverNodes.add(node1);
		proc.processMouseOuts(Sets.newHashSet(node1), 0, 0);
		assertEquals(Sets.newHashSet(node1), mouseOverNodes);
		
		// 1 node currently over. 0 nodes now over
		proc.processMouseOuts(Sets.<XmlNode>newHashSet(), 0, 0);
		assertTrue(mouseOverNodes.isEmpty());
		verify(eventDispatcher).fire(node1, new XmlMouseEvent(MouseEventType.MOUSE_LEFT, 0, 0));
	}
	
	@Test
	public void testProcessMouseOuts2()
	{	
		MouseEventProcessor proc=new MouseEventProcessor(eventDispatcher);
		Set<XmlNode> mouseOverNodes=(Set)ReflectionTestUtils.getField(proc, "mouseOver");

		// 0 nodes currently over. 1 node over now
		// not a mouseout so does nothing to mouseOverNodes
		proc.processMouseOuts(Sets.newHashSet(node1), 0, 0);
		assertTrue(mouseOverNodes.isEmpty());
		verify(eventDispatcher, never()).fire(any(XmlNode.class), any(Event.class));
	}
	
	@Test
	public void testProcessMouseOuts3()
	{
		MouseEventProcessor proc=new MouseEventProcessor(eventDispatcher);
		Set<XmlNode> mouseOverNodes=(Set)ReflectionTestUtils.getField(proc, "mouseOver");

		// 1 node currently over. 1 nodes still over after
		mouseOverNodes.add(hnode0_1_1);
		proc.processMouseOuts(Sets.newHashSet(hnode0_1_1, hnode0_1_2), 0, 0);
		assertEquals(Sets.newHashSet(hnode0_1_1), mouseOverNodes);

		verify(eventDispatcher, never()).fire(any(XmlNode.class), any(Event.class));
	}
	
	@Test
	public void testProcessMouseOuts4()
	{
		MouseEventProcessor proc=new MouseEventProcessor(eventDispatcher);
		Set<XmlNode> mouseOverNodes=(Set)ReflectionTestUtils.getField(proc, "mouseOver");

		// 2 node currently over. 1 nodes still over after
		mouseOverNodes.add(hnode0_1_1);
		mouseOverNodes.add(hnode0_1_2);
		proc.processMouseOuts(Sets.newHashSet(hnode0_1_1), 0, 0);
		assertEquals(Sets.newHashSet(hnode0_1_1), mouseOverNodes);

		verify(eventDispatcher).fire(hnode0_1_2, new XmlMouseEvent(MouseEventType.MOUSE_LEFT, 0, 0));
		verify(eventDispatcher, never()).fire(eq(hnode0_1_1), any(Event.class));
	}
	
	@Test
	public void testProcessMouseOuts5()
	{
		MouseEventProcessor proc=new MouseEventProcessor(eventDispatcher);
		Set<XmlNode> mouseOverNodes=(Set)ReflectionTestUtils.getField(proc, "mouseOver");

		// 2 node currently over. 0 modes after
		mouseOverNodes.add(hnode0_1_1);
		mouseOverNodes.add(hnode0_1_2);
		proc.processMouseOuts(Sets.<XmlNode>newHashSet(), 0, 0);
		assertEquals(Sets.newHashSet(), mouseOverNodes);

		verify(eventDispatcher, times(1)).fire(hnode0_1_1, new XmlMouseEvent(MouseEventType.MOUSE_LEFT, 0, 0));
		verify(eventDispatcher, times(1)).fire(hnode0_1_2, new XmlMouseEvent(MouseEventType.MOUSE_LEFT, 0, 0));
	}
	
	@Test
	public void testProcessMouseOuts6()
	{
		MouseEventProcessor proc=new MouseEventProcessor(eventDispatcher);
		Set<XmlNode> mouseOverNodes=(Set)ReflectionTestUtils.getField(proc, "mouseOver");

		// 2 node currently over. 0 nodes after. only 1 event fired because they're in the same path
		mouseOverNodes.add(hnode0_1);
		mouseOverNodes.add(hnode0_1_1);
		proc.processMouseOuts(Sets.<XmlNode>newHashSet(), 0, 0);
		assertEquals(Sets.newHashSet(), mouseOverNodes);

		verify(eventDispatcher, times(1)).fire(hnode0_1_1, new XmlMouseEvent(MouseEventType.MOUSE_LEFT, 0, 0));
		verify(eventDispatcher, times(0)).fire(hnode0_1, new XmlMouseEvent(MouseEventType.MOUSE_LEFT, 0, 0));
	}

	@Test
	public void testProcessMouseOuts7()
	{
		MouseEventProcessor proc=new MouseEventProcessor(eventDispatcher);
		Set<XmlNode> mouseOverNodes=(Set)ReflectionTestUtils.getField(proc, "mouseOver");

		// 4 node currently over. 0 nodes after. only 2 event fired because 2 in the same path
		mouseOverNodes.add(hnode0_1);
		mouseOverNodes.add(hnode0_1_1);
		mouseOverNodes.add(hnode0_2);
		mouseOverNodes.add(hnode0_2_1);
		
		proc.processMouseOuts(Sets.<XmlNode>newHashSet(), 0, 0);
		assertEquals(Sets.newHashSet(), mouseOverNodes);

		verify(eventDispatcher, times(1)).fire(hnode0_1_1, new XmlMouseEvent(MouseEventType.MOUSE_LEFT, 0, 0));
		verify(eventDispatcher, times(1)).fire(hnode0_2_1, new XmlMouseEvent(MouseEventType.MOUSE_LEFT, 0, 0));
	}
	
	@Test
	public void testProcessMouseIns1()
	{
		MouseEventProcessor proc=new MouseEventProcessor(eventDispatcher);
		Set<XmlNode> mouseOverNodes=(Set)ReflectionTestUtils.getField(proc, "mouseOver");
		
		// 1 node currently over. 1 node still over
		mouseOverNodes.add(node1);
		proc.processMouseIns(Sets.newHashSet(node1), 0, 0);
		assertEquals(Sets.newHashSet(node1), mouseOverNodes);
		
		// 1 node currently over. 0 nodes now over
		proc.processMouseIns(Sets.<XmlNode>newHashSet(), 0, 0);
		assertEquals(Sets.newHashSet(node1), mouseOverNodes);
		
		verify(eventDispatcher, never()).fire(any(XmlNode.class), any(Event.class));
	}
	
	@Test
	public void testProcessMouseIns2()
	{	
		MouseEventProcessor proc=new MouseEventProcessor(eventDispatcher);
		Set<XmlNode> mouseOverNodes=(Set)ReflectionTestUtils.getField(proc, "mouseOver");

		// 0 nodes currently over. 1 node over now
		proc.processMouseIns(Sets.newHashSet(node1), 0, 0);
		assertEquals(Sets.newHashSet(node1), mouseOverNodes);
		verify(eventDispatcher).fire(node1, new XmlMouseEvent(MouseEventType.MOUSE_ENTERED, 0, 0));
	}
	
	@Test
	public void testProcessMouseIns3()
	{
		MouseEventProcessor proc=new MouseEventProcessor(eventDispatcher);
		Set<XmlNode> mouseOverNodes=(Set)ReflectionTestUtils.getField(proc, "mouseOver");

		// 1 node currently over. 2 nodes over after
		mouseOverNodes.add(hnode0_1_1);
		proc.processMouseIns(Sets.newHashSet(hnode0_1_1, hnode0_1_2), 0, 0);
		assertEquals(Sets.newHashSet(hnode0_1_1, hnode0_1_2), mouseOverNodes);

		verify(eventDispatcher).fire(hnode0_1_2, new XmlMouseEvent(MouseEventType.MOUSE_ENTERED, 0, 0));
	}
	
	@Test
	public void testProcessMouseIns4()
	{
		MouseEventProcessor proc=new MouseEventProcessor(eventDispatcher);
		Set<XmlNode> mouseOverNodes=(Set)ReflectionTestUtils.getField(proc, "mouseOver");

		// 2 node currently over. 2 nodes still over after
		mouseOverNodes.add(hnode0_1_1);
		mouseOverNodes.add(hnode0_1_2);
		proc.processMouseIns(Sets.newHashSet(hnode0_1_1), 0, 0);
		assertEquals(Sets.newHashSet(hnode0_1_1, hnode0_1_2), mouseOverNodes);

		verify(eventDispatcher, never()).fire(eq(hnode0_1_1), any(Event.class));
		verify(eventDispatcher, never()).fire(eq(hnode0_1_2), any(Event.class));
	}
	
	@Test
	public void testProcessMouseIns5()
	{
		MouseEventProcessor proc=new MouseEventProcessor(eventDispatcher);
		Set<XmlNode> mouseOverNodes=(Set)ReflectionTestUtils.getField(proc, "mouseOver");

		// 2 node currently over. 2 modes after
		mouseOverNodes.add(hnode0_1_1);
		mouseOverNodes.add(hnode0_1_2);
		proc.processMouseIns(Sets.<XmlNode>newHashSet(), 0, 0);
		assertEquals(Sets.newHashSet(hnode0_1_1, hnode0_1_2), mouseOverNodes);

		verify(eventDispatcher, never()).fire(eq(hnode0_1_1), any(Event.class));
		verify(eventDispatcher, never()).fire(eq(hnode0_1_2), any(Event.class));
	}
	
	@Test
	public void testProcessMouseIns6()
	{
		MouseEventProcessor proc=new MouseEventProcessor(eventDispatcher);
		Set<XmlNode> mouseOverNodes=(Set)ReflectionTestUtils.getField(proc, "mouseOver");

		// 0 node currently over. 2 nodes after. only 1 event fired because they're in the same path
		proc.processMouseIns(Sets.<XmlNode>newHashSet(hnode0_1, hnode0_1_1), 0, 0);
		assertEquals(Sets.newHashSet(hnode0_1, hnode0_1_1), mouseOverNodes);

		verify(eventDispatcher, times(1)).fire(hnode0_1_1, new XmlMouseEvent(MouseEventType.MOUSE_ENTERED, 0, 0));
		verify(eventDispatcher, times(0)).fire(hnode0_1, new XmlMouseEvent(MouseEventType.MOUSE_ENTERED, 0, 0));
	}

	@Test
	public void testProcessMouseIns7()
	{
		MouseEventProcessor proc=new MouseEventProcessor(eventDispatcher);
		Set<XmlNode> mouseOverNodes=(Set)ReflectionTestUtils.getField(proc, "mouseOver");

		// 0 node currently over. 4 nodes after. only 2 event fired because 2 in the same path
	
		proc.processMouseIns(Sets.<XmlNode>newHashSet(hnode0_1, hnode0_1_1, hnode0_2, hnode0_2_1), 0, 0);
		assertEquals(Sets.newHashSet(hnode0_1, hnode0_1_1, hnode0_2, hnode0_2_1), mouseOverNodes);

		verify(eventDispatcher, times(1)).fire(hnode0_1_1, new XmlMouseEvent(MouseEventType.MOUSE_ENTERED, 0, 0));
		verify(eventDispatcher, times(1)).fire(hnode0_2_1, new XmlMouseEvent(MouseEventType.MOUSE_ENTERED, 0, 0));
		verify(eventDispatcher, times(0)).fire(hnode0_1, new XmlMouseEvent(MouseEventType.MOUSE_ENTERED, 0, 0));
		verify(eventDispatcher, times(0)).fire(hnode0_2, new XmlMouseEvent(MouseEventType.MOUSE_ENTERED, 0, 0));
	}
}



