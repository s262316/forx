package com.github.s262316.forx.tree.style;

import java.util.List;
import java.net.URL;

import com.github.s262316.forx.graphics.GraphicsContext;
import com.github.s262316.forx.tree.style.selectors.PseudoElementType;

public interface StyleElementCascadeRules
{
    public Value getPropertyValue(String property, MediaType mediaType, PseudoElementType pseudoType);
    public void clearStyles();
    public void setStyles(List<Declaration> decs);
    public GraphicsContext getGraphicsContext();
    public void computed_value(String property, Value value);
    public String resolve_resource(URL url);
    public String getDefaultStyleLanguage();
    public StyleElementCascadeRules find_counter(String name);
    public StyleElementCascadeRules find_counter_before(String name);
    public void inc_counter(String name, int amount);
    public void reset_counter(String name, int to);
    public int counter_value(String name);

}
