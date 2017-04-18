package com.github.s262316.forx.css;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

public class TestSkipPastSemicolonOrBlock
{
	@Test
	public void testSkipSimpleImport()
	{
		Tokenizer tokenizer=new Tokenizer("import \"file.com.github.s262316.forx.css\"; div");
		tokenizer.advance();
		
		SkipPastSemicolonOrBlock skipper=new SkipPastSemicolonOrBlock();

		// on "file.com.github.s262316.forx.css"
		Assert.assertFalse(skipper.test(tokenizer));
		Assert.assertEquals("file.com.github.s262316.forx.css", tokenizer.curr.syntax);
		// on ";"
		Assert.assertFalse(skipper.test(tokenizer));
		Assert.assertEquals(";", tokenizer.curr.syntax);
		// on div
		Assert.assertTrue(skipper.test(tokenizer));
		Assert.assertEquals("div", tokenizer.curr.syntax);
	}
	
	@Test
	public void testSkipImportsWithMediaTypes()
	{
		Tokenizer tokenizer=new Tokenizer("import \"file.com.github.s262316.forx.css\" print all; div");
		tokenizer.advance();
		
		SkipPastSemicolonOrBlock skipper=new SkipPastSemicolonOrBlock();

		// on "file.com.github.s262316.forx.css"
		Assert.assertFalse(skipper.test(tokenizer));
		Assert.assertEquals("file.com.github.s262316.forx.css", tokenizer.curr.syntax);
		// on print
		Assert.assertFalse(skipper.test(tokenizer));
		Assert.assertEquals("print", tokenizer.curr.syntax);
		// on all
		Assert.assertFalse(skipper.test(tokenizer));
		Assert.assertEquals("all", tokenizer.curr.syntax);
		// on ";"
		Assert.assertFalse(skipper.test(tokenizer));
		Assert.assertEquals(";", tokenizer.curr.syntax);
		// on div
		Assert.assertTrue(skipper.test(tokenizer));
		Assert.assertEquals("div", tokenizer.curr.syntax);
	}
	
	@Test
	public void testSimpleMediaRule()
	{
		Tokenizer tokenizer=new Tokenizer("media print {}  div");
		tokenizer.advance();
		
		SkipPastSemicolonOrBlock skipper=new SkipPastSemicolonOrBlock();

		// on print
		Assert.assertFalse(skipper.test(tokenizer));
		Assert.assertEquals("print", tokenizer.curr.syntax);
		// on {
		Assert.assertFalse(skipper.test(tokenizer));
		Assert.assertEquals("{", tokenizer.curr.syntax);
		// on }
		Assert.assertFalse(skipper.test(tokenizer));
		Assert.assertEquals("}", tokenizer.curr.syntax);
		// on div
		Assert.assertTrue(skipper.test(tokenizer));
		Assert.assertEquals("div", tokenizer.curr.syntax);
	}
	
	@Test
	public void testStyleRulesInsideMediaBlock()
	{
		Tokenizer tokenizer=new Tokenizer("media print {a { color : red } }  div");
		tokenizer.advance();
		
		SkipPastSemicolonOrBlock skipper=new SkipPastSemicolonOrBlock();

		// on print
		Assert.assertFalse(skipper.test(tokenizer));
		Assert.assertEquals("print", tokenizer.curr.syntax);
		// on {
		Assert.assertFalse(skipper.test(tokenizer));
		Assert.assertEquals("{", tokenizer.curr.syntax);
		// on a 
		Assert.assertFalse(skipper.test(tokenizer));
		Assert.assertEquals("a", tokenizer.curr.syntax);
		// on {
		Assert.assertFalse(skipper.test(tokenizer));
		Assert.assertEquals("{", tokenizer.curr.syntax);
		// on color
		Assert.assertFalse(skipper.test(tokenizer));
		Assert.assertEquals("color", tokenizer.curr.syntax);
		// on :
		Assert.assertFalse(skipper.test(tokenizer));
		Assert.assertEquals(":", tokenizer.curr.syntax);
		// on red
		Assert.assertFalse(skipper.test(tokenizer));
		Assert.assertEquals("red", tokenizer.curr.syntax);
		// on }
		Assert.assertFalse(skipper.test(tokenizer));
		Assert.assertEquals("}", tokenizer.curr.syntax);
		// on }
		Assert.assertFalse(skipper.test(tokenizer));
		Assert.assertEquals("}", tokenizer.curr.syntax);
		// on div
		Assert.assertTrue(skipper.test(tokenizer));
		Assert.assertEquals("div", tokenizer.curr.syntax);
	}	

	@Test
	public void testMalformedCharset()
	{
		Tokenizer tokenizer=new Tokenizer("charset \"test\" . stuff { color : red } div");
		tokenizer.advance();
		
		SkipPastSemicolonOrBlock skipper=new SkipPastSemicolonOrBlock();

		// on test
		Assert.assertFalse(skipper.test(tokenizer));
		Assert.assertEquals("test", tokenizer.curr.syntax);
		// on .
		Assert.assertFalse(skipper.test(tokenizer));
		Assert.assertEquals(".", tokenizer.curr.syntax);
		// on stuff
		Assert.assertFalse(skipper.test(tokenizer));
		Assert.assertEquals("stuff", tokenizer.curr.syntax);
		// on {
		Assert.assertFalse(skipper.test(tokenizer));
		Assert.assertEquals("{", tokenizer.curr.syntax);
		// on color
		Assert.assertFalse(skipper.test(tokenizer));
		Assert.assertEquals("color", tokenizer.curr.syntax);
		// on :
		Assert.assertFalse(skipper.test(tokenizer));
		Assert.assertEquals(":", tokenizer.curr.syntax);
		// on red
		Assert.assertFalse(skipper.test(tokenizer));
		Assert.assertEquals("red", tokenizer.curr.syntax);
		// on }
		Assert.assertFalse(skipper.test(tokenizer));
		Assert.assertEquals("}", tokenizer.curr.syntax);
		// on div
		Assert.assertTrue(skipper.test(tokenizer));
		Assert.assertEquals("div", tokenizer.curr.syntax);
	}
}
