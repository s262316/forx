package com.github.s262316.forx.box.properties;

import com.github.s262316.forx.box.properties.converters.EnumConverter;
import com.github.s262316.forx.box.properties.converters.ModelPropertyBinding;
import com.github.s262316.forx.box.util.FontStyle;
import com.github.s262316.forx.box.util.FontVariant;
import com.github.s262316.forx.css.FontStyles;
import com.github.s262316.forx.style.Identifier;
import com.github.s262316.forx.style.MediaType;
import com.github.s262316.forx.style.Value;
import com.github.s262316.forx.style.selectors.PseudoElementType;

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
}
