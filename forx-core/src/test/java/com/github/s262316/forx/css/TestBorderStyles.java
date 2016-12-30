package com.github.s262316.forx.css;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Ordering;
import com.github.s262316.forx.box.properties.BorderDescriptor;
import com.github.s262316.forx.box.util.BorderStyle;
import com.github.s262316.forx.tree.style.*;
import com.github.s262316.forx.tree.style.selectors.PseudoElementType;
import com.github.s262316.forx.tree.style.util.ValuesHelper;
import com.github.s262316.forx.tree.visual.VElement;

public class TestBorderStyles
{
	BorderStyles borderStyles=new BorderStyles();
	
	@Test
	public void testResolveBorders()
	{
		VElement velement=Mockito.mock(VElement.class);
				
		when(velement.getPropertyValue(eq("border-top-style"), any(MediaType.class), any(PseudoElementType.class))).thenReturn(new Identifier("outset"));
		when(velement.getPropertyValue(eq("border-bottom-style"), any(MediaType.class), any(PseudoElementType.class))).thenReturn(new Identifier("groove"));
		when(velement.getPropertyValue(eq("border-left-style"), any(MediaType.class), any(PseudoElementType.class))).thenReturn(new Identifier("dashed"));
		when(velement.getPropertyValue(eq("border-right-style"), any(MediaType.class), any(PseudoElementType.class))).thenReturn(new Identifier("dotted"));
		
		when(velement.getPropertyValue(eq("border-top-width"), any(MediaType.class), any(PseudoElementType.class))).thenReturn(new NumericValue(1, "px"));
		when(velement.getPropertyValue(eq("border-bottom-width"), any(MediaType.class), any(PseudoElementType.class))).thenReturn(new NumericValue(2, "px"));
		when(velement.getPropertyValue(eq("border-left-width"), any(MediaType.class), any(PseudoElementType.class))).thenReturn(new NumericValue(3, "px"));
		when(velement.getPropertyValue(eq("border-right-width"), any(MediaType.class), any(PseudoElementType.class))).thenReturn(new Identifier("thick"));
		
		when(velement.getPropertyValue(eq("border-top-color"), any(MediaType.class), any(PseudoElementType.class))).thenReturn(new Identifier("red"));
		when(velement.getPropertyValue(eq("border-bottom-color"), any(MediaType.class), any(PseudoElementType.class))).thenReturn(new HashValue("ff0000"));
		when(velement.getPropertyValue(eq("border-left-color"), any(MediaType.class), any(PseudoElementType.class))).thenReturn(new HashValue("ff0000"));
		when(velement.getPropertyValue(eq("border-right-color"), any(MediaType.class), any(PseudoElementType.class))).thenReturn(new FunctionValue("rgb", ValuesHelper.newValueList(new NumericValue(255, ""), new NumericValue(0, ""), new NumericValue(0, ""))));

		when(velement.getPropertyValue(eq("padding-top"), any(MediaType.class), any(PseudoElementType.class))).thenReturn(new NumericValue(1, "px"));
		when(velement.getPropertyValue(eq("padding-bottom"), any(MediaType.class), any(PseudoElementType.class))).thenReturn(new NumericValue(2, "px"));
		when(velement.getPropertyValue(eq("padding-left"), any(MediaType.class), any(PseudoElementType.class))).thenReturn(new NumericValue(3, "px"));
		when(velement.getPropertyValue(eq("padding-right"), any(MediaType.class), any(PseudoElementType.class))).thenReturn(new NumericValue(4, "px"));
		
		BorderDescriptor desc=new BorderDescriptor();
		BorderStyles.resolveBorders(null, velement, desc, PseudoElementType.PE_NOT_PSEUDO);
		
		assertEquals(BorderStyle.BS_OUTSET, desc.borderTopStyle);
		assertEquals(BorderStyle.BS_GROOVE, desc.borderBottomStyle);
		assertEquals(BorderStyle.BS_DASHED, desc.borderLeftStyle);
		assertEquals(BorderStyle.BS_DOTTED, desc.borderRightStyle);

		assertEquals(1, desc.borderTopWidth);
		assertEquals(2, desc.borderBottomWidth);
		assertEquals(3, desc.borderLeftWidth);
		assertEquals(8, desc.borderRightWidth);

		assertEquals(new Color(255, 0, 0), desc.borderTopColour);
		assertEquals(new Color(255, 0, 0), desc.borderBottomColour);
		assertEquals(new Color(255, 0, 0), desc.borderLeftColour);
		assertEquals(new Color(255, 0, 0), desc.borderRightColour);
		
		assertEquals(1, desc.paddingTopWidth);
		assertEquals(2, desc.paddingBottomWidth);
		assertEquals(3, desc.paddingLeftWidth);
		assertEquals(4, desc.paddingRightWidth);		
	}

