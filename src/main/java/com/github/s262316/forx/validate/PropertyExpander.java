package com.github.s262316.forx.validate;

import java.util.List;

import com.github.s262316.forx.tree.style.Declaration;

public interface PropertyExpander
{
	public List<Declaration> expand(Declaration dec);
}
