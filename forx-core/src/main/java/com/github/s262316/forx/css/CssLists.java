package com.github.s262316.forx.css;

import com.github.s262316.forx.tree.style.Identifier;
import com.github.s262316.forx.tree.style.Value;
import com.github.s262316.forx.tree.style.util.ValuesHelper;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CssLists
{
    private List<String> LIST_STYLE_TYPES= Lists.newArrayList(
            "disc", "circle", "square", "decimal" , "decimal-leading-zero", "lower-roman",
            "upper-roman", "lower-greek", "lower-latin", "upper-latin" ,
            "armenian", "georgian", "lower-alpha", "upper-alpha", "none" ,
            "inherit");

    public boolean validateListStyleProperty(Value listStyleValue)
    {
        boolean valid;
        Optional<String> ident= ValuesHelper.getIdentifier(listStyleValue);

        valid=ident.map(LIST_STYLE_TYPES::contains)
                .orElse(false);

        return valid;
    }
}
