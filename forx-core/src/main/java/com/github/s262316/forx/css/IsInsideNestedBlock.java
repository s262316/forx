package com.github.s262316.forx.css;

import java.util.LinkedList;
import java.util.function.Predicate;

public class IsInsideNestedBlock implements Predicate<Tokenizer>
{
	private SyntaxPairs pairs=new SyntaxPairs();
	private LinkedList<String> openMatchingPairs=new LinkedList<>();	
	
	@Override
	public boolean test(Tokenizer tokenizer)
	{
		boolean inInside;

		if(tokenizer.curr.type==TokenType.CR_PUNCT)
		{
			if(pairs.openPairs().contains(tokenizer.curr.syntax))
			{
				openMatchingPairs.push(tokenizer.curr.syntax);
				inInside=true;
			}
			else if(pairs.closedPairs().contains(tokenizer.curr.syntax))
			{
				if(!openMatchingPairs.isEmpty() &&
						openMatchingPairs.peekFirst().equals(pairs.oppositeOf(tokenizer.curr.syntax)))
				{
					openMatchingPairs.pop();
					inInside=true; // returns true because we're still on the last }
				}
				else
					inInside=!openMatchingPairs.isEmpty();
			}
			else
				inInside=!openMatchingPairs.isEmpty();
		}
		else if(tokenizer.curr.type==TokenType.CR_END)
			inInside=false;
		else if(!openMatchingPairs.isEmpty())
			inInside=true;
		else
			inInside=false;

		return inInside;
	}
}
