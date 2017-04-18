package com.github.s262316.forx.css;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

public class TestCssParserUtils
{
	@Test
	public void testIsIdentifier()
	{
		Assert.assertTrue(CssParserUtils.isIdentifier("ident"));
		Assert.assertTrue(CssParserUtils.isIdentifier("ident1"));
		Assert.assertTrue(CssParserUtils.isIdentifier("ident_1"));
		Assert.assertTrue(CssParserUtils.isIdentifier("ident_x"));
		Assert.assertTrue(CssParserUtils.isIdentifier("-ident"));
		Assert.assertTrue(CssParserUtils.isIdentifier("_ident"));
		// TODO test escape and nonascii rules
		
		Assert.assertFalse(CssParserUtils.isIdentifier("1ident"));
	}
}
