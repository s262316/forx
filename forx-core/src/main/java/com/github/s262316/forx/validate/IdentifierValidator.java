/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.s262316.forx.validate;

import com.github.s262316.forx.tree.style.Identifier;
import com.github.s262316.forx.tree.style.Value;
import com.github.s262316.forx.tree.style.ValueList;

public class IdentifierValidator implements PropertyValidator
{
    private Identifier reference;

    public IdentifierValidator(String ref)
    {
        this.reference=new Identifier(ref);
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

	return value.equals(reference);
    }

}
