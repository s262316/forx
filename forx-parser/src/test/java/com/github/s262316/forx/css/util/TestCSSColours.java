package com.github.s262316.forx.css.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.awt.Color;

import org.junit.Assert;
import org.junit.Test;

import com.github.s262316.forx.style.HashValue;
import com.github.s262316.forx.style.Identifier;
import com.github.s262316.forx.style.NumericValue;

public class TestCSSColours
{
	@Test
	public void testColourNameToColorValid()
	{
		Assert.assertEquals(new Color(255, 0, 0), CSSColours.colourNameToColor(new Identifier("red")));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testColourNameToColorNotAColour()
	{
		CSSColours.colourNameToColor(new Identifier("zzzzzz"));
		Assert.fail();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testColourNameToColorNotAnIdent()
	{
		CSSColours.colourNameToColor(new NumericValue(1, "px"));
		Assert.fail();
	}
	
	@Test
	public void testHashValueToColor()
	{
		Assert.assertEquals(new Color(255, 0, 0), CSSColours.hashValueToColor(new HashValue("ff0000")));
		Assert.assertEquals(new Color(0, 255, 0), CSSColours.hashValueToColor(new HashValue("00ff00")));
		Assert.assertEquals(new Color(0, 0, 255), CSSColours.hashValueToColor(new HashValue("0000ff")));
		Assert.assertEquals(new Color(255, 0, 0), CSSColours.hashValueToColor(new HashValue("f00")));
		Assert.assertEquals(new Color(0, 255, 0), CSSColours.hashValueToColor(new HashValue("0f0")));
		Assert.assertEquals(new Color(0, 0, 255), CSSColours.hashValueToColor(new HashValue("00f")));
	}

	@Test
	public void testHashValueToColorOutOfRange()
	{
		Assert.assertEquals(new Color(255, 0, 0), CSSColours.hashValueToColor(new HashValue("gg0000")));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testColourNameToColorNotAHash()
	{
		CSSColours.hashValueToColor(new NumericValue(1, "px"));
		Assert.fail();
	}
	
	@Test
	public void testRgbFunctionToColor()
	{
		// TODO
		Assert.fail();
	}
}
