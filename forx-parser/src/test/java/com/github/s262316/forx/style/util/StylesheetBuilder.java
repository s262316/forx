package com.github.s262316.forx.style.util;

import com.github.s262316.forx.style.Declaration;
import com.github.s262316.forx.style.MediaType;
import com.github.s262316.forx.style.StyleRule;
import com.github.s262316.forx.style.Stylesheet;
import com.github.s262316.forx.style.selectors.Selector;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.TreeSet;

public class StylesheetBuilder
{
    private Selector selector;
    private List<StyleRule> styleRules=new ArrayList<>();

    public StylesheetBuilder withStylerule(Selector selector, List<Declaration> declarations)
    {
        styleRules.add(new StyleRule(selector,
                Maps.uniqueIndex(declarations, v-> v.getProperty()),
                EnumSet.of(MediaType.MT_ALL), styleRules.size()));

        return this;
    }

    public Stylesheet build()
    {
        Stylesheet ss=new Stylesheet(null, null);
        TreeSet<StyleRule> rulesets=ss.getRuleset();
        rulesets.addAll(styleRules);

        return ss;
    }
}
