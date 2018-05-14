package com.github.s262316.forx.css;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.s262316.forx.css.validate.ColourValidator;
import com.github.s262316.forx.css.validate.FontFamilyValidator;
import com.github.s262316.forx.style.ColourValue;
import com.github.s262316.forx.style.Identifier;
import com.github.s262316.forx.style.NumericValue;
import com.github.s262316.forx.style.StringValue;
import com.github.s262316.forx.style.selectors.util.ValuesHelper;
import com.google.common.collect.ImmutableMap;

@Configuration
public class CssProperties
{
	@Bean
	public Map<String, ShorthandPropertyReference> cssShorthandPropertyTable(BorderStyles borderStyles)
	{
		ImmutableMap<String, ShorthandPropertyReference> shorthandPropertyTable = new ImmutableMap.Builder<String, ShorthandPropertyReference>()
				.put("border", new ShorthandPropertyReference("border", borderStyles::expandBorder))
				.put("background", new ShorthandPropertyReference("background", null))
				.put("border-color", new ShorthandPropertyReference("border-color", null))
				.put("border-style", new ShorthandPropertyReference("border-style", null))
				.put("border-top", new ShorthandPropertyReference("border-top", null))
				.put("border-bottom", new ShorthandPropertyReference("border-bottom", null))
				.put("border-left", new ShorthandPropertyReference("border-left", null))
				.put("border-right", new ShorthandPropertyReference("border-right", null))
				.put("border-width", new ShorthandPropertyReference("border-width", null))
				.put("font", new ShorthandPropertyReference("font", null))
				.put("list-style", new ShorthandPropertyReference("list-style", null))
				.put("margin", new ShorthandPropertyReference("margin", null))
				.put("outline", new ShorthandPropertyReference("outline", null))
				.put("padding", new ShorthandPropertyReference("padding", null))
				.build();

		return shorthandPropertyTable;
	}

