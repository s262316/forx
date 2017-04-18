package com.github.s262316.forx.css;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.github.s262316.forx.style.Declaration;
import com.github.s262316.forx.style.Identifier;
import org.junit.Assert;
import org.junit.Test;

public class TestCSSPropertiesReference
{
	CSSPropertiesReference ref=new CSSPropertiesReference();
	
	@Test
	public void testGetPropertyDescriptor()
	{
		Assert.assertNotNull(ref.getPropertyDescriptor("display"));
		Assert.assertNull(ref.getPropertyDescriptor("zzzzz"));
	}
	
	@Test
	public void testGetShorthandPropertyDescriptor()
	{
		Assert.assertNotNull(ref.getPropertyDescriptor("border"));
		Assert.assertNull(ref.getPropertyDescriptor("zzzzz"));
	}

	@Test
	public void validateFailsWithUnknownProperty()
	{
		Declaration dec=new Declaration("unknown property", new Identifier("fffff"), false);

		Assert.assertFalse(ref.validate(dec));
	}

}
