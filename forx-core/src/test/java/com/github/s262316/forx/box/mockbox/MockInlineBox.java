package com.github.s262316.forx.box.mockbox;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.NotImplementedException;

import com.github.s262316.forx.box.AbsoluteBox;
import com.github.s262316.forx.box.AtomicInline;
import com.github.s262316.forx.box.BlockBox;
import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.FloatBox;
import com.github.s262316.forx.box.FlowContext;
import com.github.s262316.forx.box.Flowspace;
import com.github.s262316.forx.box.Inline;
import com.github.s262316.forx.box.InlineBox;
import com.github.s262316.forx.box.Layable;
import com.github.s262316.forx.box.Line;
import com.github.s262316.forx.box.ReplaceableBoxPlugin;
import com.github.s262316.forx.box.TableBox;
import com.github.s262316.forx.box.TableMember;
import com.github.s262316.forx.box.properties.Visual;
import com.github.s262316.forx.box.relayouter.LayoutResult;
import com.github.s262316.forx.box.util.Border;
import com.github.s262316.forx.box.util.Direction;
import com.github.s262316.forx.box.util.Length;
import com.github.s262316.forx.box.util.SizeResult;
import com.github.s262316.forx.box.util.SpaceFlag;
import com.github.s262316.forx.box.util.VerticalAlignment;
import com.google.common.base.Preconditions;

public class MockInlineBox extends InlineBox
{
    private LinkedList<Inline> inlines=new LinkedList<Inline>();
    private LinkedList<FloatBox> float_list=new LinkedList<FloatBox>();
    private LinkedList<Layable> all=new LinkedList<Layable>();
    private Box container;

	public MockInlineBox()
	{
		super(null, null, null, null);
	}

	public Box select(int[] index)
	{
		Layable layable=all.get(index[0]);
		
		if(index.length==1)
			return (Box)layable;

		int subarray[]=ArrayUtils.subarray(index, 1, index.length);
	
		if(layable instanceof MockBlockBox)
			return ((MockBlockBox)layable).select(subarray);
		else if(layable instanceof MockInlineBox)
			return ((MockInlineBox)layable).select(subarray);
		else
			throw new IllegalArgumentException();
	}    
    
    @Override
    public Box container()
    {
        return container;
    }
    
    @Override
    public void set_container(Box cont)
    {
    	Preconditions.checkNotNull(cont);
    	
    	container=cont;
    }    
    
    @Override
	public Box getContainer()
    {
		return container;
	}    
    
	@Override
	public List<Layable> getMembersAll()
	{
		return all;
	}

	@Override
	public void flow_back(Box b)
	{
		Preconditions.checkArgument(b instanceof Inline);
		
		all.add(b);
		inlines.add((InlineBox)b);
		b.set_container(this);
	}
	
	public void setVisual(Visual visual)
	{
		this.visual=visual;
	}    
    
	@Override
	public Visual getVisual()
	{
		return visual;
	}
	
	@Override
	public int getId()
	{
		return id;
	}
	
	@Override
	public SizeResult compute_dimensions() {
		throw new NotImplementedException("");

	}

	@Override
	public LayoutResult calculate_position(Layable member) {
		throw new NotImplementedException("");

	}

	@Override
	public void uncalculate_position(Layable member) {
		throw new NotImplementedException("");
		
	}

	@Override
	public void computeProperties() {
		throw new NotImplementedException("");
		
	}

	@Override
	public void set_dimensions(int width, int height) {
		throw new NotImplementedException("");
		
	}

	@Override
	public void set_position(int left, int top) {
		throw new NotImplementedException("");
		
	}

	@Override
	public void unsetPosition() {
		throw new NotImplementedException("");
		
	}

	@Override
	public void set_width(int width) {
		throw new NotImplementedException("");
		
	}

	@Override
	public void set_height(int height) {
		throw new NotImplementedException("");
		
	}

