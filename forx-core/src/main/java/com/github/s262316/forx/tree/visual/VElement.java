package com.github.s262316.forx.tree.visual;

import java.net.URL;
import java.util.Optional;

import com.github.s262316.forx.box.BlockBox;
import com.github.s262316.forx.box.CellBox;
import com.github.s262316.forx.box.Column;
import com.github.s262316.forx.box.InlineBlockRootBox;
import com.github.s262316.forx.box.InlineBox;
import com.github.s262316.forx.box.TableBox;
import com.github.s262316.forx.box.TableRow;
import com.github.s262316.forx.graphics.GraphicsContext;
import com.github.s262316.forx.style.MediaType;
import com.github.s262316.forx.style.Value;
import com.github.s262316.forx.style.selectors.PseudoElementType;

public interface VElement
{
    Value getPropertyValue(String property, MediaType mediaType, PseudoElementType pseudoType);
    Value getPropertyValue(String property, MediaType mediaType);
    void reset_counter(String name, int to);
    void inc_counter(String name, int amount);
    Optional<Integer> counter_value(String name);
    VElement find_counter(String name);
    GraphicsContext getGraphicsContext();
    void computed_value(String property, Value value);
    String resolve_resource(URL url);

    InlineBox createAnonInlineBox();
    BlockBox createAnonBlockBox();
    InlineBlockRootBox createAnonInlineBlockRootBox();
    TableRow createAnonRowBox();
    Column createAnonColBox();
    TableBox createAnonTableBox();
    CellBox createAnonCellBox();
}
