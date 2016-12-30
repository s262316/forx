package com.github.s262316.forx.css.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.awt.Color;

import org.junit.Test;

import com.github.s262316.forx.tree.style.HashValue;
import com.github.s262316.forx.tree.style.Identifier;
import com.github.s262316.forx.tree.style.NumericValue;

public class TestCSSColours
{
	@Test
	public void testColourNameToColorValid()
	{
		assertEquals(new Color(255, 0, 0), CSSColours.colourNameToColor(new Identifier("red")));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testColourNameToColorNotAColour()
	{
		CSSColours.colourNameToColor(new Identifier("zzzzzz"));
		fail();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testColourNameToColorNotAnIdent()
	{
		CSSColours.colourNameToColor(new NumericValue(1, "px"));
		fail();
	}
	
	@Test
	public void testHashValueToColor()
	{
		assertEquals(new Color(255, 0, 0), CSSColours.hashValueToColor(new HashValue("ff0000")));		
		assertEquals(new Color(0, 255, 0), CSSColours.hashValueToColor(new HashValue("00ff00")));		
		assertEquals(new Color(0, 0, 255), CSSColours.hashValueToColor(new HashValue("0000ff")));
		assertEquals(new Color(255, 0, 0), CSSColours.hashValueToColor(new HashValue("f00")));		
		assertEquals(new Color(0, 255, 0), CSSColours.hashValueToColor(new HashValue("0f0")));		
		assertEquals(new Color(0, 0, 255), CSSColours.hashValueToColor(new HashValue("00f")));
	}

	@Test
	public void testHashValueToColorOutOfRange()
	{
		assertEquals(new Color(255, 0, 0), CSSColours.hashValueToColor(new HashValue("gg0000")));	
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testColourNameToColorNotAHash()
	{
		CSSColours.hashValueToColor(new NumericValue(1, "px"));
		fail();
	}
	
	@Test
	public void testRgbFunctionToColor()
	{
		// TODO
		fail();
	}
}
