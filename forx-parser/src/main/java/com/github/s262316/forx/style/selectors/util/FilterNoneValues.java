package com.github.s262316.forx.style.selectors.util;

import javax.annotation.Nullable;

import com.github.s262316.forx.style.Value;

import com.google.common.base.Predicate;

import java.util.Optional;

public class FilterNoneValues implements Predicate<Value>
{
	@Override
	public boolean apply(@Nullable Value input)
	{
		Optional<String> identifier=ValuesHelper.getIdentifier(input);

		return !identifier.orElse("").equals("none");
	}
}
