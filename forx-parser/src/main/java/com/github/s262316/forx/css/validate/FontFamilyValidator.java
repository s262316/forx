package com.github.s262316.forx.css.validate;

import java.util.function.Predicate;

import com.github.s262316.forx.style.Identifier;
import com.github.s262316.forx.style.StringValue;
import com.github.s262316.forx.style.Value;
import com.github.s262316.forx.style.ValueList;
import com.github.s262316.forx.style.selectors.util.ValuesHelper;

/*
 * 	[[ <family-name> | <generic-family> ] [, [ <family-name>| <generic-family>] ]* ] | inherit
 * 
 */
public class FontFamilyValidator implements PropertyValidator
{
	@Override
	public boolean validate(Value value)
	{
		ValueList valueListOuter=ValuesHelper.asValueList(value);
		ValueList valueListInner=ValuesHelper.asValueList(valueListOuter.get(0));

		Predicate<Value> isString=StringValue.class::isInstance;
		Predicate<Value> isIdentifier=Identifier.class::isInstance;
		
		return valueListInner.members.stream().allMatch(
				isString.or(isIdentifier)
		);
	}
}
