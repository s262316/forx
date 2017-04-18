package com.github.s262316.forx.style.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.github.s262316.forx.style.selectors.util.ValuesHelper;
import org.junit.Test;
import com.github.s262316.forx.style.Identifier;
import com.github.s262316.forx.style.NumericValue;
import com.github.s262316.forx.style.StringValue;
import com.github.s262316.forx.style.ValueList;


public class TestValuesHelper
{
	@Test
	public void testGetIdentifier()
	{
		Identifier ident=new Identifier("myident");
		assertEquals(ident.ident, ValuesHelper.getIdentifier(ident).get());
		
		StringValue strValue=new StringValue("myident");
		assertEquals(false, ValuesHelper.getIdentifier(strValue).isPresent());
	}
	
	@Test
	public void testGetInt()
	{
		NumericValue num1=new NumericValue(3, "");
		assertEquals(num1.amount, ValuesHelper.getInt(num1).get().intValue());

		NumericValue num2=new NumericValue(3.3, "");
		assertEquals(num2.amount, ValuesHelper.getInt(num2).get().intValue());
		
		StringValue strValue=new StringValue("myident");
		assertEquals(false, ValuesHelper.getInt(strValue).isPresent());		
	}
	
	@Test
	public void testAsValueList()
	{
		ValueList vl=new ValueList();
		assertSame(vl, ValuesHelper.asValueList(vl));

		StringValue sv=new StringValue("aaa");
		ValueList vl2=ValuesHelper.asValueList(sv);
		assertEquals(1, vl2.members.size());
		assertEquals(sv, vl2.members.get(0));
	}
}


