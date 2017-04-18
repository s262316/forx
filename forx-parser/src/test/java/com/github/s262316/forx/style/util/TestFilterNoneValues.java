package com.github.s262316.forx.style.util;

import static org.junit.Assert.assertEquals;

import com.github.s262316.forx.style.selectors.util.FilterNoneValues;
import org.junit.Test;

import com.github.s262316.forx.style.Identifier;
import com.github.s262316.forx.style.StringValue;

public class TestFilterNoneValues
{
	@Test
	public void testExistsIsNone()
	{
		FilterNoneValues fnv=new FilterNoneValues();
		assertEquals(false, fnv.apply(new Identifier("none")));
	}
	
	@Test
	public void testExistsIsNotNone()
	{
		FilterNoneValues fnv=new FilterNoneValues();
		assertEquals(true, fnv.apply(new Identifier("notnone")));
	}
	
	@Test
	public void testDoesNotExist()
	{
		FilterNoneValues fnv=new FilterNoneValues();
		assertEquals(true, fnv.apply(new StringValue("zzzzz")));
	}
}


