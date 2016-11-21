package com.github.s262316.forx.css.validate;

import com.github.s262316.forx.tree.style.Identifier;
import com.github.s262316.forx.tree.style.UrlValue;
import com.github.s262316.forx.tree.style.Value;
import com.github.s262316.forx.tree.style.ValueList;

public class BackgroundImageValidator implements PropertyValidator
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

        if(value.getClass().equals(UrlValue.class))
            return true;
        else if(value.getClass().equals(Identifier.class))
            return ((Identifier)value).ident.equals("none");
        else
            return false;
    }
}
