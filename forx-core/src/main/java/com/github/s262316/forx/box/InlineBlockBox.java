package com.github.s262316.forx.box;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.github.s262316.forx.box.cast.BoxTypes;
import com.github.s262316.forx.box.properties.DimensionsDescriptor;
import com.github.s262316.forx.box.properties.HasAbsolutePosition;
import com.github.s262316.forx.box.properties.HasBorders;
import com.github.s262316.forx.box.properties.HasColour;
import com.github.s262316.forx.box.properties.HasFontProperties;
import com.github.s262316.forx.box.properties.HasLineProperties;
import com.github.s262316.forx.box.properties.HasTextProperties;
import com.github.s262316.forx.box.properties.HasWordProperties;
import com.github.s262316.forx.box.properties.MarginDescriptor;
import com.github.s262316.forx.box.properties.PropertiesInjector;
import com.github.s262316.forx.box.properties.PropertyBoxAdaptor;
import com.github.s262316.forx.box.properties.Visual;
import com.github.s262316.forx.box.relayouter.LayoutResult;
import com.github.s262316.forx.box.relayouter.Relayouter;
import com.github.s262316.forx.box.relayouter.util.LayoutUtils;
import com.github.s262316.forx.box.util.Border;
import com.github.s262316.forx.box.util.Direction;
import com.github.s262316.forx.box.util.Length;
import com.github.s262316.forx.box.util.SizeResult;
import com.github.s262316.forx.box.util.SpaceFlag;
import com.github.s262316.forx.box.util.SpecialLength;
import com.github.s262316.forx.box.util.TextAlign;
import com.github.s262316.forx.box.util.VerticalAlignment;
import com.github.s262316.forx.graphics.Triangle;
import com.github.s262316.forx.tree.visual.AnonReason;
import com.google.common.base.Optional;

