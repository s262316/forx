package com.github.s262316.forx.css;

import java.util.LinkedList;
import java.util.function.Predicate;

/**
 * assume we're at [@][rulename][x1][x2][x3][....]
 *                     ^^^^^^^^          
 */
public class SkipPastSemicolonOrBlock implements Predicate<Tokenizer>
{
	private SyntaxPairs pairs=new SyntaxPairs();
	private LinkedList<String> openMatchingPairs=new LinkedList<>();	
	
	@Override
	public boolean test(Tokenizer tokenizer)
	{
		boolean done=false;

		if(tokenizer.curr.type==TokenType.CR_PUNCT)
		{
			if(pairs.openPairs().contains(tokenizer.curr.syntax))
			{
				openMatchingPairs.push(tokenizer.curr.syntax);
			}
			else if(pairs.closedPairs().contains(tokenizer.curr.syntax))
			{
				if(openMatchingPairs.peekFirst().equals(pairs.oppositeOf(tokenizer.curr.syntax)))
					openMatchingPairs.pop();
				
				if(openMatchingPairs.isEmpty())
					done=true;
			}
			else if(tokenizer.curr.syntax.equals(";") && openMatchingPairs.isEmpty())
			{
				done=true;
			}
		}
		else if(tokenizer.curr.type==TokenType.CR_END)
			done=true;
		
		if(tokenizer.curr.type!=TokenType.CR_END)
			tokenizer.advance();		
		
		return done;
	}
}
