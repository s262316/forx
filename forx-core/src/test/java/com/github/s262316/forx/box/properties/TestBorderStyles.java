package com.github.s262316.forx.box.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.util.List;

import com.github.s262316.forx.box.properties.BorderStylesImpl;
import com.github.s262316.forx.style.Declaration;
import com.github.s262316.forx.style.FunctionValue;
import com.github.s262316.forx.style.HashValue;
import com.github.s262316.forx.style.Identifier;
import com.github.s262316.forx.style.MediaType;
import com.github.s262316.forx.style.NumericValue;
import com.github.s262316.forx.style.StringValue;
import com.github.s262316.forx.style.selectors.util.ValuesHelper;
import com.google.common.collect.Ordering;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.github.s262316.forx.box.properties.BorderDescriptor;
import com.github.s262316.forx.box.util.BorderStyle;
import com.github.s262316.forx.style.selectors.PseudoElementType;
import com.github.s262316.forx.tree.visual.VElement;

public class TestBorderStyles
{
	BorderStylesImpl borderStyles=new BorderStylesImpl();
	
	@Test
	public void testResolveBorders()
	{
		VElement velement=Mockito.mock(VElement.class);
				
		Mockito.when(velement.getPropertyValue(Matchers.eq("border-top-style"), Matchers.any(MediaType.class), Matchers.any(PseudoElementType.class))).thenReturn(new Identifier("outset"));
		Mockito.when(velement.getPropertyValue(Matchers.eq("border-bottom-style"), Matchers.any(MediaType.class), Matchers.any(PseudoElementType.class))).thenReturn(new Identifier("groove"));
		Mockito.when(velement.getPropertyValue(Matchers.eq("border-left-style"), Matchers.any(MediaType.class), Matchers.any(PseudoElementType.class))).thenReturn(new Identifier("dashed"));
		Mockito.when(velement.getPropertyValue(Matchers.eq("border-right-style"), Matchers.any(MediaType.class), Matchers.any(PseudoElementType.class))).thenReturn(new Identifier("dotted"));
		
		Mockito.when(velement.getPropertyValue(Matchers.eq("border-top-width"), Matchers.any(MediaType.class), Matchers.any(PseudoElementType.class))).thenReturn(new NumericValue(1, "px"));
		Mockito.when(velement.getPropertyValue(Matchers.eq("border-bottom-width"), Matchers.any(MediaType.class), Matchers.any(PseudoElementType.class))).thenReturn(new NumericValue(2, "px"));
		Mockito.when(velement.getPropertyValue(Matchers.eq("border-left-width"), Matchers.any(MediaType.class), Matchers.any(PseudoElementType.class))).thenReturn(new NumericValue(3, "px"));
		Mockito.when(velement.getPropertyValue(Matchers.eq("border-right-width"), Matchers.any(MediaType.class), Matchers.any(PseudoElementType.class))).thenReturn(new Identifier("thick"));
		
		Mockito.when(velement.getPropertyValue(Matchers.eq("border-top-color"), Matchers.any(MediaType.class), Matchers.any(PseudoElementType.class))).thenReturn(new Identifier("red"));
		Mockito.when(velement.getPropertyValue(Matchers.eq("border-bottom-color"), Matchers.any(MediaType.class), Matchers.any(PseudoElementType.class))).thenReturn(new HashValue("ff0000"));
		Mockito.when(velement.getPropertyValue(Matchers.eq("border-left-color"), Matchers.any(MediaType.class), Matchers.any(PseudoElementType.class))).thenReturn(new HashValue("ff0000"));
		Mockito.when(velement.getPropertyValue(Matchers.eq("border-right-color"), Matchers.any(MediaType.class), Matchers.any(PseudoElementType.class))).thenReturn(new FunctionValue("rgb", ValuesHelper.newValueList(new NumericValue(255, ""), new NumericValue(0, ""), new NumericValue(0, ""))));

		Mockito.when(velement.getPropertyValue(Matchers.eq("padding-top"), Matchers.any(MediaType.class), Matchers.any(PseudoElementType.class))).thenReturn(new NumericValue(1, "px"));
		Mockito.when(velement.getPropertyValue(Matchers.eq("padding-bottom"), Matchers.any(MediaType.class), Matchers.any(PseudoElementType.class))).thenReturn(new NumericValue(2, "px"));
		Mockito.when(velement.getPropertyValue(Matchers.eq("padding-left"), Matchers.any(MediaType.class), Matchers.any(PseudoElementType.class))).thenReturn(new NumericValue(3, "px"));
		Mockito.when(velement.getPropertyValue(Matchers.eq("padding-right"), Matchers.any(MediaType.class), Matchers.any(PseudoElementType.class))).thenReturn(new NumericValue(4, "px"));
		
		BorderDescriptor desc=new BorderDescriptor();
		BorderStylesImpl.resolveBorders(null, velement, desc, PseudoElementType.PE_NOT_PSEUDO);
		
		Assert.assertEquals(BorderStyle.BS_OUTSET, desc.borderTopStyle);
		Assert.assertEquals(BorderStyle.BS_GROOVE, desc.borderBottomStyle);
		Assert.assertEquals(BorderStyle.BS_DASHED, desc.borderLeftStyle);
		Assert.assertEquals(BorderStyle.BS_DOTTED, desc.borderRightStyle);

		Assert.assertEquals(1, desc.borderTopWidth);
		Assert.assertEquals(2, desc.borderBottomWidth);
		Assert.assertEquals(3, desc.borderLeftWidth);
		Assert.assertEquals(8, desc.borderRightWidth);

		Assert.assertEquals(new Color(255, 0, 0), desc.borderTopColour);
		Assert.assertEquals(new Color(255, 0, 0), desc.borderBottomColour);
		Assert.assertEquals(new Color(255, 0, 0), desc.borderLeftColour);
		Assert.assertEquals(new Color(255, 0, 0), desc.borderRightColour);
		
		Assert.assertEquals(1, desc.paddingTopWidth);
		Assert.assertEquals(2, desc.paddingBottomWidth);
		Assert.assertEquals(3, desc.paddingLeftWidth);
		Assert.assertEquals(4, desc.paddingRightWidth);
	}

