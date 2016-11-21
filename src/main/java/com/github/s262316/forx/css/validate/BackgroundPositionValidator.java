package com.github.s262316.forx.css.validate;

import com.github.s262316.forx.tree.style.Identifier;
import com.github.s262316.forx.tree.style.NumericValue;
import com.github.s262316.forx.tree.style.Value;
import com.github.s262316.forx.tree.style.ValueList;

public class BackgroundPositionValidator implements PropertyValidator
{
    @Override
    public boolean validate(Value value)
    {
            Value v1;
            Value v2=null;
            NumericValue nv;

            if(value.getClass().equals(ValueList.class))
            {
                ValueList vl=(ValueList)value;
                if(vl.size()==1)
                    v1=vl.get(0);
                else if(vl.size()==2)
                {
                    v1=vl.members.get(0);
                    v2=vl.members.get(vl.members.size()-1);
                }
                else
                        return false;
            }
            else
                    v1=value;

            if(value.getClass().equals(NumericValue.class))
            {
            }
            else if(value.getClass().equals(Identifier.class))
            {
                Identifier id=(Identifier)value;

                if(id.ident.equals("left"))
                {}
                else if(id.ident.equals("center"))
                {}
                else if(id.ident.equals("right"))
                {}
                else if(id.ident.equals("top") && v2==null)
                {}
                else if(id.ident.equals("bottom") && v2==null)
                {}
                else
                    return false;
            }
            else
                    return false;

            if(v2!=null)
            {
                if(value.getClass().equals(NumericValue.class))
                {
                }
                else if(value.getClass().equals(Identifier.class))
                {
                    Identifier id=(Identifier)value;

                    if(id.ident.equals("top"))
                    {}
                    else if(id.ident.equals("center"))
                    {}
                    else if(id.ident.equals("bottom"))
                    {}
                    else
                        return false;
                }
                else
                        return false;
            }

            return true;
    }

}
