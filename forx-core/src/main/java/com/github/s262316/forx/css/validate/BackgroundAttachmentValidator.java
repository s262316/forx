package com.github.s262316.forx.css.validate;

import com.github.s262316.forx.tree.style.Identifier;
import com.github.s262316.forx.tree.style.Value;
import com.github.s262316.forx.tree.style.ValueList;

public class BackgroundAttachmentValidator implements PropertyValidator
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

            if(id.ident.equals("scroll") ||
                    id.ident.equals("fixed"))
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

