package com.github.s262316.forx.tree.style.selectors;

import org.junit.Test;

import com.github.s262316.forx.tree.style.selectors.Specificity;
import com.github.s262316.forx.tree.style.selectors.SpecificityComparator;
import static org.junit.Assert.*;

public class TestSpecificityComparator
{
	// each entry in the array is higher than it's previous
	Specificity s[]=new Specificity[]{
			new Specificity(0,0,0,0),
			new Specificity(0,0,0,1),
			new Specificity(0,0,1,0),
			new Specificity(0,0,1,1),
			new Specificity(0,1,0,0),
			new Specificity(0,1,0,1),
			new Specificity(0,1,1,0),
			new Specificity(0,1,1,1),
			new Specificity(1,0,0,0),
			new Specificity(1,0,0,1),
			new Specificity(1,0,1,0),
			new Specificity(1,0,1,1),
			new Specificity(1,1,0,0),
			new Specificity(1,1,0,1),
			new Specificity(1,1,1,0),
			new Specificity(1,1,1,1)};

	SpecificityComparator comparator=new SpecificityComparator();
	
	@Test
	public void test()
	{
		for(int i=0; i<s.length; i++)
		{
			for(int j=0; j<s.length; j++)
			{
				int result=comparator.compare(s[i], s[j]);
				
				if(i>j)
					assertEquals("i="+i+",j="+j, 1, result);
				else if(j>i)
					assertEquals("i="+i+",j="+j, -1, result);
				else
					assertEquals("i="+i+",j="+j, 0, result);

			}
		}
	}
}
