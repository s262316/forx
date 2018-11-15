package com.github.s262316.forx.box.properties;

import com.github.s262316.forx.box.properties.converters.ComputedValues;
import com.github.s262316.forx.box.properties.converters.EnumConverter;
import com.github.s262316.forx.box.properties.converters.ModelPropertyBinding;
import com.github.s262316.forx.box.properties.converters.PropertyInjectorPair;
import com.github.s262316.forx.box.util.BorderStyle;
import com.github.s262316.forx.css.BorderStyles;
import com.github.s262316.forx.css.DummyPropertyAdaptor;
import com.github.s262316.forx.css.Shorthands;
import com.github.s262316.forx.css.util.CSSColours;
import com.github.s262316.forx.css.util.InferenceTable2;
import com.github.s262316.forx.style.ColourValue;
import com.github.s262316.forx.style.Declaration;
import com.github.s262316.forx.style.FunctionValue;
import com.github.s262316.forx.style.HashValue;
import com.github.s262316.forx.style.Identifier;
import com.github.s262316.forx.style.MediaType;
import com.github.s262316.forx.style.NumericValue;
import com.github.s262316.forx.style.Value;
import com.github.s262316.forx.style.ValueList;
import com.github.s262316.forx.style.selectors.PseudoElementType;
import com.github.s262316.forx.style.selectors.util.ValuesHelper;
import com.github.s262316.forx.tree.visual.VElement;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.*;

@Component
public class BorderStylesImpl implements BorderStyles
{
	@Autowired
	private Shorthands shorthands;

	@Override
	public java.util.List<Declaration> expandBorder(Declaration dec)
	{
		java.util.List<Declaration> expanded = new ArrayList<>();
		ValueList valuelist = null;
		valuelist= ValuesHelper.asValueList(dec.getValue());

		HashBasedTable<Value, String, Boolean> table=shorthands.tableOfPropertyValueCombos(valuelist.members, "border-top-style", "border-top-color", "border-top-width");

		if(!Shorthands.strictTest(table))
			return Collections.<Declaration>emptyList();

		Map<String, Value> matched= InferenceTable2.infer(table);

		for(Map.Entry<String, Value> e : matched.entrySet())
		{
			String props[]=null;

			if(e.getKey().endsWith("-style"))
				props=new String[]{"border-top-style", "border-bottom-style", "border-left-style", "border-right-style"};
			else if(e.getKey().endsWith("-color"))
				props=new String[]{"border-top-color", "border-bottom-color", "border-left-color", "border-right-color"};
			else if(e.getKey().endsWith("-width"))
				props=new String[]{"border-top-width", "border-bottom-width", "border-left-width", "border-right-width"};

			for(String p : props)
				expanded.add(new Declaration(p, e.getValue(), dec.isImportant()));
		}

		return expanded;
	}

	public static final ImmutableMap<String, Integer> BORDER_WIDTHS = new ImmutableMap.Builder<String, Integer>()
			.put("thin", 1)
			.put("medium", 4)
			.put("thick", 8)
			.build();

	public static int borderWidthToInt(Value width)
	{
		Preconditions.checkArgument(width instanceof Identifier);

		String widthName=((Identifier)width).ident;
		if(!BORDER_WIDTHS.containsKey(widthName))
			throw new IllegalArgumentException("unsupported width "+widthName);

		return BORDER_WIDTHS.get(widthName);
	}

	@Override
	public boolean validateBorderOneStyle(Value v)
	{
		return new ModelPropertyBinding<BorderStyle>(MediaType.MT_SCREEN, PseudoElementType.PE_NOT_PSEUDO)
	    	.when(Identifier.class, new EnumConverter<BorderStyle>("BS_", true, BorderStyle.class))
	    	.validate(v);
	}

	@Override
	public boolean validateBorderOneWidth(Value v)
	{
		return new ModelPropertyBinding<Integer>(MediaType.MT_SCREEN, PseudoElementType.PE_NOT_PSEUDO)
	    	.when(NumericValue.class, nv -> NumericValues.absLength((NumericValue)nv, new DummyPropertyAdaptor()))
	    	.when(Identifier.class, BorderStylesImpl::borderWidthToInt)
	    	.validate(v);
	}

	@Override
	public boolean validateBorderOneColor(Value v)
	{
		return new ModelPropertyBinding<Color>(MediaType.MT_SCREEN, PseudoElementType.PE_NOT_PSEUDO)
			.when(Identifier.class, CSSColours::colourNameToColor)
	    	.when(HashValue.class, CSSColours::hashValueToColor)
	    	.when(FunctionValue.class, CSSColours::rgbFunctionToColor)
        	.when(ColourValue.class, CSSColours::colourValueToColor)
	    	.validate(v);
	}

