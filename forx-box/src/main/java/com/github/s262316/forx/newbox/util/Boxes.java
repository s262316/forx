package com.github.s262316.forx.newbox.util;

import java.util.Iterator;

import com.github.s262316.forx.newbox.Box;
import com.github.s262316.forx.newbox.BoxCounter;
import com.github.s262316.forx.newbox.relayouter.LayableTreeTraverser;
import com.google.common.collect.FluentIterable;

public class Boxes
{
	public static void renumber(Box root)
	{
		LayableTreeTraverser traverser=new LayableTreeTraverser();
		FluentIterable<Box> fit=traverser.preOrderTraversal(root);
		
		BoxCounter.reset();
		
		Iterator<Box> lit=fit.iterator();
		while(lit.hasNext())
		{
			Box l=lit.next();
			
			l.setId(BoxCounter.next());
		}
	}
}

