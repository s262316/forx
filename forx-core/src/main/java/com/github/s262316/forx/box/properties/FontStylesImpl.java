package com.github.s262316.forx.box.properties;

import com.github.s262316.forx.box.properties.converters.ComputedValues;
import com.github.s262316.forx.box.properties.converters.EnumConverter;
import com.github.s262316.forx.box.properties.converters.ModelPropertyBinding;
import com.github.s262316.forx.box.util.FontStyle;
import com.github.s262316.forx.box.util.FontVariant;
import com.github.s262316.forx.css.DummyPropertyAdaptor;
import com.github.s262316.forx.css.FontStyles;
import com.github.s262316.forx.style.Identifier;
import com.github.s262316.forx.style.MediaType;
import com.github.s262316.forx.style.NumericValue;
import com.github.s262316.forx.style.Value;
import com.github.s262316.forx.style.selectors.PseudoElementType;
import com.github.s262316.forx.style.selectors.util.ValuesHelper;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

/*
 [ [ <'font-style'> || <'font-variant'> || <'font-weight'> ]? <'font-size'> [ / <'line-height'> ]? <'font-family'> ] | caption | icon | menu | message-box | small-caption | status-bar | inherit
p { font: 12px/14px sans-serif }
p { font: 80% sans-serif }
p { font: x-large/110% "New Century Schoolbook", serif }
p { font: bold italic large Palatino, serif }
p { font: normal small-caps 120%/120% fantasy }
 */
public class FontStylesImpl implements FontStyles
{
//	@Override
//	public List<Declaration> expand(Declaration toExpand)
//	{
//		ValueList valuelist=ValuesHelper.asValueList(toExpand.getValue());
//
//		if(valuelist.size()==1 && ValuesHelper.getIdentifier(valuelist.get(0)).isPresent())
//		{
//			Set<String> systemFonts=Sets.newHashSet("caption", "icon", "message-box", "small-caption", "status-bar");
//			if(systemFonts.contains(ValuesHelper.getIdentifier(valuelist.get(0)).get()))
//			{
//			
//			}
//		}
//		
//		return null;
//	}
	
	@Override
	public boolean validateFontStyle(Value v)
	{
		return new ModelPropertyBinding<FontStyle>(MediaType.MT_SCREEN, PseudoElementType.PE_NOT_PSEUDO)
	    	.when(Identifier.class, new EnumConverter<FontStyle>("FS_", true, FontStyle.class))
	    	.validate(v);
	}

	@Override
	public boolean validateFontVariant(Value v)
	{
		return new ModelPropertyBinding<FontVariant>(MediaType.MT_SCREEN, PseudoElementType.PE_NOT_PSEUDO)
	    	.when(Identifier.class, new EnumConverter<FontVariant>("FV_", true, FontVariant.class))
	    	.validate(v);
	}

	@Override
	public boolean validateFontWeight(Value v)
	{
		return new ModelPropertyBinding<Integer>(MediaType.MT_SCREEN, PseudoElementType.PE_NOT_PSEUDO)
				.when(NumericValue.class, nv -> NumericValues.absLength((NumericValue)nv, new DummyPropertyAdaptor()))
				.when(Identifier.class, this::fontWeightToInt)
				.validate(v);
	}

	public int fontWeightToInt(Value v)
	{
		String weight=ValuesHelper.getIdentifier(v).orElseThrow(IllegalArgumentException::new);
		switch(weight)
		{
			case "normal":
				return 400;
			case "bold":
				return 700;
			case "bolder":
			case "lighter":
				throw new IllegalArgumentException("unsupported font-weight: "+weight);
			default:
				throw new IllegalArgumentException("unrecognised font-weight "+weight);
		}
	}

	@Override
	public boolean validateFontSize(Value v)
	{
		return new ModelPropertyBinding<Integer>(MediaType.MT_SCREEN, PseudoElementType.PE_NOT_PSEUDO)
				.when(NumericValue.class, nv -> NumericValues.absLength((NumericValue)nv, new DummyPropertyAdaptor()))
				.when(Identifier.class, this::fontSizetoInt)
				.validate(v);
	}

	public static final ImmutableMap<String, Integer> ABSOLUTE_SIZES = new ImmutableMap.Builder<String, Integer>()
			.put("xx-small", 1)
			.put("x-small", 2)
			.put("medium", 3)
			.put("large", 4)
			.put("x-large", 5)
			.put("xx-large", 6)
			.build();

	public int fontSizetoInt(Value size)
	{
		Preconditions.checkArgument(size instanceof Identifier);

		String sizeName=((Identifier)size).ident;
		if(!ABSOLUTE_SIZES.containsKey(sizeName))
			throw new IllegalArgumentException("unsupported size "+sizeName);

		return ABSOLUTE_SIZES.get(sizeName);
	}
}