	@Test
	public void testBorderWidthToInt1()
	{
		Assert.assertEquals(1, BorderStylesImpl.borderWidthToInt(new Identifier("thin")));
		Assert.assertEquals(4, BorderStylesImpl.borderWidthToInt(new Identifier("medium")));
		Assert.assertEquals(8,  BorderStylesImpl.borderWidthToInt(new Identifier("thick")));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testBorderWidthToInt2()
	{
		BorderStylesImpl.borderWidthToInt(new Identifier("qqqqq"));
		Assert.fail();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testBorderWidthToInt3()
	{
		BorderStylesImpl.borderWidthToInt(new NumericValue(1, "px"));
		Assert.fail();
	}	
	
	class DeclarationPropertyOrder extends Ordering<Declaration>
	{
		@Override
		public int compare(Declaration arg0, Declaration arg1)
		{
			return arg0.getProperty().compareTo(arg1.getProperty());
		}
	}
	
	/*
	 * each property has 1 possible match
	 * 
	      | width | colour | style
	red   |   0   |   1    |   0
	solid |   0   |   0    |   1
	thin  |   1   |   0    |   0
	*/	
	@Test
	public void testExpandBorders1()
	{
		Declaration d1=new Declaration("border", ValuesHelper.newValueList(new Identifier("thin"), new Identifier("red"), new Identifier("solid")), false);
		List<Declaration> decs1=new DeclarationPropertyOrder().sortedCopy(borderStyles.expandBorder(d1));
		Assert.assertEquals(12, decs1.size());
		Assert.assertEquals(new Declaration("border-bottom-color", new Identifier("red"), false), decs1.get(0));
		Assert.assertEquals(new Declaration("border-bottom-style", new Identifier("solid"), false), decs1.get(1));
		Assert.assertEquals(new Declaration("border-bottom-width", new Identifier("thin"), false), decs1.get(2));
		Assert.assertEquals(new Declaration("border-left-color", new Identifier("red"), false), decs1.get(3));
		Assert.assertEquals(new Declaration("border-left-style", new Identifier("solid"), false), decs1.get(4));
		Assert.assertEquals(new Declaration("border-left-width", new Identifier("thin"), false), decs1.get(5));
		Assert.assertEquals(new Declaration("border-right-color", new Identifier("red"), false), decs1.get(6));
		Assert.assertEquals(new Declaration("border-right-style", new Identifier("solid"), false), decs1.get(7));
		Assert.assertEquals(new Declaration("border-right-width", new Identifier("thin"), false), decs1.get(8));
		Assert.assertEquals(new Declaration("border-top-color", new Identifier("red"), false), decs1.get(9));
		Assert.assertEquals(new Declaration("border-top-style", new Identifier("solid"), false), decs1.get(10));
		Assert.assertEquals(new Declaration("border-top-width", new Identifier("thin"), false), decs1.get(11));
		
		Declaration d2=new Declaration("border", ValuesHelper.newValueList(new Identifier("thin"), new Identifier("red")), false);
		List<Declaration> decs2=new DeclarationPropertyOrder().sortedCopy(borderStyles.expandBorder(d2));
		Assert.assertEquals(8, decs2.size());
		Assert.assertEquals(new Declaration("border-bottom-color", new Identifier("red"), false), decs2.get(0));
		Assert.assertEquals(new Declaration("border-bottom-width", new Identifier("thin"), false), decs2.get(1));
		Assert.assertEquals(new Declaration("border-left-color", new Identifier("red"), false), decs2.get(2));
		Assert.assertEquals(new Declaration("border-left-width", new Identifier("thin"), false), decs2.get(3));
		Assert.assertEquals(new Declaration("border-right-color", new Identifier("red"), false), decs2.get(4));
		Assert.assertEquals(new Declaration("border-right-width", new Identifier("thin"), false), decs2.get(5));
		Assert.assertEquals(new Declaration("border-top-color", new Identifier("red"), false), decs2.get(6));
		Assert.assertEquals(new Declaration("border-top-width", new Identifier("thin"), false), decs2.get(7));
		
		Declaration d3=new Declaration("border", ValuesHelper.newValueList(new Identifier("wrong"), new Identifier("red")), false);
		List<Declaration> decs3=new DeclarationPropertyOrder().sortedCopy(borderStyles.expandBorder(d3));
		Assert.assertEquals(0, decs3.size());
	}
	
	/*
	 * colour has 3 possible matches
	 * 
	      | width | colour | style
	red   |   0   |   1    |   0
	solid |   0   |   1    |   1
	thin  |   1   |   1    |   0
	*/
	@Test
	public void testExpandBorders2()
	{
		Declaration d1=new Declaration("border", ValuesHelper.newValueList(new Identifier("thin"), new Identifier("red"), new Identifier("solid")), false);
		List<Declaration> decs1=new DeclarationPropertyOrder().sortedCopy(borderStyles.expandBorder(d1));
		Assert.assertEquals(12, decs1.size());
		Assert.assertEquals(new Declaration("border-bottom-color", new Identifier("red"), false), decs1.get(0));
		Assert.assertEquals(new Declaration("border-bottom-style", new Identifier("solid"), false), decs1.get(1));
		Assert.assertEquals(new Declaration("border-bottom-width", new Identifier("thin"), false), decs1.get(2));
		Assert.assertEquals(new Declaration("border-left-color", new Identifier("red"), false), decs1.get(3));
		Assert.assertEquals(new Declaration("border-left-style", new Identifier("solid"), false), decs1.get(4));
		Assert.assertEquals(new Declaration("border-left-width", new Identifier("thin"), false), decs1.get(5));
		Assert.assertEquals(new Declaration("border-right-color", new Identifier("red"), false), decs1.get(6));
		Assert.assertEquals(new Declaration("border-right-style", new Identifier("solid"), false), decs1.get(7));
		Assert.assertEquals(new Declaration("border-right-width", new Identifier("thin"), false), decs1.get(8));
		Assert.assertEquals(new Declaration("border-top-color", new Identifier("red"), false), decs1.get(9));
		Assert.assertEquals(new Declaration("border-top-style", new Identifier("solid"), false), decs1.get(10));
		Assert.assertEquals(new Declaration("border-top-width", new Identifier("thin"), false), decs1.get(11));
	}	
	
	@Test
	public void validateBorderOneStyle()
	{
		Assert.assertTrue(borderStyles.validateBorderOneStyle(new Identifier("solid")));
		Assert.assertFalse(borderStyles.validateBorderOneStyle(new Identifier("red")));
		Assert.assertFalse(borderStyles.validateBorderOneStyle(new NumericValue(1, "px")));
	}
	
	@Test
	public void validateBorderOneWidth()
	{
		Assert.assertTrue(borderStyles.validateBorderOneWidth(new Identifier("thick")));
		Assert.assertTrue(borderStyles.validateBorderOneWidth(new NumericValue(1, "px")));
		Assert.assertFalse(borderStyles.validateBorderOneWidth(new Identifier("red")));
		Assert.assertFalse(borderStyles.validateBorderOneWidth(new StringValue("red")));
	}
	
	@Test
	public void validateBorderOneColor()
	{
		Assert.assertTrue(borderStyles.validateBorderOneColor(new Identifier("red")));
		Assert.assertFalse(borderStyles.validateBorderOneColor(new Identifier("zzzz")));
		Assert.assertFalse(borderStyles.validateBorderOneColor(new StringValue("red")));
		Assert.assertTrue(borderStyles.validateBorderOneColor(new HashValue("ff0000")));
		Assert.assertTrue(borderStyles.validateBorderOneColor(new HashValue("f00")));
		Assert.assertFalse(borderStyles.validateBorderOneColor(new HashValue("sdfsdfgf")));
		Assert.assertTrue(borderStyles.validateBorderOneColor(new FunctionValue("rgb", ValuesHelper.newValueList(new NumericValue(255, ""), new NumericValue(0, ""), new NumericValue(0, "")))));
	}
	
	@Test
	public void validatePaddingOne()
	{
		Assert.assertTrue(borderStyles.validatePaddingOne(new NumericValue(1, "px")));
		Assert.assertFalse(borderStyles.validatePaddingOne(new Identifier("thick")));
		Assert.assertFalse(borderStyles.validatePaddingOne(new StringValue("red")));
	}
}

