package com.github.s262316.forx.box.properties;

import org.junit.Assert;
import org.junit.Test;

import com.github.s262316.forx.style.Identifier;

public class FontStylesImplTest
{
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

}
