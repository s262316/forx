package com.github.s262316.forx.box.properties;

import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Ordering;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.s262316.forx.TestApplicationConfiguration;
import com.github.s262316.forx.style.Declaration;
import com.github.s262316.forx.style.Identifier;
import com.github.s262316.forx.style.NumericValue;
import com.github.s262316.forx.style.StringValue;
import com.github.s262316.forx.style.selectors.util.ValuesHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplicationConfiguration.class)
public class FontStylesImplTest
{
	@Autowired
	FontStylesImpl fontStyles;
	
	@Test
	public void validateFontStyles()
	{
		FontStylesImpl fontStyles=new FontStylesImpl();
		
		assertTrue(fontStyles.validateFontStyle(new Identifier("normal")));
		assertTrue(fontStyles.validateFontStyle(new Identifier("italic")));
		assertTrue(fontStyles.validateFontStyle(new Identifier("oblique")));
		Assert.assertFalse(fontStyles.validateFontStyle(new Identifier("wrong")));
	}

	@Test
	public void validateFontVariant()
	{
		FontStylesImpl fontStyles=new FontStylesImpl();
		
		assertTrue(fontStyles.validateFontVariant(new Identifier("normal")));
		// TODO doesn't pass
//		Assert.assertTrue(fontStyles.validateFontVariant(new Identifier("small-caps")));
		Assert.assertFalse(fontStyles.validateFontVariant(new Identifier("wrong")));
	}

	@Test
	public void validateFontWeight()
	{
		FontStylesImpl fontStyles=new FontStylesImpl();

		assertTrue(fontStyles.validateFontWeight(new Identifier("normal")));
		assertTrue(fontStyles.validateFontWeight(new Identifier("bold")));
		// TODO doesn't pass
//		Assert.assertTrue(fontStyles.validateFontWeight(new Identifier("bolder")));
//		Assert.assertTrue(fontStyles.validateFontWeight(new Identifier("lighter")));
		assertTrue(fontStyles.validateFontWeight(new NumericValue(100, "")));
		assertTrue(fontStyles.validateFontWeight(new NumericValue(200, "")));
		assertTrue(fontStyles.validateFontWeight(new NumericValue(300, "")));
		assertTrue(fontStyles.validateFontWeight(new NumericValue(400, "")));
		assertTrue(fontStyles.validateFontWeight(new NumericValue(500, "")));
		assertTrue(fontStyles.validateFontWeight(new NumericValue(600, "")));
		assertTrue(fontStyles.validateFontWeight(new NumericValue(700, "")));
		assertTrue(fontStyles.validateFontWeight(new NumericValue(800, "")));
		assertTrue(fontStyles.validateFontWeight(new NumericValue(900, "")));
	}

	@Test
	public void validateFontSize()
	{
		FontStylesImpl fontStyles=new FontStylesImpl();

		assertTrue(fontStyles.validateFontSize(new Identifier("xx-small")));
		assertTrue(fontStyles.validateFontSize(new Identifier("x-small")));
		// TODO
//		Assert.assertTrue(fontStyles.validateFontSize(new Identifier("small")));
		assertTrue(fontStyles.validateFontSize(new Identifier("medium")));
		assertTrue(fontStyles.validateFontSize(new Identifier("large")));
		assertTrue(fontStyles.validateFontSize(new Identifier("x-large")));
		assertTrue(fontStyles.validateFontSize(new Identifier("xx-large")));

		assertTrue(fontStyles.validateFontSize(new NumericValue(1, "px")));
		// TODO
//		Assert.assertTrue(fontStyles.validateFontSize(new NumericValue(1, "%")));
		Assert.assertFalse(fontStyles.validateFontSize(new StringValue("red")));
	}

	// [ [ <'font-style'> || <'font-variant'> || <'font-weight'> ]? <'font-size'> [ / <'line-height'> ]? <'font-family'> ] | caption | icon | menu | message-box | small-caption | status-bar | inherit
	@Test
	public void testExpandFont1()
	{
		Declaration d1=new Declaration("font",
				ValuesHelper.newValueList(new Identifier("normal"), new Identifier("smallcaps"), new Identifier("bold")), false);
		List<Declaration> decs1=fontStyles.expand(d1);
		assertTrue(decs1.isEmpty()); // font-size, font-family is misssing
	}

	@Test
	public void testExpandFont2()
	{
		Declaration d1=new Declaration("font",
				ValuesHelper.newValueList(new Identifier("normal"), new Identifier("smallcaps"), new Identifier("zzzzzz")), false);
		List<Declaration> decs1=fontStyles.expand(d1);
		assertTrue(decs1.isEmpty()); // zzzzzz not a valid font-size
	}

