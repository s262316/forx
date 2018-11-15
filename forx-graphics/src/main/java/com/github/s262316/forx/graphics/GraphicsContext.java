package com.github.s262316.forx.graphics;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;

public interface GraphicsContext
{
    public int getDpi();
//    public Graphics2D getBrowserCanvas();
    public Rectangle get_browser_area_limits();
    public void setContentHeight(int nh);
    public void setContentWidth(int nw);
    public FontMetrics fontMetrics(Font font);
}