	@Override
	public int preferred_width() {
		throw new NotImplementedException("");

	}

	@Override
	public int preferred_min_width() {
		throw new NotImplementedException("");

	}

	@Override
	public int preferred_shrink_width(int avail_width) {
		throw new NotImplementedException("");

	}

	@Override
	public TableBox table_root() {
		throw new NotImplementedException("");

	}

	@Override
	public int getRelTop() {
		throw new NotImplementedException("");

	}

	@Override
	public int getRelLeft() {
		throw new NotImplementedException("");

	}

	@Override
	public boolean is_relative() {
		throw new NotImplementedException("");

	}

	@Override
	public void set_relative(boolean to) {
		throw new NotImplementedException("");
		
	}

	@Override
	public int left() {
		throw new NotImplementedException("");

	}

	@Override
	public int top() {
		throw new NotImplementedException("");

	}

	@Override
	public int right() {
		throw new NotImplementedException("");

	}

	@Override
	public int bottom() {
		throw new NotImplementedException("");

	}

	@Override
	public int width() {
		throw new NotImplementedException("");

	}

	@Override
	public int height() {
		throw new NotImplementedException("");

	}

	@Override
	public void change_height(int difference) {
		throw new NotImplementedException("");
		
	}

	@Override
	public void change_width(int difference) {
		throw new NotImplementedException("");
		
	}

	@Override
	public List<Box> getMembersFlowing() {
		throw new NotImplementedException("");

	}

	@Override
	public List<FloatBox> getMembersFloating() {
		throw new NotImplementedException("");

	}

	@Override
	public List<AbsoluteBox> getMembersPositioned() {
		throw new NotImplementedException("");

	}

	@Override
	public boolean flows() {
		throw new NotImplementedException("");

	}

	@Override
	public void setId(int id) {
		throw new NotImplementedException("");
		
	}

	@Override
	public int stretchAmountWidth(Layable whichChild, int maxExtraAmount) {
		throw new NotImplementedException("");

	}

	@Override
	public int stretchAmountHeight(Layable whichChild, int y, int maxExtraAmount) {
		throw new NotImplementedException("");

	}

	@Override
	public void setFutureWidth(int futureWidth) {
		throw new NotImplementedException("");
		
	}

	@Override
	public void setFutureHeight(int futureHeight) {
		throw new NotImplementedException("");
		
	}

	@Override
	public boolean is_descendent_of(Box b) {
		throw new NotImplementedException("");

	}

	@Override
	public int atomic_width() {
		throw new NotImplementedException("");

	}

	@Override
	public int bl_shift() {
		throw new NotImplementedException("");

	}

	@Override
	public int contentLeft() {
		throw new NotImplementedException("");

	}

	@Override
	public int contentRight() {
		throw new NotImplementedException("");

	}

	@Override
	public int contentTop() {
		throw new NotImplementedException("");

	}

	@Override
	public int contentBottom() {
		throw new NotImplementedException("");

	}

	@Override
	public int contentWidth() {
		throw new NotImplementedException("");

	}

	@Override
	public int contentHeight() {
		throw new NotImplementedException("");

	}

	@Override
	public int paddingLeft() {
		throw new NotImplementedException("");

	}

	@Override
	public int paddingRight() {
		throw new NotImplementedException("");

	}

	@Override
	public int paddingTop() {
		throw new NotImplementedException("");

	}

	@Override
	public int paddingBottom() {
		throw new NotImplementedException("");

	}

	@Override
	public int marginLeft() {
		throw new NotImplementedException("");

	}

	@Override
	public int marginTop() {
		throw new NotImplementedException("");

	}

	@Override
	public int marginRight() {
		throw new NotImplementedException("");

	}

	@Override
	public int marginBottom() {
		throw new NotImplementedException("");

	}

	@Override
	public Border borderLeft() {
		throw new NotImplementedException("");

	}