	@Test
	public void testExpandFont3()
	{
		Declaration d1=new Declaration("font",
				ValuesHelper.newValueList(new Identifier("normal"), new Identifier("smallcaps"), new NumericValue(3, "px"), new Identifier("serif")), false);
		List<Declaration> decs1= Ordering.<Declaration>from(Comparator.comparing(v -> v.getProperty())).sortedCopy(fontStyles.expand(d1));
		assertEquals(new Declaration("font-family", ValuesHelper.asValueList(new Identifier("serif")), false), decs1.get(0));
		assertEquals(new Declaration("font-size", new NumericValue(3, "px"), false), decs1.get(1));
		assertEquals(new Declaration("font-variant", new Identifier("smallcaps"), false), decs1.get(2));
		assertEquals(new Declaration("font-weight", new Identifier("normal"), false), decs1.get(3));
	}

	@Test
	public void testExpandFont4()
	{
		Declaration d1=new Declaration("font",
				ValuesHelper.newValueList(new Identifier("normal"), new Identifier("smallcaps"), new Identifier("bold"), new NumericValue(3,"px"), new Identifier("serif")), false);
		List<Declaration> decs1= Ordering.<Declaration>from(Comparator.comparing(v -> v.getProperty())).sortedCopy(fontStyles.expand(d1));
		assertEquals(new Declaration("font-family", ValuesHelper.asValueList(new Identifier("serif")), false), decs1.get(0));
		assertEquals(new Declaration("font-size", new NumericValue(3, "px"), false), decs1.get(1));
		assertEquals(new Declaration("font-style", new Identifier("normal"), false), decs1.get(2));
		assertEquals(new Declaration("font-variant", new Identifier("smallcaps"), false), decs1.get(3));
		assertEquals(new Declaration("font-weight", new Identifier("bold"), false), decs1.get(4));
	}

	@Test
	public void testExpandFont5()
	{
		Declaration d1=new Declaration("font",
				ValuesHelper.newValueList(new Identifier("normal"), new Identifier("smallcaps"), new Identifier("bold"), new NumericValue(3,"px"), ValuesHelper.newValueList(new Identifier("serif1"), new Identifier("serif2"))), false);
		List<Declaration> decs1= Ordering.<Declaration>from(Comparator.comparing(v -> v.getProperty())).sortedCopy(fontStyles.expand(d1));
		assertEquals(new Declaration("font-family", ValuesHelper.newValueList(new Identifier("serif1"), new Identifier("serif2")), false), decs1.get(0));
		assertEquals(new Declaration("font-size", new NumericValue(3, "px"), false), decs1.get(1));
		assertEquals(new Declaration("font-style", new Identifier("normal"), false), decs1.get(2));
		assertEquals(new Declaration("font-variant", new Identifier("smallcaps"), false), decs1.get(3));
		assertEquals(new Declaration("font-weight", new Identifier("bold"), false), decs1.get(4));
	}

	@Test
	public void testExpandFont6()
	{
		Declaration d1=new Declaration("font",
				ValuesHelper.newValueList(new Identifier("normal"), new Identifier("smallcaps"), new Identifier("bold"), ValuesHelper.newValueList(new NumericValue(3,"px"), new NumericValue(4, "px")), ValuesHelper.newValueList(new Identifier("serif1"), new Identifier("serif2"))), false);
		List<Declaration> decs1= Ordering.<Declaration>from(Comparator.comparing(v -> v.getProperty())).sortedCopy(fontStyles.expand(d1));
		assertEquals(new Declaration("font-family", ValuesHelper.newValueList(new Identifier("serif1"), new Identifier("serif2")), false), decs1.get(0));
		assertEquals(new Declaration("font-size", new NumericValue(3, "px"), false), decs1.get(1));
		assertEquals(new Declaration("font-style", new Identifier("normal"), false), decs1.get(2));
		assertEquals(new Declaration("font-variant", new Identifier("smallcaps"), false), decs1.get(3));
		assertEquals(new Declaration("font-weight", new Identifier("bold"), false), decs1.get(4));
		assertEquals(new Declaration("line-height", new NumericValue(4, "px"), false), decs1.get(5));
	}

	@Test
	public void testExpandFont7()
	{
		Declaration d1=new Declaration("font",
				ValuesHelper.newValueList(ValuesHelper.newValueList(new NumericValue(100,"px"), new NumericValue(1, "")), ValuesHelper.newValueList(new Identifier("Ahem"))), false);
		List<Declaration> decs1= Ordering.<Declaration>from(Comparator.comparing(v -> v.getProperty())).sortedCopy(fontStyles.expand(d1));
		assertEquals(new Declaration("font-family", ValuesHelper.newValueList(new Identifier("Ahem")), false), decs1.get(0));
		assertEquals(new Declaration("font-size", new NumericValue(100, "px"), false), decs1.get(1));
		assertEquals(new Declaration("line-height", new NumericValue(1, ""), false), decs1.get(2));
	}
}
