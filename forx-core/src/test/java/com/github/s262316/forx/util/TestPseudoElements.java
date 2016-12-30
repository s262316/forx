package com.github.s262316.forx.util;

import static org.junit.Assert.*;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

public class TestPseudoElements
{
	@Test
	public void testSimple()
	{
		assertEquals(Pair.of("d", "inosaur"), PseudoElements.firstLetter("dinosaur"));
		assertEquals(Pair.of("\"d\"", "inosaur"), PseudoElements.firstLetter("\"d\"inosaur"));
		assertEquals(Pair.of("\"d", "inosaur"), PseudoElements.firstLetter("\"dinosaur"));
		assertEquals(Pair.of("d\"", "inosaur"), PseudoElements.firstLetter("d\"inosaur"));
	}
	
	@Test
	public void testMultipleQuotes()
	{
		assertEquals(Pair.of("''d''", "inosaur"), PseudoElements.firstLetter("''d''inosaur"));
		assertEquals(Pair.of("''d", "inosaur"), PseudoElements.firstLetter("''dinosaur"));
		assertEquals(Pair.of("d''", "inosaur"), PseudoElements.firstLetter("d''inosaur"));
	}
	
	@Test
	public void testShortWords()
	{
		assertEquals(Pair.of("d", ""), PseudoElements.firstLetter("d"));
		assertEquals(Pair.of("'d'", ""), PseudoElements.firstLetter("'d'"));
		assertEquals(Pair.of("d'", ""), PseudoElements.firstLetter("d'"));
		assertEquals(Pair.of("'d", ""), PseudoElements.firstLetter("'d"));
	}
	
	@Test
	public void testOdd()
	{
		assertEquals(Pair.of("", "'"), PseudoElements.firstLetter("'"));
		assertEquals(Pair.of("", "''"), PseudoElements.firstLetter("''"));
		assertEquals(Pair.of("", ""), PseudoElements.firstLetter(""));
		
	}
	
	@Test
	public void testUtf16SurrogatePairs()
	{
		assertEquals(Pair.of("d", "inosaur"), PseudoElements.firstLetter("dinosaur"));
		assertEquals(Pair.of("\uD800\uDD01d\uD800\uDD01", "inosaur"), PseudoElements.firstLetter("\uD800\uDD01d\uD800\uDD01inosaur"));
		assertEquals(Pair.of("\uD800\uDD01d", "inosaur"), PseudoElements.firstLetter("\uD800\uDD01dinosaur"));
		assertEquals(Pair.of("d\uD800\uDD01", "inosaur"), PseudoElements.firstLetter("d\uD800\uDD01inosaur"));		
	}	
	
}
