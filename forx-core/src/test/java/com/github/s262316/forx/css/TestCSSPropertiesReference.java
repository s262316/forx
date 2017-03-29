package com.github.s262316.forx.css;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.github.s262316.forx.tree.style.Declaration;
import com.github.s262316.forx.tree.style.Identifier;
import com.github.s262316.forx.tree.style.Value;
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

	@Test
	public void validateFailsWithUnknownProperty()
	{
		Declaration dec=new Declaration("unknown property", new Identifier("fffff"), false);

		assertFalse(ref.validate(dec));
	}

}