	@Override
	public Border borderRight() {
		throw new NotImplementedException("");

	}

	@Override
	public Border borderTop() {
		throw new NotImplementedException("");

	}

	@Override
	public Border borderBottom() {
		throw new NotImplementedException("");

	}

	@Override
	public void set_margins(int left, int top, int right, int bottom) {
		throw new NotImplementedException("");
		
	}

	@Override
	public void applyMinMaxConstraints(Length width, Length height, Length marginLeft, Length marginRight,
			Length minWidth, Length maxWidth, Length minHeight, Length maxHeight) {
		throw new NotImplementedException("");
		
	}

	@Override
	public int floating_boxes() {
		throw new NotImplementedException("");

	}

	@Override
	public int flowing_boxes() {
		throw new NotImplementedException("");

	}

	@Override
	public void flow_back(Inline il) {
		throw new NotImplementedException("");
		
	}

	@Override
	public void flow_insert(Box b, int before) {
		throw new NotImplementedException("");
		
	}

	@Override
	public void flow_back(String str, SpaceFlag space) {
		throw new NotImplementedException("");
		
	}

	@Override
	public void float_back(FloatBox b) {
		throw new NotImplementedException("");
		
	}

	@Override
	public void float_insert(FloatBox b, int before) {
		throw new NotImplementedException("");
		
	}

	@Override
	public void table_back(TableMember cont, TableMember memb) {
		throw new NotImplementedException("");
		
	}

	@Override
	public void table_insert(TableMember cont, TableMember memb) {
		throw new NotImplementedException("");
		
	}

	@Override
	public void absBackStatic(AbsoluteBox b) {
		throw new NotImplementedException("");
		
	}

	@Override
	public void absBack(AbsoluteBox b) {
		throw new NotImplementedException("");
		
	}

	@Override
	public Direction direction() {
		throw new NotImplementedException("");

	}

	@Override
	public Box root() {
		throw new NotImplementedException("");

	}

	@Override
	public boolean explicit_newline() {
		throw new NotImplementedException("");

	}

	@Override
	public FlowContext get_flow_context() {
		throw new NotImplementedException("");

	}

	@Override
	public Box get_flow_context_box() {
		throw new NotImplementedException("");

	}

	@Override
	public boolean stretchy_cwidth() {
		throw new NotImplementedException("");

	}

	@Override
	public boolean stretchy_cheight() {
		throw new NotImplementedException("");

	}

	@Override
	public int can_stretch(int amount) {
		throw new NotImplementedException("");

	}

	@Override
	public int can_stretch_vert(int amount) {
		throw new NotImplementedException("");

	}

	@Override
	public boolean stiffy_height() {
		throw new NotImplementedException("");

	}

	@Override
	public int max_stiffy_height() {
		throw new NotImplementedException("");

	}

	@Override
	public boolean isAutoWidth() {
		throw new NotImplementedException("");

	}

	@Override
	public boolean isAutoHeight() {
		throw new NotImplementedException("");

	}

	@Override
	public int canStretchHeight(int amount) {
		throw new NotImplementedException("");

	}

	@Override
	public ReplaceableBoxPlugin replace_object() {
		throw new NotImplementedException("");

	}

	@Override
	public void calculateStaticPosition(AbsoluteBox newMember) {
		throw new NotImplementedException("");
		
	}

	@Override
	public Font getFont() {
		throw new NotImplementedException("");
	}

	@Override
	public boolean isInline() {
		throw new NotImplementedException("");
	}

	@Override
	public void shift_vertical_position(int amount) {
		throw new NotImplementedException("");
	}

	@Override
	public void flow_batch_back(List<String> strs, List<SpaceFlag> spaces) {
		throw new NotImplementedException("");
	}

	@Override
	public void flow_insert(String str, SpaceFlag space, int before) {
		throw new NotImplementedException("");
	}

	@Override
	public void calculate_vert_position(AtomicInline place, Line on, LineInfo baseline_out) {
		throw new NotImplementedException("");
	}

