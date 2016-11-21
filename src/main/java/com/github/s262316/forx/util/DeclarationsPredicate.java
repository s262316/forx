package com.github.s262316.forx.util;

import com.github.s262316.forx.tree.style.Declaration;

import com.google.common.base.Predicate;

public class DeclarationsPredicate implements Predicate<Declaration>
{
	private boolean important;
	
	public DeclarationsPredicate(boolean important)
	{
		this.important=important;
	}
	
	@Override
	public boolean apply(Declaration input)
	{
		return input.isImportant()==important;
	}
}
