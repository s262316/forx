package com.github.s262316.forx.tree.visual;

import java.net.URL;
import java.util.Optional;

import com.github.s262316.forx.box.properties.converters.ModelPropertyBinding;
import com.github.s262316.forx.graphics.GraphicsContext;
import com.github.s262316.forx.tree.style.MediaType;
import com.github.s262316.forx.tree.style.Value;
import com.github.s262316.forx.tree.style.selectors.PseudoElementType;

public interface VElement
{
    public Value getPropertyValue(String property, MediaType mediaType, PseudoElementType pseudoType);
    public Value getPropertyValue(String property, MediaType mediaType);
    public void reset_counter(String name, int to);
    public void inc_counter(String name, int amount);
    public Optional<Integer> counter_value(String name);
    public VElement find_counter(String name);
    public GraphicsContext getGraphicsContext();
    public void computed_value(String property, Value value);
    public String resolve_resource(URL url);
}
