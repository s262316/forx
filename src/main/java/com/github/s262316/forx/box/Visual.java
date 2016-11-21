package com.github.s262316.forx.box;

import java.awt.Graphics2D;
import java.awt.Font;
import com.github.s262316.forx.box.util.BackgroundProperties;
import com.github.s262316.forx.box.util.BlockProperties;
import com.github.s262316.forx.box.util.BorderDescriptor;
import com.github.s262316.forx.box.util.ColourDescriptor;
import com.github.s262316.forx.box.util.DimensionsDescriptor;
import com.github.s262316.forx.box.util.FloatProperties;
import com.github.s262316.forx.box.util.LineDescriptor;
import com.github.s262316.forx.box.util.MarginDescriptor;
import com.github.s262316.forx.box.util.PositionDescriptor;
import com.github.s262316.forx.box.util.TextProperties;
import com.github.s262316.forx.box.util.WordProperties;
import com.github.s262316.forx.graphics.GraphicsContext;

public interface Visual
{
    public Graphics2D get_canvas();
    public GraphicsContext getGraphicsContext();
    public void calculateBorders(BoxDimensions on, BorderDescriptor borderdesc);
    public void computeMarginProperties(BoxDimensions on, MarginDescriptor margindesc);
    public void workOutAbsolutePosition(BoxDimensions on, PositionDescriptor pd);
    public void workOutFlowDimensions(BoxDimensions on, DimensionsDescriptor dd);
    public void workOutLineProperties(BoxDimensions on, LineDescriptor ld);
    public void workOutTextProperties(BoxDimensions on, TextProperties tp);
    public void workOutWordProperties(BoxDimensions on, WordProperties wp);
    public void workoutBlockProperties(BoxDimensions on, BlockProperties bp);
    public void workout_background_properties(BoxDimensions on, BackgroundProperties bp);
    public Font workOutFontProperties(BoxDimensions on);
    public void workOutFloatProperties(BoxDimensions on, FloatProperties fp);
    public void workoutColours(BoxDimensions on, ColourDescriptor coldesc);
    public InlineBox createAnonInlineBox();
    public BlockBox createAnonBlockBox();
    public InlineBlockRootBox createAnonInlineBlockRootBox();
    public TableRow createAnonRowBox();
    public Column createAnonColBox();
    public TableBox createAnonTableBox();
    public CellBox createAnonCellBox();
}
