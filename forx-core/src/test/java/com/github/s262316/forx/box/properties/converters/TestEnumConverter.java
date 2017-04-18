package com.github.s262316.forx.box.properties.converters;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.s262316.forx.box.util.BorderStyle;
import com.github.s262316.forx.style.Identifier;

public class TestEnumConverter
{
	enum TestNotCaps
	{
		test1,test2
	}
	
	@Test
	public void testEnumMapper1()
	{
		EnumConverter<BorderStyle> converter=new EnumConverter<>("BS_", true, BorderStyle.class);
		assertEquals(BorderStyle.BS_SOLID, converter.convert(new Identifier("solid")));
	}
	
	@Test
	public void testEnumMapper2()
	{
		EnumConverter<TestNotCaps> converter=new EnumConverter<>("", false, TestNotCaps.class);
		assertEquals(TestNotCaps.test1, converter.convert(new Identifier("test1")));
	}
}
