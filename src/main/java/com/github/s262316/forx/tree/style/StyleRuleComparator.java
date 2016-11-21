package com.github.s262316.forx.tree.style;

import com.github.s262316.forx.tree.style.selectors.SpecificityComparator;

import com.google.common.collect.Ordering;

/**
 *
 * compares on specificity then order
 *
 */
public class StyleRuleComparator extends Ordering<StyleRule>
{
	private static final SpecificityComparator SELECTOR_ORDERING=new SpecificityComparator();
	
    @Override
    public int compare(StyleRule r1, StyleRule r2)
    {
    	int specComparison=SELECTOR_ORDERING.compare(r1.getSelector().getSpecificity(), r2.getSelector().getSpecificity());
    	if(specComparison!=0)
    		return specComparison;
    	
    	return Integer.compare(r1.getOrder(), r2.getOrder());
    }
}

