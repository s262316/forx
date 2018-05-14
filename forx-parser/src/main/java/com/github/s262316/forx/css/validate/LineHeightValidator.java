package com.github.s262316.forx.css.validate;

import com.github.s262316.forx.style.NumericValue;
import com.github.s262316.forx.style.Value;
import com.github.s262316.forx.style.selectors.util.ValuesHelper;

public class LineHeightValidator implements PropertyValidator
{
	@Override
	public boolean validate(Value value)
	{
		if(value instanceof NumericValue)
			return true;

		return false;
	}
}
