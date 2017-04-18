package com.github.s262316.forx.css.validate;

import com.github.s262316.forx.style.Identifier;
import com.github.s262316.forx.style.Value;
import com.github.s262316.forx.style.ValueList;

public class BackgroundRepeatValidator implements PropertyValidator
{
    @Override
    public boolean validate(Value value)
    {
        if(value.getClass().equals(ValueList.class))
        {
            ValueList vl=(ValueList)value;
            if(vl.size()==1)
                value=vl.get(0);
            else
                return false;
        }

        if(value.getClass().equals(Identifier.class))
        {
            Identifier id=(Identifier)value;

            if(id.ident.equals("repeat") ||
                    id.ident.equals("repeat-x") ||
                    id.ident.equals("repeat-y") ||
                    id.ident.equals("no-repeat"))
            {
                    return true;
            }
            else
                    return false;
        }
        else
                return false;
    }
}
