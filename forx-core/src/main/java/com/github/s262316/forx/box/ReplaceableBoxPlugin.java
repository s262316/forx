package com.github.s262316.forx.box;

import java.awt.Graphics2D;
import com.github.s262316.forx.box.util.Length;

public interface ReplaceableBoxPlugin
{
    public Length swidth();
    public Length sheight();
    public double ratio();

    public void use_width(int w);
    public void use_height(int h);

    public void set_position(int left, int top);

    public void draw(Graphics2D c, int offx, int offy);
}
