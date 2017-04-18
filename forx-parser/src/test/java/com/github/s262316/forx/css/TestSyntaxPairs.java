package com.github.s262316.forx.css;

import static org.junit.Assert.*;

import org.junit.Assert;

import org.hamcrest.Matchers;
import org.junit.Test;

public class TestSyntaxPairs
{
	@Test
	public void allOpeningPairs()
	{
		SyntaxPairs pairs=new SyntaxPairs();
		
		Assert.assertThat(
				pairs.openPairs(),
				Matchers.containsInAnyOrder("{", "[", "("));
	}
	
	@Test
	public void allClosingPairs()
	{
		SyntaxPairs pairs=new SyntaxPairs();
		
		Assert.assertThat(
				pairs.closedPairs(),
				Matchers.containsInAnyOrder("}", "]", ")"));
	}	
	
	@Test
	public void testOpposites()
	{
		SyntaxPairs pairs=new SyntaxPairs();
		Assert.assertEquals("[", pairs.oppositeOf("]"));
		Assert.assertEquals("]", pairs.oppositeOf("["));
		Assert.assertEquals("(", pairs.oppositeOf(")"));
		Assert.assertEquals(")", pairs.oppositeOf("("));
		Assert.assertEquals("{", pairs.oppositeOf("}"));
		Assert.assertEquals("}", pairs.oppositeOf("{"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNoOpposite()
	{
		SyntaxPairs pairs=new SyntaxPairs();
		pairs.oppositeOf("A");
	}
}