public class InlineBlockBox extends AtomicInline implements Box, HasFontProperties, HasBorders,
HasTextProperties, HasWordProperties, HasLineProperties,
		HasColour, HasAbsolutePosition
{
	private int id;
	private int _bl_shift;
	private VerticalAlignment verticalAlign;

	private int textIndent;
	private TextAlign textAlign;
	private int lineHeight;
	private int letterSpacing;
	private int wordSpacing;
	private Color foregroundColour;

	private int paddingLeftSize, paddingTopSize, paddingRightSize, paddingBottomSize;
	private int marginLeftSize, marginTopSize, marginRightSize, marginBottomSize;
	private Border borders[] = new Border[4];

	private boolean _stretchy_cwidth;
	private boolean _auto_height;
	private boolean _auto_width;

	private LinkedList<Layable> all;
	private LinkedList<Box> dummy;
	private LinkedList<FloatBox> dummy_float;

	private Visual visual;
	private Font font;

	private Graphics2D _canvas;

	public InlineBlockBox(Visual visual, Drawer drawer)
	{
	}

	private void populate_block_root()
	{
		InlineBlockRootBox ibrb;

		ibrb = visual.createAnonInlineBlockRootBox(AnonReason.INLINE_BLOCK_ROOT_CONTAINER);

		all.add(ibrb);
		ibrb.set_container(this);
		ibrb.computeProperties();
		LayoutUtils.doLoadingLayout(ibrb);
	}

	@Override
	public void flow_back(Box b)
	{
		if (all.size() == 0)
			populate_block_root();

		BoxTypes.toBox(all.getFirst()).flow_back(b);
	}

	@Override
	public void flow_back(Inline i)
	{
		if (all.size() == 0)
			populate_block_root();

		BoxTypes.toBox(all.getFirst()).flow_back(i);
	}

	@Override
	public void flow_insert(Box b, int before)
	{
		if (all.size() == 0)
			populate_block_root();

		BoxTypes.toBox(all.getFirst()).flow_insert(b, before);
	}

	@Override
	public void flow_back(String str, SpaceFlag space)
	{
		if (all.size() == 0)
			populate_block_root();

		BoxTypes.toBox(all.getFirst()).flow_back(str, space);
	}

	@Override
	public void table_back(TableMember cont, TableMember memb)
	{
		if (all.size() == 0)
			populate_block_root();

		BoxTypes.toBox(all.getFirst()).table_back(cont, memb);
	}

	@Override
	public void absBackStatic(AbsoluteBox b)
	{
		if (all.size() == 0)
			populate_block_root();

		BoxTypes.toBlockBox(all.getFirst()).absBackStatic(b);
	}

	@Override
	public void absBack(AbsoluteBox b)
	{
		if (all.size() == 0)
			populate_block_root();

		BoxTypes.toBlockBox(all.getFirst()).absBack(b);
	}

	@Override
	public void float_back(FloatBox b)
	{
		if (all.size() == 0)
			populate_block_root();

		BoxTypes.toBox(all.getFirst()).float_back(b);
	}

	@Override
	public void float_insert(FloatBox b, int before)
	{
		if (all.size() == 0)
			populate_block_root();

		BoxTypes.toBox(all.getFirst()).float_insert(b, before);
	}

	@Override
	public int flowing_boxes()
	{
		return 0;
	}

	@Override
	public int floating_boxes()
	{
		return 0;
	}

	@Override
	public int bl_shift()
	{
		return _bl_shift;
	}

	@Override
	public int atomic_width()
	{
		return width();
	}

	@Override
	public boolean explicit_newline()
	{
		return false;
	}

	@Override
	public Font getFont()
	{
		return font;
	}

	@Override
	public void computeProperties()
	{
		PropertiesInjector.inject(visual, new PropertyBoxAdaptor(this), this, null, null, this, this, null, null, this, this, null, this, this); 
	}
	
	// @Override
	// public void compute_properties()
	// {
	// Font f;
	// BorderDescriptor bd=new BorderDescriptor();
	// TextProperties tp=new TextProperties();
	// WordProperties wp=new WordProperties();
	// LineDescriptor ld=new LineDescriptor();
	// ColourDescriptor cd=new ColourDescriptor();
	// PositionDescriptor pd=new PositionDescriptor();
	// int space_width=0, space_height=0;
	//
	// // important to get this before any other properties
	// f=visual.workOutFontProperties(this);
	// set_font(f);
	//
	// visual.calculateBorders(this, bd);
	// visual.workOutTextProperties(this, tp);
	// visual.workOutWordProperties(this, wp);
	// visual.workOutLineProperties(this, ld);
	// visual.workoutColours(this, cd);
	//
	// if(is_relative()==true)
	// {
	// visual.workOutAbsolutePosition(this, pd);
	// if(pd.left.specified==SpecialLength.SL_AUTO &&
	// pd.right.specified==SpecialLength.SL_SPECIFIED)
	// set_rel_left(-pd.right.value);
	// if(pd.right.specified==SpecialLength.SL_AUTO &&
	// pd.left.specified==SpecialLength.SL_SPECIFIED)
	// set_rel_left(pd.left.value);
	//
	// if(pd.top.specified==SpecialLength.SL_AUTO &&
	// pd.bottom.specified==SpecialLength.SL_SPECIFIED)
	// set_rel_top(-pd.bottom.value);
	// if(pd.bottom.specified==SpecialLength.SL_AUTO &&
	// pd.top.specified==SpecialLength.SL_SPECIFIED)
	// set_rel_top(pd.top.value);
	// }
	//
	// set_borders(bd);
	//
	// _text_indent=tp.text_indent;
	// _text_align=tp.text_align;
	//
	// _line_height=(int)ld.lineHeight;
	//
	// _vertical_align=ld.verticalAlign;
	// if(_vertical_align.specified==VerticalAlignmentSpecial.VAS_SUPER)
	// _bl_shift=-FontUtils.x_height(f, canvas());
	// else if(_vertical_align.specified==VerticalAlignmentSpecial.VAS_SUB)
	// _bl_shift=FontUtils.x_height(f, canvas());
	// else if(_vertical_align.specified==VerticalAlignmentSpecial.VAS_LENGTH)
	// _bl_shift=-_vertical_align.value;
	//
	// _letter_spacing=wp.letter_spacing;
	// // if(wp.word_spacing==SpecialLength.SL_AUTO)
	// // _word_spacing=0;
	// // else
	// _word_spacing=wp.word_spacing;
	//
	// _foreground_colour=cd.foreground;
	// }

	@Override
	public void set_margins(int left, int top, int right, int bottom)
	{
		set_margins(left, top, right, bottom);
	}

	@Override
	@Deprecated
	public int content_height()
	{
		return contentHeight();
	}

	@Override
	@Deprecated
	public int content_top()
	{
		return contentTop();
	}

	@Override
	@Deprecated
	public int content_bottom()
	{
		return contentBottom();
	}

	@Override
	@Deprecated
	public int content_width()
	{
		return contentWidth();
	}

	@Override
	public SizeResult compute_dimensions()
	{
		SizeResult result = new SizeResult();
		MarginDescriptor md = new MarginDescriptor();
		DimensionsDescriptor dd = new DimensionsDescriptor();

		visual.workOutFlowDimensions(new PropertyBoxAdaptor(this), dd);
		visual.computeMarginProperties(new PropertyBoxAdaptor(this), md);

		if (dd.width.specified == SpecialLength.SL_AUTO)
			_stretchy_cwidth = true;
		else
			_stretchy_cwidth = false;

		// work out the width
		calculateWidth(dd.width, md.left, md.right);

		result.width = dd.width;

		// work out the height
		// height value DOES NOT include the borders/margins/padding
		calculateHeight(dd.height, md.top, md.bottom, dd.width);

		// not sure about this, but if the width is the same as last time, then
		// use
		// the height from last time too. this is because we're infinite looping
		// when
		// an auto height is reset to 0 and the relayout is recalled which
		// resizes
		// and relayouts, etc... might need to take margins into account
		if (result.width.value == contentWidth())
			result.height.set(contentHeight());
		else
			result.height.set(dd.height);

		result.marginLeft.set(md.left);
		result.marginTop.set(md.top);
		result.marginRight.set(md.right);
		result.marginBottom.set(md.bottom);

		return result;
	}

	private void calculateWidth(Length width, Length marginLeft, Length marginRight)
	{
		// A computed value of 'auto' for 'margin-left' or 'margin-right'
		// becomes a used value of '0'
		if (marginLeft.specified == SpecialLength.SL_AUTO)
			marginLeft.set(0);
		if (marginRight.specified == SpecialLength.SL_AUTO)
			marginRight.set(0);

		// If 'width' is 'auto', the used value is the shrink-to-fit width as
		// for floating elements.
		if (width.specified == SpecialLength.SL_AUTO)
		{
			if (all.size() > 0)
				width.value = ((InlineBlockRootBox) all.getFirst()).shrink_to_fit(width, marginLeft, marginRight);
			else
				width.set(0);
		}
	}

	private void calculateHeight(Length height, Length marginTop, Length marginBottom, Length width)
	{
		// If 'margin-top', or 'margin-bottom' are 'auto', their used value is
		// 0. If 'height'
		// is 'auto', the height depends on the element's descendants

		// For 'inline-block' elements, the margin box is used when calculating
		// the height
		// of the line box
		if (marginTop.specified == SpecialLength.SL_AUTO)
			marginTop.set(0);
		if (marginBottom.specified == SpecialLength.SL_AUTO)
			marginBottom.set(0);

		if (height.specified == SpecialLength.SL_AUTO)
		{
			height.set(0);
			_auto_height = true;
		}
	}

	@Override
	public LayoutResult calculate_position(Layable newMember)
	{
		SizeResult size;
		BlockBox bb;

		bb = BoxTypes.toBlockBox(newMember);

		size = bb.compute_dimensions();
		bb.set_dimensions(size.width.value, size.height.value);
		bb.set_margins(size.marginLeft.value, size.marginTop.value, size.marginRight.value, size.marginBottom.value);

		bb.set_position(contentLeft(), contentTop());

		return new LayoutResult(true, Optional.<Relayouter> absent());
	}

	@Override
	public void uncalculate_position(Layable member)
	{
	}

	public void draw_borders(int offx, int offy, Graphics2D graphics)
	{
		// top
		Dimensionable top_dims = new Dimensionable(left() + marginLeft(), top() + marginTop(), width() - marginLeft() - marginRight(), borderLeft().width);

		// right
		Dimensionable right_dims = new Dimensionable(right() - marginRight() - borderRight().width + 1, top() + marginTop(), borderRight().width, height()
				- marginTop() - marginBottom());

		// bottom
		Dimensionable bottom_dims = new Dimensionable(left() + marginLeft(), bottom() - marginBottom() - borderBottom().width + 1, width() - marginLeft()
				- marginRight(), borderBottom().width);

		// left
		Dimensionable left_dims = new Dimensionable(left() + marginLeft(), top() + marginTop(), borderLeft().width, height() - marginTop() - marginBottom());

		Polygon top_left_clip = Triangle.newTriangle(top_dims.left(), top_dims.top(), left_dims.right(), top_dims.top(), left_dims.right(), top_dims.bottom());

		Polygon top_right_clip = Triangle.newTriangle(right_dims.left(), top_dims.top(), right_dims.right(), top_dims.top(), right_dims.left(),
				top_dims.bottom());

		Polygon left_top_clip = Triangle.newTriangle(left_dims.left(), left_dims.top(), left_dims.right(), top_dims.bottom(), left_dims.left(),
				top_dims.bottom());

		Polygon left_bottom_clip = Triangle.newTriangle(left_dims.left(), bottom_dims.top(), left_dims.right(), bottom_dims.top(), left_dims.left(),
				left_dims.bottom());

		Polygon right_top_clip = Triangle.newTriangle(right_dims.right(), right_dims.top(), right_dims.right(), top_dims.bottom(), right_dims.left(),
				top_dims.bottom());

		Polygon right_bottom_clip = Triangle.newTriangle(right_dims.left(), bottom_dims.top(), right_dims.right(), right_dims.bottom(), right_dims.right(),
				bottom_dims.top());

		Polygon bottom_left_clip = Triangle.newTriangle(left_dims.right(), bottom_dims.top(), left_dims.right(), bottom_dims.bottom(), bottom_dims.left(),
				bottom_dims.bottom());

		Polygon bottom_right_clip = Triangle.newTriangle(right_dims.left(), bottom_dims.top(), bottom_dims.right(), bottom_dims.bottom(), right_dims.left(),
				bottom_dims.bottom());

		switch (borderTop().style)
		{
			case BS_NONE:
				break;
			case BS_HIDDEN:
				break;
			case BS_DOTTED:
				graphics.setColor(borderTop().colour);
				BoxBorders.drawHorizBorderDotted(top_dims, graphics, left_dims, right_dims, top_left_clip, top_right_clip);
				break;
			case BS_DASHED:
				graphics.setColor(borderTop().colour);
				BoxBorders.drawHorizBorderDashed(top_dims, graphics, left_dims, right_dims, top_left_clip, top_right_clip);
				break;
			case BS_SOLID:
				graphics.setColor(borderTop().colour);
				BoxBorders.drawTopBorderSolid(top_dims, graphics, left_dims, right_dims, top_left_clip, top_right_clip);
				break;
			case BS_DOUBLE:
				graphics.setColor(borderTop().colour);
				BoxBorders.drawHorizBorderDouble(top_dims, graphics, left_dims, right_dims, top_left_clip, top_right_clip);
				break;
			case BS_GROOVE:
				graphics.setColor(borderTop().colour);
				BoxBorders.drawHorizBorderGroove(top_dims, graphics, left_dims, right_dims, top_left_clip, top_right_clip);
				break;
			case BS_RIDGE:
				graphics.setColor(borderTop().colour);
				BoxBorders.drawHorizBorderRidge(top_dims, graphics, left_dims, right_dims, top_left_clip, top_right_clip);
				break;
			case BS_INSET:
				graphics.setColor(borderTop().colour);
				BoxBorders.drawTopBorderInset(top_dims, graphics, left_dims, right_dims, top_left_clip, top_right_clip);
				break;
			case BS_OUTSET:
				graphics.setColor(borderTop().colour);
				BoxBorders.drawTopBorderOutset(top_dims, graphics, left_dims, right_dims, top_left_clip, top_right_clip);
				break;
		}

		switch (borderRight().style)
		{
			case BS_NONE:
				break;
			case BS_HIDDEN:
				break;
			case BS_DOTTED:
				graphics.setColor(borderRight().colour);
				BoxBorders.drawVertBorderDotted(right_dims, graphics, top_dims, bottom_dims, right_top_clip, right_bottom_clip);
				break;
			case BS_DASHED:
				graphics.setColor(borderRight().colour);
				BoxBorders.drawVertBorderDashed(right_dims, graphics, top_dims, bottom_dims, right_top_clip, right_bottom_clip);
				break;
			case BS_SOLID:
				graphics.setColor(borderRight().colour);
				BoxBorders.drawRightBorderSolid(right_dims, graphics, top_dims, bottom_dims, right_top_clip, right_bottom_clip);
				break;
			case BS_DOUBLE:
				graphics.setColor(borderRight().colour);
				BoxBorders.drawVertBorderDouble(right_dims, graphics, top_dims, bottom_dims, right_top_clip, right_bottom_clip);
				break;
			case BS_GROOVE:
				graphics.setColor(borderRight().colour);
				BoxBorders.drawVertBorderGroove(right_dims, graphics, top_dims, bottom_dims, right_top_clip, right_bottom_clip);
				break;
			case BS_RIDGE:
				graphics.setColor(borderRight().colour);
				BoxBorders.drawVertBorderRidge(right_dims, graphics, top_dims, bottom_dims, right_top_clip, right_bottom_clip);
				break;
			case BS_INSET:
				graphics.setColor(borderRight().colour);
				BoxBorders.drawRightBorderInset(right_dims, graphics, top_dims, bottom_dims, right_top_clip, right_bottom_clip);
				break;
			case BS_OUTSET:
				graphics.setColor(borderRight().colour);
				BoxBorders.drawRightBorderOutset(right_dims, graphics, top_dims, bottom_dims, right_top_clip, right_bottom_clip);
				break;
		}

		switch (borderBottom().style)
		{
			case BS_NONE:
				break;
			case BS_HIDDEN:
				break;
			case BS_DOTTED:
				graphics.setColor(borderBottom().colour);
				BoxBorders.drawHorizBorderDotted(bottom_dims, graphics, left_dims, right_dims, bottom_left_clip, bottom_right_clip);
				break;
			case BS_DASHED:
				graphics.setColor(borderBottom().colour);
				BoxBorders.drawHorizBorderDashed(bottom_dims, graphics, left_dims, right_dims, top_left_clip, top_right_clip);
				break;
			case BS_SOLID:
				graphics.setColor(borderBottom().colour);
				BoxBorders.drawBottomBorderSolid(bottom_dims, graphics, left_dims, right_dims, bottom_left_clip, bottom_right_clip);
				break;
			case BS_DOUBLE:
				graphics.setColor(borderBottom().colour);
				BoxBorders.drawHorizBorderDouble(bottom_dims, graphics, left_dims, right_dims, bottom_left_clip, bottom_right_clip);
				break;
			case BS_GROOVE:
				graphics.setColor(borderBottom().colour);
				BoxBorders.drawHorizBorderGroove(bottom_dims, graphics, left_dims, right_dims, bottom_left_clip, bottom_right_clip);
				break;
			case BS_RIDGE:
				graphics.setColor(borderBottom().colour);
				BoxBorders.drawHorizBorderRidge(bottom_dims, graphics, left_dims, right_dims, bottom_left_clip, bottom_right_clip);
				break;
			case BS_INSET:
				graphics.setColor(borderBottom().colour);
				BoxBorders.drawBottomBorderInset(bottom_dims, graphics, left_dims, right_dims, bottom_left_clip, bottom_right_clip);
				break;
			case BS_OUTSET:
				graphics.setColor(borderBottom().colour);
				BoxBorders.drawBottomBorderOutset(bottom_dims, graphics, left_dims, right_dims, bottom_left_clip, bottom_right_clip);
				break;
		}

		switch (borderLeft().style)
		{
			case BS_NONE:
				break;
			case BS_HIDDEN:
				break;
			case BS_DOTTED:
				graphics.setColor(borderLeft().colour);
				BoxBorders.drawVertBorderDotted(left_dims, graphics, top_dims, bottom_dims, right_top_clip, right_bottom_clip);
				break;
			case BS_DASHED:
				graphics.setColor(borderLeft().colour);
				BoxBorders.drawVertBorderDashed(left_dims, graphics, top_dims, bottom_dims, right_top_clip, right_bottom_clip);
				break;
			case BS_SOLID:
				graphics.setColor(borderLeft().colour);
				BoxBorders.drawLeftBorderSolid(left_dims, graphics, top_dims, bottom_dims, left_top_clip, left_bottom_clip);
				break;
			case BS_DOUBLE:
				graphics.setColor(borderLeft().colour);
				BoxBorders.drawVertBorderDouble(left_dims, graphics, top_dims, bottom_dims, left_top_clip, left_bottom_clip);
				break;
			case BS_GROOVE:
				graphics.setColor(borderLeft().colour);
				BoxBorders.drawVertBorderGroove(left_dims, graphics, top_dims, bottom_dims, left_top_clip, left_bottom_clip);
				break;
			case BS_RIDGE:
				graphics.setColor(borderLeft().colour);
				BoxBorders.drawVertBorderRidge(left_dims, graphics, top_dims, bottom_dims, left_top_clip, left_bottom_clip);
				break;
			case BS_INSET:
				graphics.setColor(borderLeft().colour);
				BoxBorders.drawLeftBorderInset(left_dims, graphics, top_dims, bottom_dims, left_top_clip, left_bottom_clip);
				break;
			case BS_OUTSET:
				graphics.setColor(borderLeft().colour);
				BoxBorders.drawLeftBorderOutset(left_dims, graphics, top_dims, bottom_dims, left_top_clip, left_bottom_clip);
				break;
		}
	}

	@Override
	public boolean stretchy_cwidth()
	{
		return _stretchy_cwidth;
	}

	
	
	@Override
	public boolean stretchy_cheight() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int can_stretch_vert(int amount) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int can_stretch(int amount)
	{
		if (_stretchy_cwidth == false)
			throw new BoxError(BoxExceptionType.BET_UNKNOWN);

		if (container().stretchy_cwidth() == true)
			return container().can_stretch(amount);

		int max_stretch = container().contentWidth() - width();
		// marginLeft()-marginRight()-
		// borderLeft().width-borderRight().width-
		// paddingLeft()-paddingRight();

		return Math.min(max_stretch, amount);
	}

	@Override
	public int stretchAmountWidth(Layable whichChild, int maxExtraAmount)
	{
		if (stretchy_cwidth())
		{
			int amount = can_stretch(maxExtraAmount);
			if (amount > 0)
				return amount;
		}

		return 0;
	}
	
	@Override
	public int canStretchHeight(int amount)
	{
		// space without resizing anything
		int currentAvailSpace=container().contentBottom() - bottom();

		if(currentAvailSpace >= amount)
			return amount;
		else
		{
			if(isAutoHeight())
			{
				amount-=currentAvailSpace;
				currentAvailSpace+=container().canStretchHeight(amount);
			}
		}
		
		return currentAvailSpace;
	}  	

	@Override
	public void setFutureWidth(int futureWidth)
	{
		// TODO Auto-generated method stub

	}

	
	
	// @Override
	// public Layable resize_height(int from, int amount)
	// {
	// Layable requester=all.get(from);
	// Layable earliest_affected=null;
	//
	// // force the change in height
	// requester.change_height(amount);
	//
	// if(requester.bottom() > contentBottom() && stiffy_height()==false)
	// earliest_affected=container().resize_height(container().getMembersAll().indexOf(this),
	// requester.bottom() - contentBottom());
	//
	// return earliest_affected;
	// }
	//
	// @Override
	// public Box resize_width(int from, int amount)
	// {
	// Layable requester=all.get(from);
	// Box earliest_affected;
	//
	// // force the change in width
	// requester.change_width(amount);
	//
	// if(requester.width() > contentWidth())
	// {
	// change_width(requester.width() - contentWidth());
	// }
	//
	// // layout from beginning of inline context - there is not a good way
	// // of getting rid of the defunct possibly tall line
	// earliest_affected=BoxTypes.toInlineBox(container()).inline_root();
	//
	// BoxTypes.toBox(all.getFirst()).get_flow_context().reset(contentLeft(),
	// contentRight(), contentTop());
	//
	// return earliest_affected;
	// }

	@Override
	public void calculateStaticPosition(AbsoluteBox newMember)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void setFont(Font font)
	{
		this.font = font;
	}

	@Override
	public int stretchAmountHeight(Layable whichChild, int y, int maxExtraAmount)
	{
		return maxExtraAmount;
	}

	@Override
	public void setFutureHeight(int futureHeight)
	{

	}

	@Override
	public Box getContainer()
	{
		return this.container();
	}

	@Override
	public boolean isAutoHeight()
	{
		return _auto_height;
	}

	@Override
	public boolean isAutoWidth()
	{
		return _auto_width;
	}

	@Override
	public int max_stiffy_height()
	{
		if (_auto_height == true)
			throw new BoxError(BoxExceptionType.BET_WRONGTYPE);

		return contentHeight();
	}

	@Override
	public boolean stiffy_height()
	{
		return !_auto_height;
	}

	@Override
	public Box get_flow_context_box()
	{
		return container().get_flow_context_box();
	}

	@Override
	public FlowContext get_flow_context()
	{
		return container().get_flow_context();
	}

	@Override
	public Box root()
	{
		Box up = this;

		while (up.container() != null)
			up = up.container();

		return up;
	}

	@Override
	public Direction direction()
	{
		return Direction.DIR_LTR;
	}// return _direction;}

	@Override
	public void table_insert(TableMember cont, TableMember memb)
	{
		throw new BoxError(BoxExceptionType.BET_WRONGTYPE);
	}

	@Override
	public void applyMinMaxConstraints(Length width, Length height, Length marginLeft, Length marginRight, Length minWidth, Length maxWidth, Length minHeight,
			Length maxHeight)
	{
	}

	@Override
	public int contentLeft()
	{
		if (left() == Dimensionable.INVALID)
			return Dimensionable.INVALID;

		return left() + paddingLeft() + marginLeft() + borderLeft().width;
	}

	@Override
	public int contentRight()
	{
		if (right() == Dimensionable.INVALID)
			return Dimensionable.INVALID;

		return right() - paddingLeft() - marginRight() - borderRight().width;
	}

	@Override
	public int contentTop()
	{
		if (top() == Dimensionable.INVALID)
			return Dimensionable.INVALID;

		return top() + paddingTop() + marginTop() + borderTop().width;
	}

	@Override
	public int contentBottom()
	{
		if (bottom() == Dimensionable.INVALID)
			return Dimensionable.INVALID;

		return bottom() - paddingBottom() - marginBottom() - borderBottom().width;
	}

	@Override
	public int contentWidth()
	{
		if (width() == Dimensionable.INVALID)
			return Dimensionable.INVALID;

		return width() - paddingLeft() - paddingRight() - borderLeft().width - borderRight().width - marginLeft() - marginRight();
	}

	@Override
	public int contentHeight()
	{
		if (height() == Dimensionable.INVALID)
			return Dimensionable.INVALID;

		return height() - paddingTop() - paddingBottom() - borderTop().width - borderBottom().width - marginTop() - marginBottom();
	}

	@Override
	public int paddingLeft()
	{
		return paddingLeftSize;
	}

	@Override
	public int paddingRight()
	{
		return paddingRightSize;
	}

	@Override
	public int paddingTop()
	{
		return paddingTopSize;
	}

	@Override
	public int paddingBottom()
	{
		return paddingBottomSize;
	}

	@Override
	public int marginLeft()
	{
		return marginLeftSize;
	}

	@Override
	public int marginTop()
	{
		return marginTopSize;
	}

	@Override
	public int marginRight()
	{
		return marginRightSize;
	}

	@Override
	public int marginBottom()
	{
		return marginBottomSize;
	}

	@Override
	public Border borderLeft()
	{
		return borders[Border.BORDER_LEFT];
	}

	@Override
	public Border borderRight()
	{
		return borders[Border.BORDER_RIGHT];
	}

	@Override
	public Border borderTop()
	{
		return borders[Border.BORDER_TOP];
	}

	@Override
	public Border borderBottom()
	{
		return borders[Border.BORDER_BOTTOM];
	}

	@Override
	public List<Layable> getMembersAll()
	{
		return Collections.unmodifiableList(all);
	}

	@Override
	public List<Box> getMembersFlowing()
	{
		return null;
	}

	@Override
	public List<FloatBox> getMembersFloating()
	{
		return Collections.unmodifiableList(dummy_float);
	}

	@Override
	public List<AbsoluteBox> getMembersPositioned()
	{
		return null;
	}

	@Override
	public Visual getVisual()
	{
		return visual;
	}

	@Override
	public boolean flows()
	{
		return true;
	}

	@Override
	public int getId()
	{
		return id;
	}

	@Override
	public void setId(int id)
	{
		this.id = id;
	}

	@Override
	public void setForegroundColour(Color foregroundColour)
	{
		this.foregroundColour=foregroundColour;
	}

	@Override
	public void setLineHeight(int lineHeight)
	{
		this.lineHeight=lineHeight;
	}

	@Override
	public void setVerticalAlign(VerticalAlignment verticalAlign)
	{
		this.verticalAlign=verticalAlign;
	}

	@Override
	public VerticalAlignment getVerticalAlign()
	{
		return verticalAlign;
	}

	@Override
	public void setWordSpacing(int wordSpacing)
	{
		this.wordSpacing=wordSpacing;
	}

	@Override
	public void setLetterSpacing(int letterSpacing)
	{
		this.letterSpacing=letterSpacing;
	}

	@Override
	public void setTextIndent(int textIndent)
	{
		this.textIndent=textIndent;
	}

	@Override
	public void setTextAlign(TextAlign textAlign)
	{
		this.textAlign=textAlign;
	}

	@Override
	public void setBorders(Border[] borders)
	{
		this.borders=borders;
	}

	@Override
	public void setPaddings(int paddingTopWidth, int paddingBottomWidth, int paddingLeftWidth, int paddingRightWidth)
	{
		this.paddingTopSize=paddingTopWidth;
		this.paddingBottomSize=paddingBottomWidth;
		this.paddingLeftSize=paddingLeftWidth;
		this.paddingRightSize=paddingRightWidth;		
	}
}


