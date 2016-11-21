package com.github.s262316.forx.box;

public interface Inline extends Layable
{
    public ReplaceableBoxPlugin replace_object();
    public boolean explicit_newline();

    public boolean is_descendent_of(Box b);
    public int atomic_width();

    @Override
    public int preferred_width();
    @Override
    public int preferred_min_width();
    @Override
    public int preferred_shrink_width(int avail_width);

    public int bl_shift();
}

