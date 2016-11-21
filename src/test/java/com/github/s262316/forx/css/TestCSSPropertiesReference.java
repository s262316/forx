package com.github.s262316.forx.css;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class TestCSSPropertiesReference
{
	CSSPropertiesReference ref=new CSSPropertiesReference();
	
	@Test
	public void testGetPropertyDescriptor()
	{
		assertNotNull(ref.getPropertyDescriptor("display"));
		assertNull(ref.getPropertyDescriptor("zzzzz"));
	}
	
	@Test
	public void testGetShorthandPropertyDescriptor()
	{
		assertNotNull(ref.getPropertyDescriptor("border"));
		assertNull(ref.getPropertyDescriptor("zzzzz"));
	}

}
