package com.github.s262316.forx.tree.events2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedHashSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.github.s262316.forx.tree.events2.Events;
import com.github.s262316.forx.tree.XmlNode;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.mockito.Mockito;

public class TestEvents
{
	@Test
	public void testFindEventPaths1()
	{
		// one node hit
		XmlNode hit= Mockito.mock(XmlNode.class);
		Mockito.when(hit.parentNode()).thenReturn(null);
		
		List<LinkedHashSet<XmlNode>> paths=Events.findEventPaths(Lists.newArrayList(hit));
		
		Assert.assertEquals(1, paths.size());
		Assert.assertEquals(1, paths.get(0).size());
		Assert.assertSame(hit, Iterables.get(paths.get(0), 0));
	}
	
	@Test
	public void testFindEventPaths2()
	{
		// 3 nodes hit same path
		XmlNode hitL1= Mockito.mock(XmlNode.class, "L1");
		XmlNode hitL2= Mockito.mock(XmlNode.class, "L2");
		XmlNode hitL3= Mockito.mock(XmlNode.class, "L3");
		
		Mockito.when(hitL1.parentNode()).thenReturn(null);
		Mockito.when(hitL2.parentNode()).thenReturn(hitL1);
		Mockito.when(hitL3.parentNode()).thenReturn(hitL2);
		
		List<LinkedHashSet<XmlNode>> paths=Events.findEventPaths(Lists.newArrayList(hitL1, hitL2, hitL3));

		Assert.assertEquals(1, paths.size());
		Assert.assertEquals(3, paths.get(0).size());
		Assert.assertSame(hitL1, Iterables.get(paths.get(0), 0));
		Assert.assertSame(hitL2, Iterables.get(paths.get(0), 1));
		Assert.assertSame(hitL3, Iterables.get(paths.get(0), 2));
	}
	
	@Test
	public void testFindEventPaths2_DiffOrder()
	{
		// 3 nodes hit same path
		XmlNode hitL1= Mockito.mock(XmlNode.class, "L1");
		XmlNode hitL2= Mockito.mock(XmlNode.class, "L2");
		XmlNode hitL3= Mockito.mock(XmlNode.class, "L3");
		
		Mockito.when(hitL1.parentNode()).thenReturn(null);
		Mockito.when(hitL2.parentNode()).thenReturn(hitL1);
		Mockito.when(hitL3.parentNode()).thenReturn(hitL2);
		
		List<LinkedHashSet<XmlNode>> paths=Events.findEventPaths(Lists.newArrayList(hitL1, hitL3, hitL2));

		Assert.assertEquals(1, paths.size());
		Assert.assertEquals(3, paths.get(0).size());
		Assert.assertSame(hitL1, Iterables.get(paths.get(0), 0));
		Assert.assertSame(hitL2, Iterables.get(paths.get(0), 1));
		Assert.assertSame(hitL3, Iterables.get(paths.get(0), 2));
	}
	
	@Test
	public void testFindEventsDifferentPaths()
	{
		// A->B->C->D
		// A->B->C->E
		// A->X->Y->Z
		XmlNode a= Mockito.mock(XmlNode.class, "A");
		XmlNode b= Mockito.mock(XmlNode.class, "B");
		XmlNode c= Mockito.mock(XmlNode.class, "C");
		XmlNode d= Mockito.mock(XmlNode.class, "D");
		XmlNode e= Mockito.mock(XmlNode.class, "E");
		XmlNode x= Mockito.mock(XmlNode.class, "X");
		XmlNode y= Mockito.mock(XmlNode.class, "Y");
		XmlNode z= Mockito.mock(XmlNode.class, "Z");
		
		Mockito.when(a.parentNode()).thenReturn(null);
		Mockito.when(b.parentNode()).thenReturn(a);
		Mockito.when(c.parentNode()).thenReturn(b);
		Mockito.when(d.parentNode()).thenReturn(c);
		Mockito.when(e.parentNode()).thenReturn(c);
		Mockito.when(x.parentNode()).thenReturn(a);
		Mockito.when(y.parentNode()).thenReturn(x);
		Mockito.when(z.parentNode()).thenReturn(y);
		
		List<LinkedHashSet<XmlNode>> paths=Events.findEventPaths(Lists.newArrayList(a,b,c,d,e,x,y,z));

		Assert.assertEquals(3, paths.size());
		Assert.assertEquals(4, paths.get(0).size());
		Assert.assertEquals(4, paths.get(1).size());
		Assert.assertEquals(4, paths.get(2).size());
		
		Assert.assertSame(a, Iterables.get(paths.get(0), 0));
		Assert.assertSame(b, Iterables.get(paths.get(0), 1));
		Assert.assertSame(c, Iterables.get(paths.get(0), 2));
		Assert.assertSame(d, Iterables.get(paths.get(0), 3));

		Assert.assertSame(a, Iterables.get(paths.get(1), 0));
		Assert.assertSame(b, Iterables.get(paths.get(1), 1));
		Assert.assertSame(c, Iterables.get(paths.get(1), 2));
		Assert.assertSame(e, Iterables.get(paths.get(1), 3));

		Assert.assertSame(a, Iterables.get(paths.get(2), 0));
		Assert.assertSame(x, Iterables.get(paths.get(2), 1));
		Assert.assertSame(y, Iterables.get(paths.get(2), 2));
		Assert.assertSame(z, Iterables.get(paths.get(2), 3));
		
	}
}


