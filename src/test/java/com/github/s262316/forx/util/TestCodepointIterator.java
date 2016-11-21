package com.github.s262316.forx.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestCodepointIterator
{
	@Test
	public void test1()
	{
		CodepointIterator it1=new CodepointIterator("\uD800\uDD01");
		assertEquals(0x10101, it1.next().intValue());
		assertFalse(it1.hasNext());
	}

	@Test
	public void test2()
	{
		CodepointIterator it1=new CodepointIterator("a\uD800\uDD01");
		assertEquals('a', it1.next().intValue());
		assertEquals(0x10101, it1.next().intValue());
		assertFalse(it1.hasNext());
	}

	@Test
	public void test3()
	{
		CodepointIterator it1=new CodepointIterator("\uD800\uDD01a");
		assertEquals(0x10101, it1.next().intValue());
		assertEquals('a', it1.next().intValue());
		assertFalse(it1.hasNext());
	}

	@Test
	public void test4()
	{
		CodepointIterator it1=new CodepointIterator("a\uD800\uDD01a");
		assertEquals('a', it1.next().intValue());
		assertEquals(0x10101, it1.next().intValue());
		assertEquals('a', it1.next().intValue());
		assertFalse(it1.hasNext());
	}

	@Test
	public void test5()
	{
		CodepointIterator it1=new CodepointIterator("aa");
		assertEquals('a', it1.next().intValue());
		assertEquals('a', it1.next().intValue());
		assertFalse(it1.hasNext());
	}
	
	
	
}
