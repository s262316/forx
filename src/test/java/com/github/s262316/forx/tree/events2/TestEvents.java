package com.github.s262316.forx.tree.events2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedHashSet;
import java.util.List;

import org.junit.Test;

import com.github.s262316.forx.tree.events2.Events;
import com.github.s262316.forx.tree.impl.XmlNode;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class TestEvents
{
	@Test
	public void testFindEventPaths1()
	{
		// one node hit
		XmlNode hit=mock(XmlNode.class);
		when(hit.parentNode()).thenReturn(null);
		
		List<LinkedHashSet<XmlNode>> paths=Events.findEventPaths(Lists.newArrayList(hit));
		
		assertEquals(1, paths.size());
		assertEquals(1, paths.get(0).size());
		assertSame(hit, Iterables.get(paths.get(0), 0));
	}
	
	@Test
	public void testFindEventPaths2()
	{
		// 3 nodes hit same path
		XmlNode hitL1=mock(XmlNode.class, "L1");
		XmlNode hitL2=mock(XmlNode.class, "L2");
		XmlNode hitL3=mock(XmlNode.class, "L3");
		
		when(hitL1.parentNode()).thenReturn(null);
		when(hitL2.parentNode()).thenReturn(hitL1);
		when(hitL3.parentNode()).thenReturn(hitL2);
		
		List<LinkedHashSet<XmlNode>> paths=Events.findEventPaths(Lists.newArrayList(hitL1, hitL2, hitL3));

		assertEquals(1, paths.size());
		assertEquals(3, paths.get(0).size());
		assertSame(hitL1, Iterables.get(paths.get(0), 0));
		assertSame(hitL2, Iterables.get(paths.get(0), 1));
		assertSame(hitL3, Iterables.get(paths.get(0), 2));
	}
	
	@Test
	public void testFindEventPaths2_DiffOrder()
	{
		// 3 nodes hit same path
		XmlNode hitL1=mock(XmlNode.class, "L1");
		XmlNode hitL2=mock(XmlNode.class, "L2");
		XmlNode hitL3=mock(XmlNode.class, "L3");
		
		when(hitL1.parentNode()).thenReturn(null);
		when(hitL2.parentNode()).thenReturn(hitL1);
		when(hitL3.parentNode()).thenReturn(hitL2);
		
		List<LinkedHashSet<XmlNode>> paths=Events.findEventPaths(Lists.newArrayList(hitL1, hitL3, hitL2));

		assertEquals(1, paths.size());
		assertEquals(3, paths.get(0).size());
		assertSame(hitL1, Iterables.get(paths.get(0), 0));
		assertSame(hitL2, Iterables.get(paths.get(0), 1));
		assertSame(hitL3, Iterables.get(paths.get(0), 2));
	}
	
	@Test
	public void testFindEventsDifferentPaths()
	{
		// A->B->C->D
		// A->B->C->E
		// A->X->Y->Z
		XmlNode a=mock(XmlNode.class, "A");
		XmlNode b=mock(XmlNode.class, "B");
		XmlNode c=mock(XmlNode.class, "C");
		XmlNode d=mock(XmlNode.class, "D");
		XmlNode e=mock(XmlNode.class, "E");
		XmlNode x=mock(XmlNode.class, "X");
		XmlNode y=mock(XmlNode.class, "Y");
		XmlNode z=mock(XmlNode.class, "Z");
		
		when(a.parentNode()).thenReturn(null);
		when(b.parentNode()).thenReturn(a);
		when(c.parentNode()).thenReturn(b);
		when(d.parentNode()).thenReturn(c);
		when(e.parentNode()).thenReturn(c);
		when(x.parentNode()).thenReturn(a);
		when(y.parentNode()).thenReturn(x);
		when(z.parentNode()).thenReturn(y);
		
		List<LinkedHashSet<XmlNode>> paths=Events.findEventPaths(Lists.newArrayList(a,b,c,d,e,x,y,z));

		assertEquals(3, paths.size());
		assertEquals(4, paths.get(0).size());
		assertEquals(4, paths.get(1).size());
		assertEquals(4, paths.get(2).size());
		
		assertSame(a, Iterables.get(paths.get(0), 0));
		assertSame(b, Iterables.get(paths.get(0), 1));
		assertSame(c, Iterables.get(paths.get(0), 2));		
		assertSame(d, Iterables.get(paths.get(0), 3));		

		assertSame(a, Iterables.get(paths.get(1), 0));
		assertSame(b, Iterables.get(paths.get(1), 1));
		assertSame(c, Iterables.get(paths.get(1), 2));		
		assertSame(e, Iterables.get(paths.get(1), 3));		

		assertSame(a, Iterables.get(paths.get(2), 0));
		assertSame(x, Iterables.get(paths.get(2), 1));
		assertSame(y, Iterables.get(paths.get(2), 2));		
		assertSame(z, Iterables.get(paths.get(2), 3));		
		
	}
}


