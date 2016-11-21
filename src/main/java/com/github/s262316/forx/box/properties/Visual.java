package com.github.s262316.forx.box.properties;

import java.awt.Font;

import com.github.s262316.forx.box.BlockBox;
import com.github.s262316.forx.box.CellBox;
import com.github.s262316.forx.box.Column;
import com.github.s262316.forx.box.InlineBlockRootBox;
import com.github.s262316.forx.box.InlineBox;
import com.github.s262316.forx.box.TableBox;
import com.github.s262316.forx.box.TableRow;
import com.github.s262316.forx.graphics.GraphicsContext;

public interface Visual
{
//    public Graphics2D get_canvas();
    public GraphicsContext getGraphicsContext();
    public void calculateBorders(PropertyAdaptor on, BorderDescriptor borderdesc);
    public void computeMarginProperties(PropertyAdaptor on, MarginDescriptor margindesc);
    public void workOutAbsolutePosition(PropertyAdaptor on, PositionDescriptor pd);
    public void workOutFlowDimensions(PropertyAdaptor on, DimensionsDescriptor dd);
    public void workOutLineProperties(PropertyAdaptor on, LineDescriptor ld, GraphicsContext graphicsContext);
    public void workOutTextProperties(PropertyAdaptor on, TextProperties tp);
    public void workOutWordProperties(PropertyAdaptor on, WordProperties wp);
    public void workoutBlockProperties(PropertyAdaptor on, BlockProperties bp);
    public void workout_background_properties(PropertyAdaptor on, BackgroundProperties bp);
    public Font workOutFontProperties(PropertyAdaptor on);
    public void workOutFloatProperties(PropertyAdaptor on, FloatProperties fp);
    public void workoutColours(PropertyAdaptor on, ColourDescriptor coldesc);
    public InlineBox createAnonInlineBox();
    public BlockBox createAnonBlockBox();
    public InlineBlockRootBox createAnonInlineBlockRootBox();
    public TableRow createAnonRowBox();
    public Column createAnonColBox();
    public TableBox createAnonTableBox();
    public CellBox createAnonCellBox();
}