	@Override
	public int inline_top_content(Line on) {
		throw new NotImplementedException("");
	}

	@Override
	public int inline_bottom_content(Line on) {
		throw new NotImplementedException("");
	}

	@Override
	public int workOutLineHeight(AtomicInline ainl, Line on) {
		throw new NotImplementedException("");
	}

	@Override
	public BlockBox block() {
		throw new NotImplementedException("");
	}

	@Override
	public AtomicInline front_atomic() {
		throw new NotImplementedException("");
	}

	@Override
	public AtomicInline back_atomic() {
		throw new NotImplementedException("");
	}

	@Override
	public Flowspace flowspace() {
		throw new NotImplementedException("");
	}

	@Override
	public void reposition_on_line(Line line) {
		throw new NotImplementedException("");
	}

	@Override
	public InlineBox inline_root() {
		throw new NotImplementedException("");
	}

	@Override
	public InlineBox aligned_subtree_root() {
		throw new NotImplementedException("");
	}

	@Override
	public void draw_borders(int offx, int offy, Graphics2D graphics) {
		throw new NotImplementedException("");
	}

	@Override
	public void draw_inline_border(int left, int top, int right, int bottom, boolean leftside, boolean rightside,
			Graphics2D graphics) {
		throw new NotImplementedException("");
	}

	@Override
	public void calculate_static_position(int pos) {
		throw new NotImplementedException("");
	}

	@Override
	public boolean marginsCollapseThrough() {
		throw new NotImplementedException("");
	}

	@Override
	public List<Inline> getMembersInline() {
		throw new NotImplementedException("");
	}

	@Override
	public int dummy_atomic_baseline(Line on, VerticalAlignment vert) {
		throw new NotImplementedException("");
	}

	@Override
	public int top_from_baseline(AtomicInline ainl, int baseline) {
		throw new NotImplementedException("");
	}

	@Override
	public int baseline_from_middle(AtomicInline ainl, int middle) {
		throw new NotImplementedException("");
	}

	@Override
	public int bottom_from_baseline(AtomicInline ainl, int baseline) {
		throw new NotImplementedException("");
	}

	@Override
	public int baseline_from_bottom(AtomicInline ainl, int bottom) {
		throw new NotImplementedException("");
	}

	@Override
	public int baseline_from_bottom_for_bl(AtomicInline ainl, int bottom) {
		throw new NotImplementedException("");

	}

	@Override
	public void setFont(Font font) {
		throw new NotImplementedException("");
	}

	@Override
	public void setRelLeft(int relLeft) {
		throw new NotImplementedException("");
	}

	@Override
	public void setRelTop(int relTop) {
		throw new NotImplementedException("");
	}

	@Override
	public void setForegroundColour(Color foregroundColour) {
		throw new NotImplementedException("");
	}

	@Override
	public Color getForegroundColour() {
		throw new NotImplementedException("");
	}

	@Override
	public void setLineHeight(int lineHeight) {
		throw new NotImplementedException("");
	}

	@Override
	public int getLineHeight() {
		throw new NotImplementedException("");
	}

	@Override
	public void setVerticalAlign(VerticalAlignment verticalAlign) {
		throw new NotImplementedException("");
	}

	@Override
	public VerticalAlignment getVerticalAlign() {
		throw new NotImplementedException("");
	}

	@Override
	public void setWordSpacing(int wordSpacing) {
		throw new NotImplementedException("");
	}

	@Override
	public void setLetterSpacing(int letterSpacing) {
		throw new NotImplementedException("");
	}

	@Override
	public void setBorders(Border[] borders) {
		throw new NotImplementedException("");
	}

	@Override
	public void setPaddings(int paddingTopWidth, int paddingBottomWidth, int paddingLeftWidth, int paddingRightWidth) {
		throw new NotImplementedException("");
	}
}