	@Override
	public boolean validatePaddingOne(Value v)
	{
		return new ModelPropertyBinding<Integer>(MediaType.MT_SCREEN, PseudoElementType.PE_NOT_PSEUDO)
			.when(NumericValue.class, nv -> NumericValues.absLength((NumericValue)nv, new DummyPropertyAdaptor()))
	    	.validate(v);
	}

	public static void resolveBorders(PropertyAdaptor on, VElement subj, BorderDescriptor borderdesc, PseudoElementType pseudoType)
	{
        // border-top-style,border-right-style,border-bottom-style,border-left-style : <border-style> | inherit
		// border-style : none | hidden | dotted | dashed | solid | double | groove | ridge |  inset | outset
    	for(PropertyInjectorPair<BorderStyle> pair : ImmutableList.of(
    			new PropertyInjectorPair<BorderStyle>("border-top-style", bs -> borderdesc.borderTopStyle=bs),
    			new PropertyInjectorPair<BorderStyle>("border-bottom-style", bs -> borderdesc.borderBottomStyle=bs),
    			new PropertyInjectorPair<BorderStyle>("border-left-style", bs -> borderdesc.borderLeftStyle=bs),
    			new PropertyInjectorPair<BorderStyle>("border-right-style", bs -> borderdesc.borderRightStyle=bs)))
    	{
    		new ModelPropertyBinding<BorderStyle>(MediaType.MT_SCREEN, pseudoType)
		    	.when(Identifier.class, new EnumConverter<BorderStyle>("BS_", true, BorderStyle.class))
		    	.inject(pair.getInjector())
		    	.computedValue(ComputedValues::specifiedValue)
		    	.bind(subj, pair.getProperty());
		}

		// border-width : thin | medium | thick | <length>
    	for(PropertyInjectorPair<Integer> pair : ImmutableList.of(
    			new PropertyInjectorPair<Integer>("border-top-width", width -> borderdesc.borderTopWidth=width),
    			new PropertyInjectorPair<Integer>("border-bottom-width", width -> borderdesc.borderBottomWidth=width),
    			new PropertyInjectorPair<Integer>("border-left-width", width -> borderdesc.borderLeftWidth=width),
    			new PropertyInjectorPair<Integer>("border-right-width", width -> borderdesc.borderRightWidth=width)))
		{
    		new ModelPropertyBinding<Integer>(MediaType.MT_SCREEN, pseudoType)
				.when(NumericValue.class, nv -> NumericValues.absLength((NumericValue)nv, new DummyPropertyAdaptor()))
		    	.when(Identifier.class, BorderStylesImpl::borderWidthToInt)
		    	.inject(pair.getInjector())
		    	.computedValue(ComputedValues::domainValueAsNumericAbsolute)
		    	.bind(subj, pair.getProperty());
		}

		for(PropertyInjectorPair<Color> pair : ImmutableList.of(
				new PropertyInjectorPair<Color>("border-top-color", colour -> borderdesc.borderTopColour=colour),
				new PropertyInjectorPair<Color>("border-bottom-color", colour -> borderdesc.borderBottomColour=colour),
				new PropertyInjectorPair<Color>("border-left-color", colour -> borderdesc.borderLeftColour=colour),
				new PropertyInjectorPair<Color>("border-right-color", colour -> borderdesc.borderRightColour=colour)))
		{
			new ModelPropertyBinding<Color>(MediaType.MT_SCREEN, pseudoType)
				.when(Identifier.class, CSSColours::colourNameToColor)
	        	.when(HashValue.class, CSSColours::hashValueToColor)
	        	.when(FunctionValue.class, CSSColours::rgbFunctionToColor)
	        	.when(ColourValue.class, CSSColours::colourValueToColor)
	        	.inject(pair.getInjector())
	        	.computedValue(ComputedValues::domainValueAsColourValue)
				.bind(subj, pair.getProperty(), "color");
		}

		// padding-width : <length> | <percentage>
		for(PropertyInjectorPair<Integer> pair : ImmutableList.of(
				new PropertyInjectorPair<Integer>("padding-top", width -> borderdesc.paddingTopWidth=width),
				new PropertyInjectorPair<Integer>("padding-bottom", width -> borderdesc.paddingBottomWidth=width),
				new PropertyInjectorPair<Integer>("padding-left", width -> borderdesc.paddingLeftWidth=width),
				new PropertyInjectorPair<Integer>("padding-right", width -> borderdesc.paddingRightWidth=width)))
		{
			new ModelPropertyBinding<Integer>( MediaType.MT_SCREEN, pseudoType)
				.when(NumericValue.class, nv -> NumericValues.absLength((NumericValue)nv, new DummyPropertyAdaptor()))
	        	.inject(pair.getInjector())
	        	.computedValue(ComputedValues::domainValueAsNumericAbsolute)
	        	.bind(subj, pair.getProperty());
		}
	}
}

