/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.s262316.forx.validate;

import com.github.s262316.forx.tree.style.NumericValue;
import com.github.s262316.forx.tree.style.Value;
import com.github.s262316.forx.tree.style.ValueList;

public class LengthValidator implements PropertyValidator
{
    private NumericValue reference;

    public LengthValidator()
    {
        reference=null;
    }

    @Override
    public boolean validate(Value value)
    {
	ValueList vl;
	if(value.getClass().equals(ValueList.class))
	{
            vl=(ValueList)value;
            if(vl.size()==1)
                value=vl.get(0);
            else
                return false;
	}

	if(reference==null)
	{
            return value.getClass().equals(NumericValue.class);
	}
	else
            throw new RuntimeException("unsupported");
    }


}
