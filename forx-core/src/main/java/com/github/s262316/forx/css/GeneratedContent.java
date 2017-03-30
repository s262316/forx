package com.github.s262316.forx.css;

import com.github.fracpete.romannumerals4j.RomanNumeralFormat;
import com.github.s262316.forx.common.NumberRep;
import com.github.s262316.forx.tree.style.FunctionValue;
import com.github.s262316.forx.tree.style.Identifier;
import com.github.s262316.forx.tree.style.StringValue;
import com.github.s262316.forx.tree.style.UrlValue;
import com.github.s262316.forx.tree.style.Value;
import com.github.s262316.forx.tree.style.util.ValuesHelper;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Component
public class GeneratedContent
{
    private List<String> CONTENT_IDENTIFIERS=Lists.newArrayList(
            "normal", "none", "open-quote", "close-quote", "no-open-quote", "no-close-quote", "inherit"
    );

    @Autowired
    private CssLists lists;

    //normal | none | [ <string> | <uri> | <counter> | attr(<identifier>) | open-quote | close-quote | no-open-quote | no-close-quote ]+ | inherit
    public boolean validateContentProperty(Value contentPropertyValue)
    {
        boolean valid=true;
        List<Value> vs=ValuesHelper.asValueList(contentPropertyValue).members;
        Iterator<Value> it=vs.iterator();

        while(it.hasNext() && valid)
        {
            Value value=it.next();
            if(value.getClass().equals(Identifier.class))
            {
                Optional<String> ident=ValuesHelper.getIdentifier(value);

                valid &= ident.map(CONTENT_IDENTIFIERS::contains)
                        .orElse(false);

                // if "inherit" only that is allowed
                valid &= ident.map(
                        v -> (StringUtils.equals(v, "inherit") && vs.size()==1) || !StringUtils.equals(v, "inherit"))
                        .orElse(false);
            }
            else if(value.getClass().equals(StringValue.class))
            {
                valid &= true;
            }
            else if(value.getClass().equals(UrlValue.class))
            {
                valid &= true;
            }
            else if(value.getClass().equals(FunctionValue.class))
            {
                FunctionValue func=(FunctionValue)value;
                if(StringUtils.equals(func.name, "attr"))
                {
                    valid &= validateAttrFunction(func);
                }
                else if(StringUtils.equals(func.name, "counter"))
                {
                    valid &= validateCounterFunction(func);
                }
                else if(StringUtils.equals(func.name, "counters"))
                {
                    valid &= validateCountersFunction(func);
                }
                else
                    valid=false;
            }
            else
                valid=false;

        }

        return valid;
    }

    public boolean validateAttrFunction(FunctionValue func)
    {
        boolean valid;

        valid=StringUtils.equals(func.name, "attr");
        valid &= func.values.members.size()==1;
        valid &= func.values.members.get(0).getClass().equals(Identifier.class);

        return valid;
    }

    public boolean validateCounterFunction(FunctionValue func)
    {
        boolean valid;

        valid=StringUtils.equals(func.name, "counter");
        if(func.values.members.size()==1)
        {
            valid &= func.values.members.get(0).getClass().equals(Identifier.class);
        }
        else if(func.values.members.size()==2)
        {
            valid &= func.values.members.get(0).getClass().equals(Identifier.class);
            valid &= lists.validateListStyleProperty(func.values.members.get(1));
        }
        else
            valid=false;

        return valid;
    }

    // counters(name, string)
    // counters(name, string, style)
    public boolean validateCountersFunction(FunctionValue func)
    {
        boolean valid;

        valid=StringUtils.equals(func.name, "counters");
        if(func.values.members.size()==2)
        {
            valid &= func.values.members.get(0).getClass().equals(Identifier.class);
            valid &= func.values.members.get(1).getClass().equals(StringValue.class);
        }
        else if(func.values.members.size()==3)
        {
            valid &= func.values.members.get(0).getClass().equals(Identifier.class);
            valid &= func.values.members.get(1).getClass().equals(StringValue.class);
            valid &= lists.validateListStyleProperty(func.values.members.get(2));
        }
        else
            valid=false;

        return valid;
    }

    public static String formatCounterAsDecimal(int input)
    {
        return String.valueOf(input);
    }

    public static String formatCounterAsDecimalLeadingZero(int input)
    {
        String result=String.valueOf(input);
        if(input < 10)
            result="0" + result;

        return result;
    }

    public static String formatCounterAsLowerRoman(int input)
    {
        RomanNumeralFormat romanFormat = new RomanNumeralFormat();
        return romanFormat.format(input).toLowerCase();
    }

    public static String formatCounterAsUpperRoman(int input)
    {
        RomanNumeralFormat romanFormat = new RomanNumeralFormat();
        return romanFormat.format(input);
    }

    public static String formatCounterAsLowerAlpha(int input)
    {
        return NumberRep.translate_alpha_upper(input).toLowerCase();
    }

    public static String formatCounterAsUpperAlpha(int input)
    {
        return NumberRep.translate_alpha_upper(input);
    }

    public static String formatAsGeorgian(int input)
    {
        return String.valueOf(input);
    }

    public static String formatCounterAsArmenian(int input)
    {
        return String.valueOf(input);
    }

    public static String formatCounterAsLowerGreek(int input)
    {
        return String.valueOf(input);
    }
}
