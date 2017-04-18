package com.github.s262316.forx.style.selectors;


import com.github.s262316.forx.box.util.Objects2;
import com.google.common.collect.Ordering;

public class SpecificityComparator extends Ordering<Specificity>
{
    @Override
    public int compare(Specificity r1, Specificity r2)
    {
    	return Objects2.firstNonZero(
	    	Integer.compare(r1.getA(), r2.getA()),
	    	Integer.compare(r1.getB(), r2.getB()),
	    	Integer.compare(r1.getC(), r2.getC()),
	    	Integer.compare(r1.getD(), r2.getD()));    	
    }
}

