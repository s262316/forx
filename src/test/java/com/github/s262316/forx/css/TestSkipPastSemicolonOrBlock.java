package com.github.s262316.forx.css;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestSkipPastSemicolonOrBlock
{
	@Test
	public void testSkipSimpleImport()
	{
		Tokenizer tokenizer=new Tokenizer("import \"file.css\"; div");
		tokenizer.advance();
		
		SkipPastSemicolonOrBlock skipper=new SkipPastSemicolonOrBlock();

		// on "file.css"
		assertFalse(skipper.test(tokenizer));
		assertEquals("file.css", tokenizer.curr.syntax);
		// on ";"
		assertFalse(skipper.test(tokenizer));
		assertEquals(";", tokenizer.curr.syntax);
		// on div
		assertTrue(skipper.test(tokenizer));
		assertEquals("div", tokenizer.curr.syntax);
	}
	
	@Test
	public void testSkipImportsWithMediaTypes()
	{
		Tokenizer tokenizer=new Tokenizer("import \"file.css\" print all; div");
		tokenizer.advance();
		
		SkipPastSemicolonOrBlock skipper=new SkipPastSemicolonOrBlock();

		// on "file.css"
		assertFalse(skipper.test(tokenizer));
		assertEquals("file.css", tokenizer.curr.syntax);
		// on print
		assertFalse(skipper.test(tokenizer));
		assertEquals("print", tokenizer.curr.syntax);
		// on all
		assertFalse(skipper.test(tokenizer));
		assertEquals("all", tokenizer.curr.syntax);
		// on ";"
		assertFalse(skipper.test(tokenizer));
		assertEquals(";", tokenizer.curr.syntax);
		// on div
		assertTrue(skipper.test(tokenizer));
		assertEquals("div", tokenizer.curr.syntax);		
	}
	
	@Test
	public void testSimpleMediaRule()
	{
		Tokenizer tokenizer=new Tokenizer("media print {}  div");
		tokenizer.advance();
		
		SkipPastSemicolonOrBlock skipper=new SkipPastSemicolonOrBlock();

		// on print
		assertFalse(skipper.test(tokenizer));
		assertEquals("print", tokenizer.curr.syntax);
		// on {
		assertFalse(skipper.test(tokenizer));
		assertEquals("{", tokenizer.curr.syntax);
		// on }
		assertFalse(skipper.test(tokenizer));
		assertEquals("}", tokenizer.curr.syntax);
		// on div
		assertTrue(skipper.test(tokenizer));
		assertEquals("div", tokenizer.curr.syntax);		
	}
	
	@Test
	public void testStyleRulesInsideMediaBlock()
	{
		Tokenizer tokenizer=new Tokenizer("media print {a { color : red } }  div");
		tokenizer.advance();
		
		SkipPastSemicolonOrBlock skipper=new SkipPastSemicolonOrBlock();

		// on print
		assertFalse(skipper.test(tokenizer));
		assertEquals("print", tokenizer.curr.syntax);
		// on {
		assertFalse(skipper.test(tokenizer));
		assertEquals("{", tokenizer.curr.syntax);
		// on a 
		assertFalse(skipper.test(tokenizer));
		assertEquals("a", tokenizer.curr.syntax);
		// on {
		assertFalse(skipper.test(tokenizer));
		assertEquals("{", tokenizer.curr.syntax);
		// on color
		assertFalse(skipper.test(tokenizer));
		assertEquals("color", tokenizer.curr.syntax);
		// on :
		assertFalse(skipper.test(tokenizer));
		assertEquals(":", tokenizer.curr.syntax);
		// on red
		assertFalse(skipper.test(tokenizer));
		assertEquals("red", tokenizer.curr.syntax);
		// on }
		assertFalse(skipper.test(tokenizer));
		assertEquals("}", tokenizer.curr.syntax);
		// on }
		assertFalse(skipper.test(tokenizer));
		assertEquals("}", tokenizer.curr.syntax);
		// on div
		assertTrue(skipper.test(tokenizer));
		assertEquals("div", tokenizer.curr.syntax);		
	}	

	@Test
	public void testMalformedCharset()
	{
		Tokenizer tokenizer=new Tokenizer("charset \"test\" . stuff { color : red } div");
		tokenizer.advance();
		
		SkipPastSemicolonOrBlock skipper=new SkipPastSemicolonOrBlock();

		// on test
		assertFalse(skipper.test(tokenizer));
		assertEquals("test", tokenizer.curr.syntax);
		// on .
		assertFalse(skipper.test(tokenizer));
		assertEquals(".", tokenizer.curr.syntax);
		// on stuff
		assertFalse(skipper.test(tokenizer));
		assertEquals("stuff", tokenizer.curr.syntax);
		// on {
		assertFalse(skipper.test(tokenizer));
		assertEquals("{", tokenizer.curr.syntax);
		// on color
		assertFalse(skipper.test(tokenizer));
		assertEquals("color", tokenizer.curr.syntax);
		// on :
		assertFalse(skipper.test(tokenizer));
		assertEquals(":", tokenizer.curr.syntax);
		// on red
		assertFalse(skipper.test(tokenizer));
		assertEquals("red", tokenizer.curr.syntax);
		// on }
		assertFalse(skipper.test(tokenizer));
		assertEquals("}", tokenizer.curr.syntax);
		// on div
		assertTrue(skipper.test(tokenizer));
		assertEquals("div", tokenizer.curr.syntax);
	}
}
