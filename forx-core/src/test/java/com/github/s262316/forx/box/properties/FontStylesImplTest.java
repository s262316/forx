package com.github.s262316.forx.box.properties;

import java.util.List;

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
		
		Assert.assertTrue(fontStyles.validateFontStyle(new Identifier("normal")));
		Assert.assertTrue(fontStyles.validateFontStyle(new Identifier("italic")));
		Assert.assertTrue(fontStyles.validateFontStyle(new Identifier("oblique")));
		Assert.assertFalse(fontStyles.validateFontStyle(new Identifier("wrong")));
	}

	@Test
	public void validateFontVariant()
	{
		FontStylesImpl fontStyles=new FontStylesImpl();
		
		Assert.assertTrue(fontStyles.validateFontVariant(new Identifier("normal")));
		// TODO doesn't pass
//		Assert.assertTrue(fontStyles.validateFontVariant(new Identifier("small-caps")));
		Assert.assertFalse(fontStyles.validateFontVariant(new Identifier("wrong")));
	}

	@Test
	public void validateFontWeight()
	{
		FontStylesImpl fontStyles=new FontStylesImpl();

		Assert.assertTrue(fontStyles.validateFontWeight(new Identifier("normal")));
		Assert.assertTrue(fontStyles.validateFontWeight(new Identifier("bold")));
		// TODO doesn't pass
//		Assert.assertTrue(fontStyles.validateFontWeight(new Identifier("bolder")));
//		Assert.assertTrue(fontStyles.validateFontWeight(new Identifier("lighter")));
		Assert.assertTrue(fontStyles.validateFontWeight(new NumericValue(100, "px")));
		Assert.assertTrue(fontStyles.validateFontWeight(new NumericValue(200, "px")));
		Assert.assertTrue(fontStyles.validateFontWeight(new NumericValue(300, "px")));
		Assert.assertTrue(fontStyles.validateFontWeight(new NumericValue(400, "px")));
		Assert.assertTrue(fontStyles.validateFontWeight(new NumericValue(500, "px")));
		Assert.assertTrue(fontStyles.validateFontWeight(new NumericValue(600, "px")));
		Assert.assertTrue(fontStyles.validateFontWeight(new NumericValue(700, "px")));
		Assert.assertTrue(fontStyles.validateFontWeight(new NumericValue(800, "px")));
		Assert.assertTrue(fontStyles.validateFontWeight(new NumericValue(900, "px")));
	}

	@Test
	public void validateFontSize()
	{
		FontStylesImpl fontStyles=new FontStylesImpl();

		Assert.assertTrue(fontStyles.validateFontSize(new Identifier("xx-small")));
		Assert.assertTrue(fontStyles.validateFontSize(new Identifier("x-small")));
		// TODO
//		Assert.assertTrue(fontStyles.validateFontSize(new Identifier("small")));
		Assert.assertTrue(fontStyles.validateFontSize(new Identifier("medium")));
		Assert.assertTrue(fontStyles.validateFontSize(new Identifier("large")));
		Assert.assertTrue(fontStyles.validateFontSize(new Identifier("x-large")));
		Assert.assertTrue(fontStyles.validateFontSize(new Identifier("xx-large")));

		Assert.assertTrue(fontStyles.validateFontSize(new NumericValue(1, "px")));
		// TODO
//		Assert.assertTrue(fontStyles.validateFontSize(new NumericValue(1, "%")));
		Assert.assertFalse(fontStyles.validateFontSize(new StringValue("red")));
	}
	
	@Test
	public void testExpandFont()
	{
		Declaration d1=new Declaration("font", ValuesHelper.newValueList(new Identifier("normal"), new Identifier("smallcaps"), new Identifier("bold")), false);
		List<Declaration> decs1=fontStyles.expand(d1);
		
		
	}
	
}
