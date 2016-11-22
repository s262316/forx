package com.github.s262316.forx.css;

import static org.junit.Assert.*;

import org.hamcrest.Matchers;
import org.junit.Test;

public class TestSyntaxPairs
{
	@Test
	public void allOpeningPairs()
	{
		SyntaxPairs pairs=new SyntaxPairs();
		
		assertThat(
				pairs.openPairs(),
				Matchers.containsInAnyOrder("{", "[", "("));
	}
	
	@Test
	public void allClosingPairs()
	{
		SyntaxPairs pairs=new SyntaxPairs();
		
		assertThat(
				pairs.closedPairs(),
				Matchers.containsInAnyOrder("}", "]", ")"));
	}	
	
	@Test
	public void testOpposites()
	{
		SyntaxPairs pairs=new SyntaxPairs();
		assertEquals("[", pairs.oppositeOf("]"));
		assertEquals("]", pairs.oppositeOf("["));
		assertEquals("(", pairs.oppositeOf(")"));
		assertEquals(")", pairs.oppositeOf("("));
		assertEquals("{", pairs.oppositeOf("}"));
		assertEquals("}", pairs.oppositeOf("{"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNoOpposite()
	{
		SyntaxPairs pairs=new SyntaxPairs();
		pairs.oppositeOf("A");
	}
}

