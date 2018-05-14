package com.github.s262316.forx.box.properties;

import com.github.s262316.forx.style.NumericValue;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

public class NumericValues
{
    public static int absLength(NumericValue nv, PropertyAdaptor on)
    {
        switch(nv.unit)
        {
            case "em":
                return (int)(on.getContainer().getFont().getSize()*nv.amount);
            case "ex":
                return (int)(on.getContainer().getFont().getSize()*nv.amount);
            case "px":
                return (int)nv.amount;
            case "pt":
                return (int)nv.amount;
            case "in":
                return (int)(nv.amount/96);
            case "cm":
                return (int)((25.54*nv.amount)/96);
            case "mm":
                return (int)((2.54*nv.amount)/96);
            default:
                Preconditions.checkState(false, "unknown unit %s", nv.unit);
                return 0; // never reached
        }
    }

    public static int requireNoUnit(NumericValue nv)
    {
        Preconditions.checkArgument(StringUtils.isEmpty(nv.unit));

        return (int)nv.amount;
    }

}
