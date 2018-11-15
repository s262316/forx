/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.s262316.forx.box;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import com.github.s262316.forx.box.properties.Visual;
import com.github.s262316.forx.box.util.Border;
import com.github.s262316.forx.box.util.Direction;
import com.github.s262316.forx.box.util.Length;
import com.github.s262316.forx.box.util.SpaceFlag;

public interface Box extends Layable
{

    public int contentLeft();

    public int contentRight();

    public int contentTop();

    public int contentBottom();

    public int contentWidth();

    public int contentHeight();

    public int paddingLeft();

    public int paddingRight();

    public int paddingTop();

    public int paddingBottom();

    public int marginLeft();

    public int marginTop();

    public int marginRight();

    public int marginBottom();

    public Border borderLeft();

    public Border borderRight();

    public Border borderTop();

    public Border borderBottom();

    public void set_margins(int left, int top, int right, int bottom);

    public void applyMinMaxConstraints(Length width, Length height, Length marginLeft, Length marginRight,
                                       Length minWidth, Length maxWidth,
                                       Length minHeight, Length maxHeight);

    public int floating_boxes();

    public int flowing_boxes();

    public void flow_back(Box b);

    public void flow_back(Inline il);

    public void flow_insert(Box b, int before);

    public void flow_back(String str, SpaceFlag space);

    public void float_back(FloatBox b);

    public void float_insert(FloatBox b, int before);

    public void table_back(TableMember cont, TableMember memb);

    public void table_insert(TableMember cont, TableMember memb);

    /** add to parent in the document */
    public void absBackStatic(AbsoluteBox b);
    
    /** add to containing block (rootbox or nearest absolute/relative) */
    public void absBack(AbsoluteBox b);

//    @Deprecated
//	public Box resize_width(int from, int amount);
//    
//    @Deprecated
//    public Layable resize_height(int from, int amount);

    public Direction direction();

//    @Override
//    public Graphics2D canvas();

    public Box root();

    public abstract boolean explicit_newline();

    public FlowContext get_flow_context();

    public Box get_flow_context_box();

    public boolean stretchy_cwidth();
    public boolean stretchy_cheight();

    public int can_stretch(int amount);
    public int can_stretch_vert(int amount);

    public boolean stiffy_height();

    public int max_stiffy_height();

    public boolean isAutoWidth();
    public boolean isAutoHeight();
    
    public int canStretchHeight(int amount);

    public ReplaceableBoxPlugin replace_object();

    public Visual getVisual();
    
	public void calculateStaticPosition(AbsoluteBox newMember);
	
	public Font getFont();
    
}
