package com.github.s262316.forx.box.properties;

import java.awt.Font;

import com.github.s262316.forx.box.BlockBox;
import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.CellBox;
import com.github.s262316.forx.box.Column;
import com.github.s262316.forx.box.InlineBlockRootBox;
import com.github.s262316.forx.box.InlineBox;
import com.github.s262316.forx.box.TableBox;
import com.github.s262316.forx.box.TableRow;
import com.github.s262316.forx.graphics.GraphicsContext;
import com.github.s262316.forx.tree.visual.AnonReason;

public interface Visual
{
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
    
    public InlineBox createAnonInlineBox(AnonReason anonReason);
    public BlockBox createAnonBlockBox(AnonReason anonReason);
    public InlineBlockRootBox createAnonInlineBlockRootBox(AnonReason anonReason);
    public TableRow createAnonRowBox(AnonReason anonReason);
    public Column createAnonColBox(AnonReason anonReason);
    public TableBox createAnonTableBox(AnonReason anonReason);
    public CellBox createAnonCellBox(AnonReason anonReason);
    
    public AnonReason getAnonReason();
	public void setPostSplit(InlineBox postSplitInlineBox);
	public Box getPostSplit();
}
