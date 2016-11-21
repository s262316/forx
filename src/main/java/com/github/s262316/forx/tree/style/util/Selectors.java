package com.github.s262316.forx.tree.style.util;

import java.util.Collections;
import java.util.List;

import com.github.s262316.forx.tree.style.selectors.Operator;
import com.github.s262316.forx.tree.style.selectors.PseudoClass;
import com.github.s262316.forx.tree.style.selectors.PseudoElementType;
import com.github.s262316.forx.tree.style.selectors.Selector;
import com.github.s262316.forx.tree.style.selectors.SelectorAttr;
import com.github.s262316.forx.tree.style.selectors.SelectorElement;
import com.github.s262316.forx.tree.style.selectors.SelectorPart;
import com.github.s262316.forx.tree.style.selectors.Specificity;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class Selectors
{
	public static Selector createSimpleElementNameSelector(String name)
	{
		SelectorElement se=new SelectorElement(name);
		
		Selector s=new Selector(Lists.<SelectorPart>newArrayList(se));
		
		return s;
	}

	
	public static Selector createAttributeNameSelector(String name)
	{
		SelectorAttr sa=new SelectorAttr(name, "", "");
		
		SelectorElement se=new SelectorElement(
				"*", Lists.newArrayList(sa),
				Collections.<PseudoClass>emptyList(),
				Collections.<PseudoElementType>emptyList(),
				Collections.<String>emptyList());

		Selector s=new Selector(Lists.<SelectorPart>newArrayList(se));
		
		return s;		
	}
	
	public static Selector trueSelector()
	{
		SelectorElement se=new SelectorElement("*");
		
		Selector s=new Selector(Lists.<SelectorPart>newArrayList(se));
		
		return s;		
	}
	
	public static Selector falseSelector()
	{
		SelectorElement se=new SelectorElement("<<<<<");

		Selector s=new Selector(Lists.<SelectorPart>newArrayList(se));
		
		return s;
	}
	
	public static Selector descendentSelector(String e1, String e2)
	{
		return new Selector(Lists.newArrayList(
				new SelectorElement(e1),
				new Operator(" "),
				new SelectorElement(e2)
				));
	}
	
	/**
	 * never calculates "a" (style attribute)
	 * 
	 * @param sel
	 * @return
	 */
	public static Specificity calculate_specificity(Selector sel)
	{
		int a=0, b=0, c=0, d=0;
		SelectorElement se;

		// 1. count the number of ID attributes in the selector (= b)

		// 2. count the number of other attributes and pseudo-classes in
		// the selector (= c)

		// 3. count the number of element names and pseudo-elements in the
		// selector (= d)

		for(SelectorPart sp : sel.getParts())
		{
			if(sp.getClass().equals(SelectorElement.class))
			{
				se=(SelectorElement)sp;

				if(se.ids.size()>0)
					b++;

				c+=se.attrs.size();
				c+=se.pseudoClasses.size();

				if(!se.name.equals("*"))
					d++;
				d+=se.pseudoElements.size();
			}
			else if(sp.getClass().equals(SelectorAttr.class))
			{
				c++;
			}
		}

		return new Specificity(a, b, c, d);
	}	
	
	public static boolean dashmatch(String fromSelector, String fromDocument)
	{
    	List<String> splitSelector=Splitter.on('-')
	        	.splitToList(fromSelector);
    	
    	List<String> splitAttrValue=Lists.newArrayList(
    			Iterables.limit(
    					Splitter.on('-').split(fromDocument),
    					splitSelector.size()
    					)
   				);
    	
    	// selector is the shorter one
    	// we want to do this: splitAttrValue.startswith(splitSelector)
    	
        return splitSelector.equals(splitAttrValue);		
	}
	
	public static boolean containsPseudoType(Selector selector)
	{
		for(SelectorPart sp : selector.getParts())
		{
			if(sp instanceof SelectorElement)
			{
				SelectorElement se=(SelectorElement)sp;
				
				if(!se.pseudoClasses.isEmpty())
					return true;
			}
		}
		
		return false;
	}
}


