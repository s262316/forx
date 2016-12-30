package com.github.s262316.forx.css;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestCssParserUtils
{
	@Test
	public void testIsIdentifier()
	{
		assertTrue(CssParserUtils.isIdentifier("ident"));
		assertTrue(CssParserUtils.isIdentifier("ident1"));
		assertTrue(CssParserUtils.isIdentifier("ident_1"));
		assertTrue(CssParserUtils.isIdentifier("ident_x"));
		assertTrue(CssParserUtils.isIdentifier("-ident"));
		assertTrue(CssParserUtils.isIdentifier("_ident"));
		// TODO test escape and nonascii rules
		
		assertFalse(CssParserUtils.isIdentifier("1ident"));
	}
}
