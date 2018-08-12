package com.github.s262316.forx.tree.events2;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.s262316.forx.tree.XmlNode;

@RunWith(MockitoJUnitRunner.class)
public class TestEventDispatcher
{
	EventDispatcher eventDispatcher1=new EventDispatcher();
	EventDispatcher eventDispatcher2=new EventDispatcher();
	Event1 event1=new Event1();
	Event2 event2=new Event2();
	Event3 event3=new Event3();

	@Mock
	Listener<Event> event1Listener;
	@Mock
	Listener<Event> event2Listener;
	@Mock
	Listener<Event> event3Listener;
	
	@Mock
	XmlNode node1, node2, node3;
	
	@Before
	public void setup()
	{
		eventDispatcher1.addListener(node1, PropagationType.BUBBLE, Event1.class, event1Listener);
		eventDispatcher1.addListener(node2, PropagationType.BUBBLE, Event1.class, event1Listener);
		eventDispatcher1.addListener(node3, PropagationType.BUBBLE, Event1.class, event1Listener);
		eventDispatcher1.addListener(node1, PropagationType.BUBBLE, Event2.class, event2Listener);
		eventDispatcher1.addListener(node2, PropagationType.BUBBLE, Event2.class, event2Listener);
		eventDispatcher1.addListener(node3, PropagationType.BUBBLE, Event2.class, event2Listener);
		eventDispatcher1.addListener(node1, PropagationType.BUBBLE, Event3.class, event3Listener);
		eventDispatcher1.addListener(node2, PropagationType.BUBBLE, Event3.class, event3Listener);
		eventDispatcher1.addListener(node3, PropagationType.BUBBLE, Event3.class, event3Listener);
		eventDispatcher1.addListener(node1, PropagationType.CAPTURE, Event1.class, event1Listener);
		eventDispatcher1.addListener(node2, PropagationType.CAPTURE, Event1.class, event1Listener);
		eventDispatcher1.addListener(node3, PropagationType.CAPTURE, Event1.class, event1Listener);
		eventDispatcher1.addListener(node1, PropagationType.CAPTURE, Event2.class, event2Listener);
		eventDispatcher1.addListener(node2, PropagationType.CAPTURE, Event2.class, event2Listener);
		eventDispatcher1.addListener(node3, PropagationType.CAPTURE, Event2.class, event2Listener);
		eventDispatcher1.addListener(node1, PropagationType.CAPTURE, Event3.class, event3Listener);
		eventDispatcher1.addListener(node2, PropagationType.CAPTURE, Event3.class, event3Listener);
		eventDispatcher1.addListener(node3, PropagationType.CAPTURE, Event3.class, event3Listener);
		eventDispatcher1.addListener(node1, PropagationType.ON_TARGET, Event1.class, event1Listener);
		eventDispatcher1.addListener(node2, PropagationType.ON_TARGET, Event1.class, event1Listener);
		eventDispatcher1.addListener(node3, PropagationType.ON_TARGET, Event1.class, event1Listener);
		eventDispatcher1.addListener(node1, PropagationType.ON_TARGET, Event2.class, event2Listener);
		eventDispatcher1.addListener(node2, PropagationType.ON_TARGET, Event2.class, event2Listener);
		eventDispatcher1.addListener(node3, PropagationType.ON_TARGET, Event2.class, event2Listener);
		eventDispatcher1.addListener(node1, PropagationType.ON_TARGET, Event3.class, event3Listener);
		eventDispatcher1.addListener(node2, PropagationType.ON_TARGET, Event3.class, event3Listener);
		eventDispatcher1.addListener(node3, PropagationType.ON_TARGET, Event3.class, event3Listener);		
		
		eventDispatcher2.addListener(node1, PropagationType.BUBBLE, Event1.class, event1Listener);
		eventDispatcher2.addListener(node1, PropagationType.BUBBLE, Event1.class, event2Listener);
		
		eventDispatcher2.addListener(node1, PropagationType.BUBBLE, Event3.class, event1Listener);
		eventDispatcher2.addListener(node2, PropagationType.CAPTURE, Event3.class, event2Listener);
		eventDispatcher2.addListener(node3, PropagationType.ON_TARGET, Event3.class, event3Listener);
		
		
	}
	
	@Test
	public void testAllKindsUniqueListenerForEach()
	{
		eventDispatcher1.fire(node1, event1);
		Mockito.verify(event1Listener, Mockito.times(3)).handle(event1);
		eventDispatcher1.fire(node1, event2);
		Mockito.verify(event2Listener, Mockito.times(3)).handle(event2);
		eventDispatcher1.fire(node1, event3);
		Mockito.verify(event3Listener, Mockito.times(3)).handle(event3);
		eventDispatcher1.fire(node2, event1);
		Mockito.verify(event1Listener, Mockito.times(6)).handle(event1);
		eventDispatcher1.fire(node2, event2);
		Mockito.verify(event2Listener, Mockito.times(6)).handle(event2);
		eventDispatcher1.fire(node2, event3);
		Mockito.verify(event3Listener, Mockito.times(6)).handle(event3);
		eventDispatcher1.fire(node3, event1);
		Mockito.verify(event1Listener, Mockito.times(9)).handle(event1);
		eventDispatcher1.fire(node3, event2);
		Mockito.verify(event2Listener, Mockito.times(9)).handle(event2);
		eventDispatcher1.fire(node3, event3);
		Mockito.verify(event3Listener, Mockito.times(9)).handle(event3);
	}
	
	@Test
	public void testMultipleListenersInstalled()
	{
		eventDispatcher2.fire(node1, event1);
		Mockito.verify(event1Listener, Mockito.times(1)).handle(event1);
		Mockito.verify(event2Listener, Mockito.times(1)).handle(event1);
	}
	
	@Test
	public void testJustBubble()
	{
		eventDispatcher2.fire(node1, event3);
		Mockito.verify(event1Listener, Mockito.times(1)).handle(event3);
	}
	
	@Test
	public void testJustCaputre()
	{
		eventDispatcher2.fire(node2, event3);
		Mockito.verify(event2Listener, Mockito.times(1)).handle(event3);
	}
	
	@Test
	public void testJustOnTarget()
	{
		eventDispatcher2.fire(node3, event3);
		Mockito.verify(event3Listener, Mockito.times(1)).handle(event3);
	}
}

class Event1 implements Event
{}

class Event2 implements Event
{}

class Event3 implements Event
{}
