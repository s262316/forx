package com.github.s262316.forx.box;

import java.awt.Font;
import java.awt.Graphics2D;

public interface BoxDimensions
{
    int contentWidth();
    int contentHeight();
    BoxDimensions container_box();
    Font getFont();
    Graphics2D canvas();
}
