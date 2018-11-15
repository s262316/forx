package com.github.s262316.forx.box.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import com.github.s262316.forx.css.CSSPropertiesReference;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.s262316.forx.box.properties.converters.EnumConverter;
import com.github.s262316.forx.box.properties.converters.ModelPropertyBinding;
import com.github.s262316.forx.box.util.FontStyle;
import com.github.s262316.forx.box.util.FontVariant;
import com.github.s262316.forx.css.DummyPropertyAdaptor;
import com.github.s262316.forx.css.FontStyles;
import com.github.s262316.forx.css.Shorthands;
import com.github.s262316.forx.css.util.InferenceTable2;
import com.github.s262316.forx.style.Declaration;
import com.github.s262316.forx.style.Identifier;
import com.github.s262316.forx.style.MediaType;
import com.github.s262316.forx.style.NumericValue;
import com.github.s262316.forx.style.Value;
import com.github.s262316.forx.style.ValueList;
import com.github.s262316.forx.style.selectors.PseudoElementType;
import com.github.s262316.forx.style.selectors.util.ValuesHelper;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/*
 [ [ <'font-style'> || <'font-variant'> || <'font-weight'> ]? <'font-size'> [ / <'line-height'> ]? <'font-family'> ] | caption | icon | menu | message-box | small-caption | status-bar | inherit
p { font: 12px/14px sans-serif }
p { font: 80% sans-serif }
p { font: x-large/110% "New Century Schoolbook", serif }
p { font: bold italic large Palatino, serif }
p { font: normal small-caps 120%/120% fantasy }
 */
@Component
public class FontStylesImpl implements FontStyles
{
	private final static Logger logger= LoggerFactory.getLogger(FontStylesImpl.class);

	@Autowired
	private Shorthands shorthands;
	@Autowired
	private CSSPropertiesReference propertiesReference;

	@Override
	public List<Declaration> expand(Declaration toExpand)
	{
		logger.info("expanding font property with values {}", toExpand.getValue());

		List<Declaration> expanded = new ArrayList<>();
		ValueList valuelist = ValuesHelper.asValueList(toExpand.getValue());

		// font-style, font-variant, font-weight in any orde
		List<Value> first3=valuelist.members.subList(0, Math.min(3, valuelist.members.size()));

		HashBasedTable<Value, String, Boolean> styleVariantWeight=shorthands.tableOfPropertyValueCombos(first3, "font-style", "font-variant", "font-weight");
		Map<String, Value> matchedStyleVariantWeight=InferenceTable2.infer(styleVariantWeight);

		matchedStyleVariantWeight.forEach((k, v) -> expanded.add(new Declaration(k, v, false)));

		logger.info("matched style/variant/weight: {}", matchedStyleVariantWeight);

		ValueList copy=new ValueList(valuelist);

		matchedStyleVariantWeight.values().forEach(v -> copy.remove(v));

		logger.info("matching font-size/line-height");

		ListIterator<Value> vit=copy.members.listIterator();

		try
		{
			Value fontSizeAndOrLineHeight=vit.next();
			ValueList fontSizeAndOrLineHeightList=ValuesHelper.asValueList(fontSizeAndOrLineHeight);

			if(propertiesReference.validate("font-size", fontSizeAndOrLineHeightList.get(0)))
			{
				expanded.add(new Declaration("font-size", fontSizeAndOrLineHeightList.get(0), false));
				logger.info("matched with value {}", fontSizeAndOrLineHeightList.get(0));
			}
			else
				return Collections.emptyList();

			if(fontSizeAndOrLineHeightList.size()>1)
			{
				if (propertiesReference.validate("line-height", fontSizeAndOrLineHeightList.get(1)))
				{
					expanded.add(new Declaration("line-height", fontSizeAndOrLineHeightList.get(1), false));
					logger.info("matched with value {}", fontSizeAndOrLineHeightList.get(1));
				}
			}

			logger.info("matching font-family");

			Value fontFamily=vit.next();
			if (propertiesReference.validate("font-family", fontFamily))
			{
				expanded.add(new Declaration("font-family", ValuesHelper.asValueList(fontFamily), false));
			}
			else
				return Collections.emptyList();

			return expanded;
		}
		catch(NoSuchElementException nsee)
		{
			return Collections.emptyList();
		}
	}
	
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
				.when(NumericValue.class, nv ->  NumericValues.requireNoUnit((NumericValue)nv))
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