	@Test
	public void testBorderWidthToInt1()
	{
		assertEquals(1, BorderStyles.borderWidthToInt(new Identifier("thin")));
		assertEquals(4, BorderStyles.borderWidthToInt(new Identifier("medium")));
		assertEquals(8,  BorderStyles.borderWidthToInt(new Identifier("thick")));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testBorderWidthToInt2()
	{
		BorderStyles.borderWidthToInt(new Identifier("qqqqq"));
		fail();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testBorderWidthToInt3()
	{
		BorderStyles.borderWidthToInt(new NumericValue(1, "px"));
		fail();
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
		assertEquals(12, decs1.size());
		assertEquals(new Declaration("border-bottom-color", new Identifier("red"), false), decs1.get(0));
		assertEquals(new Declaration("border-bottom-style", new Identifier("solid"), false), decs1.get(1));
		assertEquals(new Declaration("border-bottom-width", new Identifier("thin"), false), decs1.get(2));
		assertEquals(new Declaration("border-left-color", new Identifier("red"), false), decs1.get(3));
		assertEquals(new Declaration("border-left-style", new Identifier("solid"), false), decs1.get(4));
		assertEquals(new Declaration("border-left-width", new Identifier("thin"), false), decs1.get(5));
		assertEquals(new Declaration("border-right-color", new Identifier("red"), false), decs1.get(6));
		assertEquals(new Declaration("border-right-style", new Identifier("solid"), false), decs1.get(7));
		assertEquals(new Declaration("border-right-width", new Identifier("thin"), false), decs1.get(8));
		assertEquals(new Declaration("border-top-color", new Identifier("red"), false), decs1.get(9));
		assertEquals(new Declaration("border-top-style", new Identifier("solid"), false), decs1.get(10));
		assertEquals(new Declaration("border-top-width", new Identifier("thin"), false), decs1.get(11));
		
		Declaration d2=new Declaration("border", ValuesHelper.newValueList(new Identifier("thin"), new Identifier("red")), false);
		List<Declaration> decs2=new DeclarationPropertyOrder().sortedCopy(borderStyles.expandBorder(d2));
		assertEquals(8, decs2.size());
		assertEquals(new Declaration("border-bottom-color", new Identifier("red"), false), decs2.get(0));
		assertEquals(new Declaration("border-bottom-width", new Identifier("thin"), false), decs2.get(1));
		assertEquals(new Declaration("border-left-color", new Identifier("red"), false), decs2.get(2));
		assertEquals(new Declaration("border-left-width", new Identifier("thin"), false), decs2.get(3));
		assertEquals(new Declaration("border-right-color", new Identifier("red"), false), decs2.get(4));
		assertEquals(new Declaration("border-right-width", new Identifier("thin"), false), decs2.get(5));
		assertEquals(new Declaration("border-top-color", new Identifier("red"), false), decs2.get(6));
		assertEquals(new Declaration("border-top-width", new Identifier("thin"), false), decs2.get(7));
		
		Declaration d3=new Declaration("border", ValuesHelper.newValueList(new Identifier("wrong"), new Identifier("red")), false);
		List<Declaration> decs3=new DeclarationPropertyOrder().sortedCopy(borderStyles.expandBorder(d3));
		assertEquals(0, decs3.size());
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
		assertEquals(12, decs1.size());
		assertEquals(new Declaration("border-bottom-color", new Identifier("red"), false), decs1.get(0));
		assertEquals(new Declaration("border-bottom-style", new Identifier("solid"), false), decs1.get(1));
		assertEquals(new Declaration("border-bottom-width", new Identifier("thin"), false), decs1.get(2));
		assertEquals(new Declaration("border-left-color", new Identifier("red"), false), decs1.get(3));
		assertEquals(new Declaration("border-left-style", new Identifier("solid"), false), decs1.get(4));
		assertEquals(new Declaration("border-left-width", new Identifier("thin"), false), decs1.get(5));
		assertEquals(new Declaration("border-right-color", new Identifier("red"), false), decs1.get(6));
		assertEquals(new Declaration("border-right-style", new Identifier("solid"), false), decs1.get(7));
		assertEquals(new Declaration("border-right-width", new Identifier("thin"), false), decs1.get(8));
		assertEquals(new Declaration("border-top-color", new Identifier("red"), false), decs1.get(9));
		assertEquals(new Declaration("border-top-style", new Identifier("solid"), false), decs1.get(10));
		assertEquals(new Declaration("border-top-width", new Identifier("thin"), false), decs1.get(11));
	}	
	
	@Test
	public void validateBorderOneStyle()
	{
		assertTrue(BorderStyles.validateBorderOneStyle(new Identifier("solid")));
		assertFalse(BorderStyles.validateBorderOneStyle(new Identifier("red")));
		assertFalse(BorderStyles.validateBorderOneStyle(new NumericValue(1, "px")));
	}
	
	@Test
	public void validateBorderOneWidth()
	{
		assertTrue(BorderStyles.validateBorderOneWidth(new Identifier("thick")));
		assertTrue(BorderStyles.validateBorderOneWidth(new NumericValue(1, "px")));
		assertFalse(BorderStyles.validateBorderOneWidth(new Identifier("red")));
		assertFalse(BorderStyles.validateBorderOneWidth(new StringValue("red")));
	}
	
	@Test
	public void validateBorderOneColor()
	{
		assertTrue(BorderStyles.validateBorderOneColor(new Identifier("red")));
		assertFalse(BorderStyles.validateBorderOneColor(new Identifier("zzzz")));
		assertFalse(BorderStyles.validateBorderOneColor(new StringValue("red")));
		assertTrue(BorderStyles.validateBorderOneColor(new HashValue("ff0000")));
		assertTrue(BorderStyles.validateBorderOneColor(new HashValue("f00")));
		assertFalse(BorderStyles.validateBorderOneColor(new HashValue("sdfsdfgf")));
		assertTrue(BorderStyles.validateBorderOneColor(new FunctionValue("rgb", ValuesHelper.newValueList(new NumericValue(255, ""), new NumericValue(0, ""), new NumericValue(0, "")))));
	}
	
	@Test
	public void validatePaddingOne()
	{
		assertTrue(BorderStyles.validatePaddingOne(new NumericValue(1, "px")));
		assertFalse(BorderStyles.validatePaddingOne(new Identifier("thick")));
		assertFalse(BorderStyles.validatePaddingOne(new StringValue("red")));
	}
}

