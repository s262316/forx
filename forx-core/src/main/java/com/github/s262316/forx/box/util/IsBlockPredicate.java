package com.github.s262316.forx.box.util;

import javax.annotation.Nullable;

import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.cast.BoxTypes;

import com.google.common.base.Predicate;

public class IsBlockPredicate implements Predicate<Box>
{
	@Override
	public boolean apply(@Nullable Box input)
	{
		return BoxTypes.isBlockBox(input);
	}
}
