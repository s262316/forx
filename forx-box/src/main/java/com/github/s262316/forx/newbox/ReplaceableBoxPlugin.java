package com.github.s262316.forx.newbox;

import java.awt.Graphics2D;

public interface ReplaceableBoxPlugin
{
    public int swidth();
    public int sheight();
    public double ratio();

    public void use_width(int w);
    public void use_height(int h);

    public void set_position(int left, int top);

    public void draw(Graphics2D c, int offx, int offy);
}