	@Bean
	public Map<String, PropertyReference> cssPropertyTable(BorderStyles borderStyles, GeneratedContent generatedContent, FontStyles fontStyles)
	{
		ImmutableMap<String, PropertyReference> propertyTable = new ImmutableMap.Builder<String, PropertyReference>()
			.put("display", new PropertyReference("display", false, new Identifier("inline"), null))
			.put("float", new PropertyReference("float", false, new Identifier("none"), null))
			.put("position", new PropertyReference("position", false, new Identifier("static"), null))
			.put("border-top-width", new PropertyReference("border-top-width", false, new Identifier("medium"), borderStyles::validateBorderOneWidth))
			.put("border-right-width", new PropertyReference("border-right-width", false, new Identifier("medium"), borderStyles::validateBorderOneWidth))
			.put("border-bottom-width", new PropertyReference("border-bottom-width", false, new Identifier("medium"), borderStyles::validateBorderOneWidth))
			.put("border-left-width", new PropertyReference("border-left-width", false, new Identifier("medium"), borderStyles::validateBorderOneWidth))
			.put("border-top-color", new PropertyReference("border-top-color", false, null, borderStyles::validateBorderOneColor))
			.put("border-right-color", new PropertyReference("border-right-color", false, null, borderStyles::validateBorderOneColor))
			.put("border-bottom-color", new PropertyReference("border-bottom-color", false, null, borderStyles::validateBorderOneColor))
			.put("border-left-color", new PropertyReference("border-left-color", false, null, borderStyles::validateBorderOneColor))
			.put("border-top-style", new PropertyReference("border-top-style", false, new Identifier("none"), borderStyles::validateBorderOneStyle))
			.put("border-right-style", new PropertyReference("border-right-style", false, new Identifier("none"), borderStyles::validateBorderOneStyle))
			.put("border-bottom-style", new PropertyReference("border-bottom-style", false, new Identifier("none"), borderStyles::validateBorderOneStyle))
			.put("border-left-style", new PropertyReference("border-left-style", false, new Identifier("none"), borderStyles::validateBorderOneStyle))
			.put("padding-top", new PropertyReference("padding-top", false, new NumericValue(0, "px"), borderStyles::validatePaddingOne))
			.put("padding-right", new PropertyReference("padding-right", false, new NumericValue(0, "px"), borderStyles::validatePaddingOne))
			.put("padding-bottom", new PropertyReference("padding-bottom", false, new NumericValue(0, "px"), borderStyles::validatePaddingOne))
			.put("padding-left", new PropertyReference("padding-left", false, new NumericValue(0, "px"), borderStyles::validatePaddingOne))
			.put("margin-top", new PropertyReference("margin-top", false, new NumericValue(0, "px"), null))
			.put("margin-bottom", new PropertyReference("margin-bottom", false, new NumericValue(0, "px"), null))
			.put("margin-right", new PropertyReference("margin-right", false, new NumericValue(0, "px"), null))
			.put("margin-left", new PropertyReference("margin-left", false, new NumericValue(0, "px"), null))
			.put("top", new PropertyReference("top", false, new Identifier("auto"), null))
			.put("right", new PropertyReference("right", false, new Identifier("auto"), null))
			.put("bottom", new PropertyReference("bottom", false, new Identifier("auto"), null))
			.put("left", new PropertyReference("left", false, new Identifier("auto"), null))
			.put("width", new PropertyReference("width", false, new Identifier("auto"), null))
			.put("height", new PropertyReference("height", false, new Identifier("auto"), null))
			.put("min-width", new PropertyReference("min-width", false, new NumericValue(0, "px"), null))
			.put("max-width", new PropertyReference("max-width", false, new Identifier("none"), null))
			.put("min-height", new PropertyReference("min-height", false, new NumericValue(0, "px"), null))
			.put("max-height", new PropertyReference("max-height", false, new Identifier("none"), null))
			.put("line-height", new PropertyReference("line-height", true, new Identifier("normal"), null))
			.put("vertical-align", new PropertyReference("vertical-align", false, new Identifier("baseline"), null))
			.put("text-indent", new PropertyReference("text-indent", true, new NumericValue(0, "px"), null))
			.put("text-align", new PropertyReference("text-align", true, null, null))
			.put("text-decoration", new PropertyReference("text-decoration", true, null, null))
			.put("letter-spacing", new PropertyReference("letter-spacing", true, new Identifier("normal"), null))
			.put("word-spacing", new PropertyReference("word-spacing", true, new Identifier("normal"), null))
			.put("direction", new PropertyReference("direction", true, new Identifier("ltr"), null))
			.put("font-family", new PropertyReference("font-family", true, new Identifier("Times New Roman"), new FontFamilyValidator()))
			.put("font-style", new PropertyReference("font-style", true, new Identifier("normal"), fontStyles::validateFontStyle))
			.put("font-variant", new PropertyReference("font-variant", true, new Identifier("normal"), fontStyles::validateFontVariant))
			.put("font-weight", new PropertyReference("font-weight", true, new Identifier("normal"), fontStyles::validateFontWeight))
			.put("font-size", new PropertyReference("font-size", true, new Identifier("medium"), fontStyles::validateFontSize))
			.put("content", new PropertyReference("content", false, new Identifier("normal"), generatedContent::validateContentProperty))
			.put("quotes", new PropertyReference("quotes", true, ValuesHelper.newValueList(new StringValue("\""), new StringValue("\""), new StringValue("\'"), new StringValue("\'")), null))
			.put("counter-reset", new PropertyReference("counter-reset", false, new Identifier("none"), null))
			.put("counter-increment", new PropertyReference("counter-increment", false, new Identifier("none"), null))
			.put("clear", new PropertyReference("clear", false, new Identifier("none"), null))
			.put("color", new PropertyReference("color", true, new ColourValue(0, 0, 0), new ColourValidator()))
			.put("background-color", new PropertyReference("background-color", false, new Identifier("transparent"), null))
			.put("background-image", new PropertyReference("background-image", false, new Identifier("none"), null))
			.put("background-repeat", new PropertyReference("background-repeat", false, new Identifier("repeat"), null))
			.put("background-attachment", new PropertyReference("background-attachment", false, new Identifier("scroll"), null))
			.put("background-position", new PropertyReference("background-position", false, ValuesHelper.newValueList(new NumericValue(0, "px"), new NumericValue(0, "px")), null))
			.put("overflow", new PropertyReference("overflow", false, new Identifier("visible"), null))
			.put("z-index", new PropertyReference("z-index", false, new Identifier("auto"), null))
			.build();	
		
		return propertyTable;
	}
}

