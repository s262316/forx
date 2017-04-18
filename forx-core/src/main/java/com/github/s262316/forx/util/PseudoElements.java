package com.github.s262316.forx.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.s262316.forx.tree.visual.XmlVDocument;
import org.apache.commons.lang3.tuple.Pair;

import com.github.s262316.forx.tree.XNodes;
import com.github.s262316.forx.tree.XmlNode;
import com.github.s262316.forx.style.MediaType;
import com.github.s262316.forx.style.selectors.PseudoClassType;
import com.github.s262316.forx.style.selectors.PseudoElementType;
import com.github.s262316.forx.tree.visual.XmlVElement;

import com.google.common.collect.Lists;

public class PseudoElements
{
	public static final Pattern FIRST_LETTER_PATTERN = Pattern.compile("[\\p{Ps}\\p{Pe}\\p{Pi}\\p{Pf}\\p{Po}]*\\p{Alnum}[\\p{Ps}\\p{Pe}\\p{Pi}\\p{Pf}\\p{Po}]*");

	public static Pair<String, String> firstLetter(String input)
	{
		Matcher m=FIRST_LETTER_PATTERN.matcher(input);
		
		boolean found=m.find();
		if(found)
		{
			String firstLetter=m.group();
			String restOfInput=input.substring(m.end());

			return Pair.of(firstLetter, restOfInput);
		}
		else
		{
			return Pair.of("", input);
		}
	}

	public static XmlVElement nearestParentWithFirstLetter(XmlVElement element)
	{
		XmlVDocument doc=(XmlVDocument)element.getDocument();
		List<XmlNode> rootToHere=XNodes.pathToHere(element);
		boolean isSensitive;
		
		for(XmlNode node : Lists.reverse(rootToHere.subList(1, rootToHere.size())))
		{
			isSensitive=doc.isSensitiveToPseudoElement((XmlVElement)node, MediaType.MT_ALL, PseudoElementType.PE_FIRST_LETTER, PseudoClassType.PCT_NO_CLASS);
			if(isSensitive)
				return (XmlVElement)node;
		}		
		
		return null;
	}
}

