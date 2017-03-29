/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.s262316.forx.css.validate;

import com.github.s262316.forx.css.util.CSSColours;
import com.github.s262316.forx.tree.style.ColourValue;
import com.github.s262316.forx.tree.style.FunctionValue;
import com.github.s262316.forx.tree.style.HashValue;
import com.github.s262316.forx.tree.style.Identifier;
import com.github.s262316.forx.tree.style.Value;
import com.github.s262316.forx.tree.style.ValueList;

public class ColourValidator implements PropertyValidator
{
    public ColourValidator()
    {

    }

    @Override
    public boolean validate(Value value)
    {
	ColourValue cv=null;

	if(value.getClass().equals(ValueList.class))
	{
            ValueList vl=(ValueList)value;
            if(vl.size()==1)
                value=vl.get(0);
            else
                return false;
	}

	if(value.getClass().equals(Identifier.class))
		cv=CSSColours.normalise(((Identifier)value).ident);
	else if(value.getClass().equals(HashValue.class))
		cv=CSSColours.normalise(((HashValue)value).str());
	else if(value.getClass().equals(FunctionValue.class))
		cv=CSSColours.normalise((FunctionValue)value);
	else if(value.getClass().equals(ColourValue.class))
	{
            cv=(ColourValue)value;
	}

	return cv!=null;
    }
}
