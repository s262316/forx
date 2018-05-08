package com.github.s262316.forx.box;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeSet;

import com.google.common.base.MoreObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.box.cast.BoxTypes;
import com.github.s262316.forx.box.properties.DimensionsDescriptor;
import com.github.s262316.forx.box.properties.HasAbsolutePosition;
import com.github.s262316.forx.box.properties.HasBackgroundProperties;
import com.github.s262316.forx.box.properties.HasBlockProperties;
import com.github.s262316.forx.box.properties.HasBorders;
import com.github.s262316.forx.box.properties.HasColour;
import com.github.s262316.forx.box.properties.HasFlowDimensions;
import com.github.s262316.forx.box.properties.HasFontProperties;
import com.github.s262316.forx.box.properties.HasLineProperties;
import com.github.s262316.forx.box.properties.HasMargins;
import com.github.s262316.forx.box.properties.HasTextProperties;
import com.github.s262316.forx.box.properties.HasWordProperties;
import com.github.s262316.forx.box.properties.MarginDescriptor;
import com.github.s262316.forx.box.properties.PropertiesInjector;
import com.github.s262316.forx.box.properties.PropertyBoxAdaptor;
import com.github.s262316.forx.box.properties.Visual;
import com.github.s262316.forx.box.relayouter.LayoutResult;
import com.github.s262316.forx.box.relayouter.Layouters;
import com.github.s262316.forx.box.relayouter.Relayouter;
import com.github.s262316.forx.box.relayouter.util.LayoutUtils;
import com.github.s262316.forx.box.util.Border;
import com.github.s262316.forx.box.util.Boxes;
import com.github.s262316.forx.box.util.Clearance;
import com.github.s262316.forx.box.util.Direction;
import com.github.s262316.forx.box.util.FloatPosition;
import com.github.s262316.forx.box.util.Length;
import com.github.s262316.forx.box.util.Overflow;
import com.github.s262316.forx.box.util.SizeResult;
import com.github.s262316.forx.box.util.SpaceFlag;
import com.github.s262316.forx.box.util.SpecialLength;
import com.github.s262316.forx.box.util.TextAlign;
import com.github.s262316.forx.box.util.VerticalAlignment;
import com.github.s262316.forx.graphics.Triangle;
import com.github.s262316.forx.util.ZIndexComparator;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

public class BlockBox implements Box, HasAbsolutePosition, HasBackgroundProperties, HasBlockProperties, HasBorders, HasColour, 
		HasFlowDimensions, HasFontProperties, HasLineProperties, HasMargins, HasTextProperties, HasWordProperties
{
	private final static Logger logger = LoggerFactory.getLogger(BlockBox.class);

	private int id = BoxCounter.count++;
	protected LinkedList<Layable> all = new LinkedList<Layable>();
	private LinkedList<Box> flowing = new LinkedList<Box>();
	private LinkedList<FloatBox> floating = new LinkedList<FloatBox>();
	private TreeSet<AbsoluteBox> positionedBoxes = Sets.newTreeSet(new ZIndexComparator());
	private Map<AbsoluteBox, Box> staticPositions=new HashMap<>(); 
//	private boolean top_margin_collapse_in, top_margin_collapse_out;
//	private boolean bottom_margin_collapse_in, bottom_margin_collapse_out;
//	private CollapseChain collapse_chain_top, collapse_chain_bottom;
//	private CollapsedMargins collapsedMargins;
	protected int textIndent;
	protected TextAlign textAlign;
	protected Clearance clearance;
	protected Overflow overflow;
	protected Visual visual;
	private Dimensionable dimensions = new Dimensionable();
	protected int lineHeight;
	protected int letterSpacing;
	protected int wordSpacing;
	protected Color foregroundColour;
	protected boolean autoWidth, autoHeight;
	private BlockBox _container;
	private int relLeft, relTop;
	private boolean relative = false;
	private int paddingLeftSize, paddingTopSize, paddingRightSize, paddingBottomSize;
	private int marginLeftSize, marginTopSize, marginRightSize, marginBottomSize;
	private Border borders[];
	private Font font;
	private ReplaceableBoxPlugin _replace_object;
	private Optional<Integer> futureWidth = Optional.absent();
	private Optional<Integer> futureHeight = Optional.absent();

	public BlockBox(Visual visual, Drawer draw, ReplaceableBoxPlugin p)
	{
		autoWidth = false;
		autoHeight = false;
		_replace_object = p;

		this.visual = visual;

		borders = new Border[4];
		for (int i = 0; i < 4; i++)
			borders[i] = new Border();
	}

	@Override
	public int floating_boxes()
	{
		return floating.size();
	}

	@Override
	public int flowing_boxes()
	{
		return flowing.size();
	}

	@Override
	public boolean explicit_newline()
	{
		return true;
	}

	@Override
	public void flow_back(Box b)
	{
		if (BoxTypes.isInlineBox(b) == true)
		{
			// need to add this inline to a dummy inline

			Box dummy = null;

			if (flowing.size() > 0)
			{
				if (BoxTypes.isInlineBox(flowing.getLast()) == true)
					dummy = BoxTypes.toInlineBox(flowing.getLast());
			}

			if (dummy == null)
			{
				dummy = visual.createAnonInlineBox();
				flowing.add(dummy);
				all.add(dummy);

				dummy.set_container(this);
				dummy.computeProperties();
				Boxes.renumber(this.root());

				LayoutUtils.doLoadingLayout(dummy);

				b.setId(BoxCounter.count++);
			}

			dummy.flow_back(b);
		}
		else
		{
			flowing.add(b);
			all.add(b);

			b.set_container(this);
			b.computeProperties();
			LayoutUtils.doLoadingLayout(b);
		}
	}

	@Override
	public void flow_back(Inline il)
	{
		// need to add this inline to a dummy inline

		Box dummy = null;

		if (flowing.size() > 0)
		{
			if (BoxTypes.isInlineBox(flowing.getLast()) == true)
				dummy = BoxTypes.toInlineBox(flowing.getLast());
		}

		if (dummy == null)
		{
			dummy = visual.createAnonInlineBox();
			flowing.add(dummy);
			all.add(dummy);

			dummy.set_container(this);
			dummy.computeProperties();
			LayoutUtils.doLoadingLayout(dummy);
		}

		dummy.flow_back(il);
	}

	@Override
	public void flow_back(String str, SpaceFlag space)
	{
		// cout << "adding string " << str << endl;

		InlineBox dummy = null;

		// we don't want to add or lay spaces if it's at the beginning
		// of a line in a new box
		if (!(space == SpaceFlag.SF_SPACE && flowing.size() == 0))
		{
			if (flowing.size() > 0 && space == SpaceFlag.SF_SPACE)
			{
				// could still allow spaces between block boxes which
				// we want to prevent...
				if (BoxTypes.isInlineBox(flowing.getLast()) == false)
					return;
			}

			if (flowing.size() > 0)
			{
				if (BoxTypes.isInlineBox(flowing.getLast()) == true)
					dummy = BoxTypes.toInlineBox(flowing.getLast());
			}

			if (dummy == null)
			{
				dummy = visual.createAnonInlineBox();
				flowing.add(dummy);
				all.add(dummy);

				dummy.set_container(this);
				dummy.computeProperties();
				Boxes.renumber(this.root());
				LayoutUtils.doLoadingLayout(dummy);
			}

			dummy.flow_back(str, space);
		}
	}

	@Override
	public void flow_insert(Box b, int before)
	{
		int last_lit = before;
		boolean found = false;
		int flow_pos;

		while (last_lit > 0 && !found)
		{
			last_lit--;
			found = all.get(last_lit).flows();
		}

		if (found == true)
			flow_pos = flowing.indexOf(all.get(last_lit));
		else
			flow_pos = 0;

		if (BoxTypes.isInlineBox(b) == true)
		{
			// need to add this inline to a dummy inline

			Box dummy = null;

			if (flowing.size() > 0)
			{
				if (BoxTypes.isInlineBox(flowing.getLast()) == true)
					dummy = BoxTypes.toInlineBox(flowing.getLast());
			}

			if (dummy == null)
			{
				dummy = visual.createAnonInlineBox();
				flowing.add(dummy);
				all.add(dummy);

				dummy.set_container(this);
				dummy.computeProperties();
				LayoutUtils.doLoadingLayout(dummy);
			}

			dummy.flow_back(b);
		}
		else
		{
			flowing.add(flow_pos, b);
			all.add(before, b);

			b.set_container(this);
			b.computeProperties();
			LayoutUtils.doLoadingLayout(b);
		}
	}

	// inserts at the end of the ALL list
	@Override
	public void float_back(FloatBox b)
	{
		Box dummy = null;

		if (flowing.size() > 0)
		{
			if (BoxTypes.isInlineBox(flowing.getLast()) == true)
				dummy = BoxTypes.toInlineBox(flowing.getLast());
		}

		if (dummy == null)
		{
			dummy = visual.createAnonInlineBox();
			flowing.add(dummy);
			all.add(dummy);

			dummy.set_container(this);
			dummy.computeProperties();
			LayoutUtils.doLoadingLayout(dummy);
		}

		dummy.float_back(b);
	}

	// before refers to the ALL list
	@Override
	public void float_insert(FloatBox b, int before)
	{
		int last_lit = before;
		boolean found = false;
		int float_pos;

		while (last_lit > 0 && !found)
		{
			last_lit--;
			found = BoxTypes.isFloatBox(all.get(last_lit));
		}

		if (found == true)
			float_pos = last_lit;
		else
			float_pos = 0;

		floating.add(float_pos, b);
		all.add(before, b);

		b.set_container(this);
		b.computeProperties();
		LayoutUtils.doLoadingLayout(b);
	}

	@Override
	public void absBackStatic(AbsoluteBox b)
	{
		Box ancestor = Boxes.absoluteContainer(this);
		b.setStaticContainer(this);
		if(!flowing.isEmpty())
			staticPositions.put(b, flowing.getLast());
		ancestor.absBack(b);
	}

	@Override
	public void absBack(AbsoluteBox b)
	{
		positionedBoxes.add(b);
		all.add(b);

		b.set_container(this);
		b.computeProperties();

		LayoutUtils.doLoadingLayout(b);
	}

	@Override
	public void table_back(TableMember cont, TableMember memb)
	{
		TableBox anon_table;
		Box last;

		if (flowing.size() > 0)
		{
			last = flowing.getLast();
			if (BoxTypes.isTableBox(last) == true)
			{
				if (BoxTypes.isAnon(last) == true)
					anon_table = BoxTypes.toTableBox(last);
				else
				{
					anon_table = visual.createAnonTableBox();
					flow_back(anon_table);
				}
			}
			else
			{
				anon_table = visual.createAnonTableBox();
				flow_back(anon_table);
			}
		}
		else
		{
			anon_table = visual.createAnonTableBox();
			flow_back(anon_table);
		}

		anon_table.table_back(null, memb);
	}

	@Override
	public SizeResult compute_dimensions()
	{
		DimensionsDescriptor dd = new DimensionsDescriptor();
		MarginDescriptor md = new MarginDescriptor();
		SizeResult result = new SizeResult();

		visual.workOutFlowDimensions(new PropertyBoxAdaptor(this), dd);
		visual.computeMarginProperties(new PropertyBoxAdaptor(this), md);

		calculateWidth(dd.width, md.left, md.right, dd.minWidth, dd.maxWidth, dd.height);
		calculateHeight(dd.height, md.top, md.bottom, dd.width);

		result.width = dd.width;
		result.marginLeft = md.left;
		result.marginRight = md.right;
		result.height = dd.height;
		result.marginTop = md.top;
		result.marginBottom = md.bottom;

		return result;
	}

	@Override
	public void computeProperties()
	{
		PropertiesInjector.inject(visual, new PropertyBoxAdaptor(this), this, this, this, this, this, null, this, this, this, this, this, this);
	}

	@Override
	public int preferred_width()
	{
		int pmw = 0;

		for (Layable l : all)
			pmw = Math.max(pmw, l.preferred_width());

		return pmw;
	}

	@Override
	public int preferred_min_width()
	{
		int pmw = 0;

		for (Layable l : all)
			pmw = Math.max(pmw, l.preferred_min_width());

		return pmw;
	}

	@Override
	public int preferred_shrink_width(int avail_width)
	{
		int psw = 0;

		for (Layable l : all)
			psw = Math.max(psw, l.preferred_shrink_width(avail_width));

		return psw;
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
	public void set_dimensions(int width, int height)
	{
		dimensions.set_width(width);
		dimensions.set_height(height);

		if (replace_object() != null)
		{
			replace_object().use_width(width);
			replace_object().use_height(height);
		}
	}

	@Override
	public void set_width(int width)
	{
		dimensions.set_width(width);

		if (replace_object() != null)
		{
			replace_object().use_width(width);
		}
	}

	@Override
	public void set_height(int height)
	{
		dimensions.set_height(height);

		if (replace_object() != null)
		{
			replace_object().use_height(height);
		}
	}

	@Override
	public void set_position(int x, int y)
	{
		dimensions.set_position(x, y);
		if (replace_object() != null)
			replace_object().set_position(x, y);
	}

	@Override
	public void unsetPosition()
	{
		dimensions.set_position(Dimensionable.INVALID, Dimensionable.INVALID);
		if (replace_object() != null)
			replace_object().set_position(Dimensionable.INVALID, Dimensionable.INVALID);
	}

	protected boolean calculateWidth(Length width, Length marginLeft, Length marginRight, Length minWidth, Length maxWidth, Length height)
	{
		if (replace_object() != null)
		{
			if (marginLeft.specified == SpecialLength.SL_AUTO)
				marginLeft.set(0);
			if (marginRight.specified == SpecialLength.SL_AUTO)
				marginRight.set(0);

			if (width.specified == SpecialLength.SL_SPECIFIED && height.specified == SpecialLength.SL_SPECIFIED)
			{
				// do nothing
			}
			else if (width.specified == SpecialLength.SL_SPECIFIED && height.specified == SpecialLength.SL_AUTO)
			{
				if (replace_object().ratio() < 0)
				{
					if (replace_object().sheight().specified == SpecialLength.SL_SPECIFIED)
						height.set(replace_object().sheight().value);
					else if (replace_object().sheight().specified == SpecialLength.SL_AUTO)
						height.set(Math.max(150, width.value / 2));
					else
						throw new BoxError(BoxExceptionType.BET_UNKNOWN);
				}
				else
				{
					height.set((int) (width.value / replace_object().ratio()));
				}
			}
			else if (width.specified == SpecialLength.SL_AUTO && height.specified == SpecialLength.SL_SPECIFIED)
			{
				if (replace_object().ratio() < 0)
				{
					if (replace_object().swidth().specified == SpecialLength.SL_SPECIFIED)
						width.set(replace_object().swidth().value);
					else if (replace_object().swidth().specified == SpecialLength.SL_AUTO)
						width.value = Math.max(300, height.value / 2);
					else
						throw new BoxError(BoxExceptionType.BET_UNKNOWN);
				}
				else
				{
					width.set((int) (height.value * replace_object().ratio()));
				}
			}
			else if (width.specified == SpecialLength.SL_AUTO && height.specified == SpecialLength.SL_AUTO)
			{
				if (replace_object().swidth().specified == SpecialLength.SL_SPECIFIED && replace_object().sheight().specified == SpecialLength.SL_SPECIFIED)
				{
					width.set(replace_object().swidth().value);
					height.set(replace_object().sheight().value);
				}
				else if (replace_object().swidth().specified == SpecialLength.SL_SPECIFIED && replace_object().sheight().specified == SpecialLength.SL_AUTO)
				{
					width.set(replace_object().swidth().value);
					if (replace_object().ratio() < 0)
						height.set(Math.max(150, width.value / 2));
					else
						height.set((int) (width.value / replace_object().ratio()));
				}
				else if (replace_object().swidth().specified == SpecialLength.SL_AUTO && replace_object().sheight().specified == SpecialLength.SL_SPECIFIED)
				{
					height.set(replace_object().sheight().value);
					if (replace_object().ratio() < 0)
						width.set(Math.max(300, height.value / 2));
					else
						width.set((int) (height.value * replace_object().ratio()));
				}
				else if (replace_object().swidth().specified == SpecialLength.SL_AUTO && replace_object().sheight().specified == SpecialLength.SL_AUTO)
				{
					width.set(300);
					if (replace_object().ratio() < 0)
						height.set(Math.max(150, width.value / 2));
					else
						height.set((int) (width.value / replace_object().ratio()));
				}
				else
					throw new BoxError(BoxExceptionType.BET_UNKNOWN);
			}
			else
				throw new BoxError(BoxExceptionType.BET_UNKNOWN);
		}
		else
		{
			/*
			 * The following constraints must hold among the used values of the
			 * other properties:
			 * 
			 * 'margin-left' + 'border-left-width' + 'padding-left' + 'width' +
			 * 'padding-right' + 'border-right-width' + 'margin-right' = width
			 * of containing block
			 */

			// If 'width' is not 'auto' and 'border-left-width' + 'padding-left'
			// +
			// 'width' + 'padding-right' + 'border-right-width' (plus any of
			// 'margin-left'
			// or 'margin-right' that are not 'auto') is larger than the width
			// of the
			// containing block, then any 'auto' values for 'margin-left' or
			// 'margin-right'
			// are, for the following rules, treated as zero.
			if (width.specified != SpecialLength.SL_AUTO)
			{
				int specWidth;

				specWidth = borderLeft().width + paddingLeft() + width.value + paddingRight() + borderRight().width;
				if (marginLeft.specified != SpecialLength.SL_AUTO)
					specWidth += marginLeft.value;
				if (marginRight.specified != SpecialLength.SL_AUTO)
					specWidth += marginRight.value;

				if (specWidth > container().width())
				{
					// the lengths are over constrained
					if (marginLeft.specified == SpecialLength.SL_AUTO)
						marginLeft.set(0);
					if (marginRight.specified == SpecialLength.SL_AUTO)
						marginRight.set(0);
				}
			}

			if (width.specified != SpecialLength.SL_AUTO && marginLeft.specified != SpecialLength.SL_AUTO && marginRight.specified != SpecialLength.SL_AUTO)
			{
				// If all of the above have a computed value other than 'auto',
				// the values
				// are said to be "over-constrained" and one of the used values
				// will have to
				// be different from its computed value. If the 'direction'
				// property has the
				// value 'ltr', the specified value of 'margin-right' is ignored
				// and the value
				// is calculated so as to make the equality true. If the value
				// of 'direction'
				// is 'rtl', this happens to 'margin-left' instead.
				// this difference could be positive as well as negative
				// -ve = too big. +ve = too small
				int difference = container().contentWidth()
						- (marginLeft.value + marginRight.value + paddingLeft() + paddingRight() + borderLeft().width + borderRight().width + width.value);

				// the margins could become negative
				switch (direction())
				{
					case DIR_LTR:
						marginRight.value += difference;
						break;
					case DIR_RTL:
						marginLeft.value += difference;
						break;
				}
			}
			else if (width.specified == SpecialLength.SL_AUTO && marginLeft.specified == SpecialLength.SL_SPECIFIED
					&& marginRight.specified == SpecialLength.SL_SPECIFIED)
			{
				// width
				// If there is exactly one value specified as 'auto', its used
				// value follows
				// from the equality.
				width.set(container().contentWidth()
						- (marginLeft.value + paddingLeft() + borderLeft().width + borderRight().width + paddingRight() + marginRight.value));

				if (width.value < 0)
					width.set(0);

				autoWidth = true;
			}
			else if (marginLeft.specified == SpecialLength.SL_AUTO && width.specified == SpecialLength.SL_SPECIFIED
					&& marginRight.specified == SpecialLength.SL_SPECIFIED)
			{
				// leftMargin
				// If there is exactly one value specified as 'auto', its used
				// value follows
				// from the equality.
				marginLeft.set(container().contentWidth()
						- (paddingLeft() + borderLeft().width + width.value + borderRight().width + paddingRight() + marginRight.value));
			}
			else if (marginRight.specified == SpecialLength.SL_AUTO && width.specified == SpecialLength.SL_SPECIFIED
					&& marginLeft.specified == SpecialLength.SL_SPECIFIED)
			{
				// rightMargin
				// If there is exactly one value specified as 'auto', its used
				// value follows
				// from the equality.
				marginRight.set(container().contentWidth()
						- (marginLeft.value + paddingLeft() + borderLeft().width + width.value + borderRight().width + paddingRight()));
			}
			else if (width.specified == SpecialLength.SL_AUTO)
			{
				// If 'width' is set to 'auto', any other 'auto' values become
				// '0' and 'width'
				// follows from the resulting equality.
				if (marginLeft.specified == SpecialLength.SL_AUTO)
					marginLeft.set(0);
				if (marginRight.specified == SpecialLength.SL_AUTO)
					marginRight.set(0);

				width.set(container().contentWidth()
						- (marginLeft.value + paddingLeft() + borderLeft().width + borderRight().width + paddingRight() + marginRight.value));

				if (width.value < 0)
					width.set(0);

				autoWidth = true;
			}
			else if (marginLeft.specified == SpecialLength.SL_AUTO && marginRight.specified == SpecialLength.SL_AUTO)
			{
				// If both 'margin-left' and 'margin-right' are 'auto', their
				// used values are
				// equal. This horizontally centers the element with respect to
				// the edges of
				// the containing block.
				int leftrightMargins;

				leftrightMargins = container().contentWidth() - (paddingLeft() + paddingRight() + borderLeft().width + borderRight().width + width.value);

				if (leftrightMargins % 2 == 0)
				{
					marginLeft.set(leftrightMargins / 2);
					marginRight.set(leftrightMargins / 2);
				}
				else
				{
					marginLeft.set(leftrightMargins / 2);
					marginRight.set(marginLeft.value + 1);
				}
			}

			// after all that... we'll blat the size if there is a future width
			if (autoWidth && futureWidth.isPresent())
				width.set(futureWidth.get() - marginLeft.getInt() - marginRight.getInt());
		}

		return autoWidth;
	}

	protected void calculateHeight(Length height, Length marginTop, Length marginBottom, Length width)
	{
		if (replace_object() != null)
		{
			if (marginTop.specified == SpecialLength.SL_AUTO)
				marginTop.set(0);
			if (marginBottom.specified == SpecialLength.SL_AUTO)
				marginBottom.set(0);

			if (width.specified == SpecialLength.SL_SPECIFIED && height.specified == SpecialLength.SL_SPECIFIED)
			{
				// do nothing
				// size.width=width;
				// size.height=height;
			}
			else if (width.specified == SpecialLength.SL_SPECIFIED && height.specified == SpecialLength.SL_AUTO)
			{
				// size.width=width;

				if (replace_object().ratio() < 0)
				{
					if (replace_object().sheight().specified == SpecialLength.SL_SPECIFIED)
						height.set(replace_object().sheight().value);
					else if (replace_object().sheight().specified == SpecialLength.SL_AUTO)
						height.set(Math.max(150, width.value / 2));
					else
						throw new BoxError(BoxExceptionType.BET_UNKNOWN);
				}
				else
				{
					height.set((int) (width.value / replace_object().ratio()));
				}
			}
			else if (width.specified == SpecialLength.SL_AUTO && height.specified == SpecialLength.SL_SPECIFIED)
			{
				if (replace_object().ratio() < 0)
				{
					if (replace_object().swidth().specified == SpecialLength.SL_SPECIFIED)
						width.set(replace_object().swidth().value);
					else if (replace_object().swidth().specified == SpecialLength.SL_AUTO)
						width.set(Math.max(300, height.value / 2));
					else
						throw new BoxError(BoxExceptionType.BET_UNKNOWN);
				}
				else
				{
					width.set((int) (height.value * replace_object().ratio()));
				}
			}
			else if (width.specified == SpecialLength.SL_AUTO && height.specified == SpecialLength.SL_AUTO)
			{
				if (replace_object().swidth().specified == SpecialLength.SL_SPECIFIED && replace_object().sheight().specified == SpecialLength.SL_SPECIFIED)
				{
					width.set(replace_object().swidth().value);
					height.set(replace_object().sheight().value);
				}
				else if (replace_object().swidth().specified == SpecialLength.SL_SPECIFIED && replace_object().sheight().specified == SpecialLength.SL_AUTO)
				{
					width.set(replace_object().swidth().value);
					if (replace_object().ratio() < 0)
						height.set(Math.max(150, width.value / 2));
					else
						height.set((int) (width.value / replace_object().ratio()));
				}
				else if (replace_object().swidth().specified == SpecialLength.SL_AUTO && replace_object().sheight().specified == SpecialLength.SL_SPECIFIED)
				{
					height.set(replace_object().sheight().value);
					if (replace_object().ratio() < 0)
						width.set(Math.max(300, height.value / 2));
					else
						width.set((int) (height.value * replace_object().ratio()));
				}
				else if (replace_object().swidth().specified == SpecialLength.SL_AUTO && replace_object().sheight().specified == SpecialLength.SL_AUTO)
				{
					width.set(300);
					if (replace_object().ratio() < 0)
						height.set(Math.max(150, width.value / 2));
					else
						height.set((int) (width.value / replace_object().ratio()));
				}
				else
					throw new BoxError(BoxExceptionType.BET_UNKNOWN);
			}
			else
				throw new BoxError(BoxExceptionType.BET_UNKNOWN);
		}
		else
		{
			// If 'margin-top', or 'margin-bottom' are 'auto', their used value
			// is 0.
			if (marginTop.specified == SpecialLength.SL_AUTO)
				marginTop.set(0);
			if (marginBottom.specified == SpecialLength.SL_AUTO)
				marginBottom.set(0);

			if (height.specified == SpecialLength.SL_AUTO)
			{
				if (futureHeight.isPresent())
					height.set(futureHeight.get());
				else
					height.set(0);

				autoHeight = true;
			}
		}
	}

	// always a block, always in the flow, iterator points to all list
	private LayoutResult position_block(Layable layMember)
	{
		int x, y;
		SizeResult size;
		BlockBox boxMember;
		int flow_pos;
		int pos = getMembersAll().indexOf(layMember);
		boolean overflow_width = false;
		int margin_top_width, margin_bottom_width;
		boolean collapseTopWithParent=false;
		boolean collapseTopWithPrevious=false;
		boolean collapseBottomWithParent=false;

		logger.debug("---------------------- position_block {}", layMember);
		
		boxMember = BoxTypes.toBlockBox(layMember);
		flow_pos = flowing.indexOf(boxMember);

		size = boxMember.compute_dimensions();
		if (size.width.value > contentWidth())
		{
			if (stretchy_cwidth() == true)
			{
				int amount = can_stretch(size.width.value + contentWidth());
				if (amount == size.width.value + contentWidth())
				{
					return new LayoutResult(false, Optional.of(Layouters.moreWidth(this, layMember, amount)));
				}
			}
			else
				overflow_width = true;
		}

		margin_top_width = size.marginTop.value;
		margin_bottom_width = size.marginBottom.value;

		int yAfterWithClearance = calculate_clearance(pos);

		if(Margins.doesTopMarginCollapseWithParent(this))
		{
			margin_top_width=Margins.marginHowBig(size.marginTop.value, _container.marginTop());

			if (margin_top_width > _container.marginTop())
				return new LayoutResult(false, Optional.of(Layouters.changeTopMarginSize(_container, this, margin_top_width)));
			
			collapseTopWithParent=true;
		}
		else if(Margins.doesTopMarginCollapseWithPrevious(this))
		{
			int prevIt = flow_pos;
			Box prev = flowing.get(prevIt - 1);			
			
			margin_top_width=Margins.marginHowBig(size.marginTop.value, prev.marginBottom());

			if (margin_top_width > prev.marginTop())
				return new LayoutResult(false, Optional.of(Layouters.changeBottomMarginSize(prev, this, margin_top_width)));
			
			collapseTopWithPrevious=true;
		}
//		
//		if(Margins.doesBottomMarginCollapseWithParent(this))
//		{
//			margin_bottom_width=Margins.marginHowBig(size.marginBottom.value, _container.marginBottom());
//
//			if (margin_bottom_width > _container.marginBottom())
//				return new LayoutResult(false, Optional.of(Layouters.changeBottomMarginSize(_container, this, margin_bottom_width)));
//			
//			collapseBottomWithParent=true;
//		}

		boxMember.set_margins(size.marginLeft.value, margin_top_width, size.marginRight.value, margin_bottom_width);

		// size.width/size.height are the content width/height
		boxMember.set_dimensions(size.width.value + size.marginLeft.value + size.marginRight.value + boxMember.paddingLeft() + boxMember.paddingRight()
				+ boxMember.borderLeft().width + boxMember.borderRight().width,
				size.height.value + margin_top_width + margin_bottom_width + boxMember.paddingTop() + boxMember.paddingBottom() + boxMember.borderTop().width
						+ boxMember.borderBottom().width);

		if (flowing_boxes() == 1 || flow_pos == 0)
		{
			// empty container or inserting at beginning
			x = contentLeft();

			if (collapseTopWithParent == true)
			{
				// position boxMember at top()
				y = top();
			}
			else
			{
				// no collapsing
				y = contentTop();
			}
		}
		else
		{
			// not inserting at beginning
			int prevIt = flow_pos;
			Box prev = flowing.get(prevIt - 1);

			x = contentLeft();

			if (yAfterWithClearance == 0)
			{
				if (BoxTypes.isBlockBox(prev) == true)
				{
					// should newMember's margin and the previous member's
					// margin collapse?
					if (collapseTopWithPrevious == true)
					{
						y = prev.contentBottom() + prev.paddingBottom() + prev.borderBottom().width + margin_top_width;
					}
					else
					{
						// no collapsing
						y = prev.bottom() + 1;
					}
				}
				else
				{
					// no collapsing
					y = prev.bottom() + 1;
				}
			}
			else
			{
				y=yAfterWithClearance;
			}
		}

//		// does height need adjusting
//		if (collapseBottomWithParent==true)
//		{
//			// adjust height and take into account collapsed bottom margin
//			if (isAutoHeight() == true && boxMember.height() > 0 && y + boxMember.height() - 1 != bottom())
//			{
//				// adjust "this" height
//				int amountToResizeThis = y + boxMember.height() - 1 - bottom();
//				if (height() == 0)
//					amountToResizeThis++;
//
//				return new LayoutResult(false, Optional.of(Layouters.moreHeight(this, boxMember, amountToResizeThis)));
//			}
//			else
//			{
//				// TODO can't do a resize (why not?)
//			}
//		}
//		else if (isAutoHeight() == true && boxMember.height() > 0 && y + boxMember.height() - 1 > contentBottom())
//		{
//			int resizeBy; // TODO by or to?
//
//			// adjust "this" height
//			resizeBy = y + boxMember.height() - 1 - contentBottom();
//			if (height() == 0)
//				resizeBy++;
//
//			return new LayoutResult(false, Optional.of(Layouters.moreHeight(this, boxMember, resizeBy)));
//		}
//		else
//		{
//			// all ok
//		}

		boxMember.set_position(x, y);

		return new LayoutResult(true, Optional.<Relayouter> absent());
	}

	private LayoutResult position_inlinebox(Layable layMember)
	{
		int x, y;
		SizeResult size;
		InlineBox boxMember;
		int flow_pos;

		boxMember = BoxTypes.toInlineBox(layMember);
		flow_pos = flowing.indexOf(boxMember);

		size = boxMember.compute_dimensions();
		// only anon inlineboxes should be inserted here
		// no need to worry about overflow

		boxMember.set_dimensions(size.width.value, size.height.value);
		boxMember.set_margins(size.marginLeft.value, size.marginTop.value, size.marginRight.value, size.marginBottom.value);

		if (size.width.value > contentWidth())
		{
			int parentCanIncreaseWidthBy = stretchAmountWidth(layMember, size.width.getInt() - contentWidth());
			if (parentCanIncreaseWidthBy > 0)
			{
				// take any more width we can get
				return new LayoutResult(false, Optional.of(Layouters.moreWidth(this, layMember, parentCanIncreaseWidthBy)));
			}
		}

		boxMember.set_margins(size.marginLeft.value, size.marginTop.value, size.marginRight.value, size.marginBottom.value);

		// size.width/size.height are the content width/height
		boxMember.set_dimensions(size.width.value + size.marginLeft.value + size.marginRight.value + boxMember.paddingLeft() + boxMember.paddingRight()
				+ boxMember.borderLeft().width + boxMember.borderRight().width, size.height.value);

		if (flowing_boxes() == 1 || flow_pos == 0)
		{
			// empty container or inserting at beginning
			x = contentLeft();
			y = contentTop();
		}
		else
		{
			// not inserting at beginning
			int prevIt = flow_pos;
			Box prev = flowing.get(prevIt - 1);

			x = contentLeft();
			y = prev.bottom() + 1;
		}

		if(Boxes.existsInSpace(boxMember) && isAutoHeight() && y + boxMember.height() - 1 > contentBottom())
		{
			int diff=y + boxMember.height() - 1 - contentBottom();
			int canStretchBy=container().canStretchHeight(diff);
			
			if(canStretchBy>0)
				return new LayoutResult(false, Optional.of(Layouters.moreHeight(this, boxMember, canStretchBy)));
		}

//		if (container().auto_height() == true && boxMember.height() > 0 && y + boxMember.height() - 1 != contentBottom())
//		{
//			int resizeBy; // TODO by or to
//			// adjust "this" height
//			resizeBy = y + boxMember.height() - 1 - contentBottom();
//			if (height() == 0)
//				resizeBy++;
//
//			return new LayoutResult(false, Optional.of(Layouters.moreHeight(this, boxMember, resizeBy)));
//		}

		boxMember.set_position(x, y);

		return new LayoutResult(true, Optional.<Relayouter> absent());
	}

	@Override
	public void calculateStaticPosition(AbsoluteBox newMember)
	{
		int sx, sy;
		
		if(staticPositions.containsKey(newMember))
		{
			Box last_flow = staticPositions.get(newMember);
			sx = contentLeft();
			sy = last_flow.bottom() + 1;
		}
		else
		{
			// empty container or inserting at beginning
			sx = contentLeft();
			sy = contentTop();
		}

		newMember.set_static_position(sx, sy);
	}

	LayoutResult position_absolute(Layable layMember)
	{
		AbsoluteBox newMember;
		AbsoluteBox.PositionAndSize posAndSize;
		
		newMember = BoxTypes.toAbsoluteBox(layMember);		
		newMember.getStaticContainer().calculateStaticPosition(newMember);
		posAndSize=newMember.calculatePositionAndDimensions();
		
		if (posAndSize.dimensions.width.specified == SpecialLength.SL_AUTO)
			newMember._stretchy_cwidth = true;
		else
			newMember._stretchy_cwidth = false;

		newMember.set_relative_position(posAndSize.position.left, posAndSize.position.top, posAndSize.position.right, posAndSize.position.bottom);

		newMember.calculateWidth(posAndSize.dimensions.width, posAndSize.margins.left,posAndSize. margins.right, posAndSize.position.left, posAndSize.position.right, posAndSize.dimensions.height);
		newMember.calculateHeight(posAndSize.dimensions.height, posAndSize.margins.top, posAndSize.margins.bottom, posAndSize.position.top, posAndSize.position.bottom, posAndSize.dimensions.width);

		newMember.set_margins(posAndSize.margins.left.value, posAndSize.margins.top.value, posAndSize.margins.right.value, posAndSize.margins.bottom.value);
		
		newMember.set_dimensions(posAndSize.dimensions.width.value + posAndSize.margins.left.value + posAndSize.margins.right.value + newMember.borderLeft().width + newMember.borderRight().width + newMember.paddingLeft()
				+ newMember.paddingRight(), posAndSize.dimensions.height.value + posAndSize.margins.top.value + posAndSize.margins.bottom.value + newMember.borderTop().width + newMember.borderBottom().width + newMember.paddingTop()
				+ newMember.paddingBottom());

		newMember.set_position(contentLeft() + posAndSize.position.left.value, contentTop() + posAndSize.position.top.value);

		return new LayoutResult(true, Optional.<Relayouter> absent());
	}

	private void unposition_block(Layable member)
	{
		LayoutUtils.invalidatePosition(member);
	}

	private void unposition_absolute(Layable member)
	{
		LayoutUtils.invalidatePosition(member);
	}
	
	private void unposition_inlinebox(Layable member)
	{
		InlineBox ilb = BoxTypes.toInlineBox(member);
		List<Line> lines = ilb.flowspace().line_list(ilb);

		if (lines != null)
		{
			for (Line l : lines)
				ilb.flowspace().disassoc_inlinebox(l, ilb);
		}

		LayoutUtils.invalidatePosition(member);
	}

	// this function is obselete
	// pos is an iterator in the ALL list
	private LayoutResult position_float(Layable layMember)
	{
		SizeResult size;
		FloatBox newMember;
		int pos = getMembersAll().indexOf(layMember);

		newMember = BoxTypes.toFloatBox(layMember);
		size = newMember.compute_dimensions();

		if (size.width.value > contentWidth())
		{
			// TODO this is wrong
			// not enough width... all bets are off
			return new LayoutResult(false, Optional.of(Layouters.moreWidthDontUnderstandWhy(this, layMember, size.width.value - contentWidth())));
		}
		else
		{
			int float_pos = floating.indexOf(newMember);
			Rectangle r1;

			newMember.set_margins(size.marginLeft.value, size.marginTop.value, size.marginRight.value, size.marginBottom.value);
			newMember.set_dimensions(size.width.value, size.height.value);

			// if inserting at beginning or there are no flows
			if (flowing_boxes() == 0 || pos == 0)
			{
				r1 = get_flow_context().metricsNoClear(contentLeft(),// atLeastX
						contentRight(),// atMostX
						contentTop(),// atLeastY
						newMember.width(),// atLeastWidth
						newMember.height());// atLeastHeight
			}
			else
			{
				// not inserting at beginning and there are flows before us
				// find the previous flow
				Layable previous_flow;
				int prevIt = pos;
				Layable prev = flowing.get(prevIt - 1);
				// this loop looks a bit dodgy, but it should never go past the
				// beginning
				// because we already know that flowing_boxes() is not zero!
				// there are
				// flowing boxes in there.......
				while (flowing.get(prevIt).flows() == false)
					prevIt = prevIt - 1;

				previous_flow = flowing.get(prevIt);

				r1 = get_flow_context().metricsNoClear(contentLeft(),// atLeastX
						contentRight(),// atMostX
						previous_flow.bottom() + 1,// atLeastY
						newMember.width(),// atLeastWidth
						newMember.height());// atLeastHeight
			}

			if (newMember.getFloatPosition() == FloatPosition.LEFT)
			{
				r1.width -= (r1.width - newMember.width());
			}
			else if (newMember.getFloatPosition() == FloatPosition.RIGHT)
			{
				r1.x += (r1.width - newMember.width());
				r1.width -= (r1.width - newMember.width());
			}
			else
				throw new BoxError(BoxExceptionType.BET_UNKNOWN);

			newMember.set_position(r1.x, r1.y);

			if (newMember.getFloatPosition() == FloatPosition.LEFT)
				get_flow_context().takeout_flow_area(newMember.top(), newMember.bottom(), newMember.right(), newMember.getFloatPosition());
			else if (newMember.getFloatPosition() == FloatPosition.RIGHT)
				get_flow_context().takeout_flow_area(newMember.top(), newMember.bottom(), newMember.left(), newMember.getFloatPosition());
			else
				throw new BoxError(BoxExceptionType.BET_UNKNOWN);

			newMember.setFloated();
		}

		return new LayoutResult(true, Optional.<Relayouter> absent());
	}

	private void unposition_absolute(Layable member, ListIterator<Layable> pos)
	{
	}



	// pos is in the all list
	private int calculate_clearance(int pos)
	{
		int where = 0;

		if (flowing_boxes() == 1 || pos == 0)
			where = 0;
		else
		{
			Clearance clear = BoxTypes.toBlockBox(all.get(pos)).getClearance();

			// not inserting at beginning
			Layable prev = all.get(pos - 1);

			if (clear == Clearance.C_LEFT)
				where = get_flow_context().highest_flow_left_clear(prev.bottom() + 1);
			else if (clear == Clearance.C_RIGHT)
				where = get_flow_context().highest_flow_right_clear(prev.bottom() + 1);
			else if (clear == Clearance.C_BOTH)
				where = get_flow_context().highest_flow_both_clear(prev.bottom() + 1);
		}

		return where;
	}

	// pos is now an iterator in the ALL list
	@Override
	public LayoutResult calculate_position(Layable newMember)
	{
		if (newMember.flows() == true)
		{
			// the new box has been added to the flow
			if (BoxTypes.isBlockBox(newMember) == true)
			{
				return position_block(newMember);
			}
			else if (BoxTypes.isInlineBox(newMember) == true)
			{
				return position_inlinebox(newMember);
			}
		}
		else if (BoxTypes.isFloatBox(newMember))
		{
			// the new box has been added as a floating box
			return position_float(newMember);
		}
		else if (BoxTypes.isAbsoluteBox(newMember))
		{
			return position_absolute(newMember);
		}

		return new LayoutResult(true, Optional.<Relayouter> absent());
	}

	@Override
	public void uncalculate_position(Layable member)
	{
		if (member.flows() == true)
		{
			// the new box has been added to the flow
			if (BoxTypes.isBlockBox(member))
			{
				unposition_block(member);
			}
			else if (BoxTypes.isInlineBox(member))
			{
				unposition_inlinebox(member);
			}
		}
		else if (BoxTypes.isAbsoluteBox(member))
		{
			unposition_absolute(member);
		}
	}

	// @Override
	// public Layable resize_height(int from, int amount)
	// {
	// Layable requester=all.get(from);
	// Layable earliest_affected=requester;
	//
	// // force the change in height
	// requester.change_height(amount);
	//
	// if(bottom_margin_collapse_in == true && BoxTypes.isBlockBox(requester) ==
	// true)
	// {
	// if(BoxTypes.toBlockBox(requester).bottom_margin_collapse_out == true)
	// {
	// if(requester.bottom() > bottom() && stiffy_height() == false)
	// earliest_affected=container().resize_height(container().getMembersAll().indexOf(this),
	// requester.bottom() - bottom());
	// }
	// else
	// {
	// if(requester.bottom() > contentBottom() && stiffy_height() == false)
	// earliest_affected=container().resize_height(container().getMembersAll().indexOf(this),
	// requester.bottom() - contentBottom());
	// }
	// }
	// else
	// {
	// if(requester.bottom() > contentBottom() && stiffy_height() == false)
	// earliest_affected=container().resize_height(container().getMembersAll().indexOf(this),
	// requester.bottom() - contentBottom());
	// }
	//
	// return earliest_affected;
	// }

	@Override
	public int stretchAmountHeight(Layable whichChild, int y, int maxExtraAmount)
	{
		if (y+whichChild.height() <= contentBottom())
			return 0;

		if (stretchy_cheight())
		{
			int amount = can_stretch_vert(maxExtraAmount);
			if (amount > 0)
				return amount;
		}

		return 0;
	}

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
	// earliest_affected=container().resize_width(container().getMembersAll().indexOf(this),
	// requester.width() - contentWidth());
	// else
	// earliest_affected=this;
	//
	// return earliest_affected;
	// }

	@Override
	public int stretchAmountWidth(Layable layMember, int maxExtraAmount)
	{
		if (layMember.width() <= contentWidth())
			return 0;

		if (stretchy_cwidth())
		{
			int amount = can_stretch(maxExtraAmount);
			if (amount > 0)
				return amount;
		}

		return 0;
	}

	@Override
	public void setFutureWidth(int futureWidth)
	{
		this.futureWidth = Optional.of(futureWidth);
	}

	@Override
	public void setFutureHeight(int futureHeight)
	{
		this.futureHeight = Optional.of(futureHeight);
	}

	public Optional<Integer> getFutureWidth()
	{
		return futureWidth;
	}

	public Optional<Integer> getFutureHeight()
	{
		return futureHeight;
	}

	@Override
	public void set_container(Box cont)
	{
		_container = (BlockBox)cont;
	}

	@Override
	public Box container()
	{
		return _container;
	}

	@Override
	public BlockBox getContainer()
	{
		return _container;
	}
	
	@Override
	public TableBox table_root()
	{
		Layable layable = this;
		boolean found = false;

		while (found == false && layable != null)
		{
			if (BoxTypes.isBox(layable))
			{
				found = BoxTypes.isTableBox(layable);
				if (found)
					return BoxTypes.toTableBox(layable);
			}

			layable = layable.container();
		}

		return null;
	}

	@Override
	public boolean is_relative()
	{
		return relative;
	}

	@Override
	public void set_relative(boolean to)
	{
		relative = to;
	}

	// Box
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

	// these are called by calculate_position
	@Override
	public void set_margins(int left, int top, int right, int bottom)
	{
		marginLeftSize = left;
		marginTopSize = top;
		marginRightSize = right;
		marginBottomSize = bottom;
	}

	// i think there should be one of these for every box type?
	// commented out the calculatewidth/calculateheight for now
	@Override
	public void applyMinMaxConstraints(Length width, Length height, Length marginLeft, Length marginRight, Length minWidth, Length maxWidth, Length minHeight,
			Length maxHeight)
	{
		// these are for less typing
		Length miw = minWidth;
		Length maw = maxWidth;
		Length mih = minHeight;
		Length mah = maxHeight;

		/*
		 * if(height==SL_AUTO && width==SL_AUTO && replace_object) {
		 * 
		 * if(replace_object->width() > miw && replace_object->width() < maw &&
		 * replace_object->height() > mih && replace_object->height() < mah) {}
		 * else { if(replace_object->width() > maw && replace_object->height() >
		 * mah && maw/replace_object->width() <= mah/replace_object->height()) {
		 * width=maxWidth; height=max(minHeight.value, maxWidth.value *
		 * (height/width)); } else if(replace_object->width() > maw &&
		 * replace_object->height() > mah && maw/replace_object->width() >
		 * mah/replace_object->height()) { width=max(minWidth.value,
		 * maxHeight.value * (width/height)); height=maxHeight; } else
		 * if(replace_object->width() < miw && replace_object->height() < mih &&
		 * miw/replace_object->width() <= mih/replace_object->height()) {
		 * width=min(maxWidth.value, minHeight.value * (width/height));
		 * height=minHeight; } else if(replace_object->width() < miw &&
		 * replace_object->height() < mih && miw/replace_object->width() >
		 * mih/replace_object->height()) { width=minWidth;
		 * height=min(maxHeight.value, minWidth.value * (height/width)); } else
		 * if(replace_object->width() < miw && replace_object->height() > mah) {
		 * width=minWidth; height=maxHeight; } else if(replace_object->width() >
		 * maw && replace_object->height() < mih) { width=maxWidth;
		 * height=minHeight; } else if(replace_object->width() > maw) {
		 * width=maxWidth.value; height=max(maxWidth.value * (height/width),
		 * minHeight.value); } else if(replace_object->width() < miw) {
		 * width=minWidth.value; height=min(minWidth.value * (height/width),
		 * maxHeight.value); } else if(replace_object->height() > mah) {
		 * width=max(maxHeight.value * (width/height), minWidth.value);
		 * height=maxHeight; } else if(replace_object->height() < mih) {
		 * width=min(minHeight.value * (width/height), maxWidth.value);
		 * height=minHeight; }
		 * 
		 * // calculateWidth(width, marginLeft, marginRight); } }
		 */
		// else
		// {
		if (maw.specified != SpecialLength.SL_NULL)
		{
			if (width.value > maxWidth.value)
			{
				// 2. If the tentative used width is greater than 'max-width',
				// the rules above
				// are applied again, but this time using the computed value of
				// 'max-width' as
				// the computed value for 'width'.
				width.set(maw);
				// calculateWidth(width, marginLeft, marginRight);
			}
		}

		if (width.value < minWidth.value)
		{
			// 3. If the resulting width is smaller than 'min-width', the rules
			// above are applied
			// again, but this time using the value of 'min-width' as the
			// computed value for 'width'.
			width.set(minWidth);
			// calculateWidth(width, marginLeft, marginRight);
		}

		if (mah.specified != SpecialLength.SL_NULL)
		{
			if (height.value > maxHeight.value)
			{
				height.set(maxHeight);
			}
		}

		if (height.value < minHeight.value)
		{
			height.set(minHeight);
			// calculateHeight(height, marginTop, marginBottom);
		}
		// }
	}

	@Override
	public void table_insert(TableMember cont, TableMember memb)
	{
		throw new BoxError(BoxExceptionType.BET_WRONGTYPE);
	}

	public boolean marginsCollapseThrough()
	{
		return false;
	}

	@Override
	public Direction direction()
	{
		return Direction.DIR_LTR;
	}// return _direction;}

	@Override
	public Box root()
	{
		Box up = this;

		while (up.container() != null)
			up = up.container();

		return up;
	}

	@Override
	public FlowContext get_flow_context()
	{
		return container().get_flow_context();
	}

	@Override
	public Box get_flow_context_box()
	{
		return container().get_flow_context_box();
	}

	@Override
	public boolean stretchy_cwidth()
	{
		if (container() != null)
			return container().stretchy_cwidth();

		return false;
	}

	@Override
	public boolean stretchy_cheight()
	{
		if (container() != null)
			return container().stretchy_cheight();

		return false;
	}	
	
	@Override
	public int can_stretch(int amount)
	{
		if (stretchy_cwidth() == false)
			throw new BoxError(BoxExceptionType.BET_WRONGTYPE);

		return container().can_stretch(amount);
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
	public int can_stretch_vert(int amount)
	{
		if (stretchy_cheight() == false)
			throw new BoxError(BoxExceptionType.BET_WRONGTYPE);

		return container().can_stretch_vert(amount);
	}	
	
	@Override
	public boolean stiffy_height()
	{
		return !autoHeight;
	}

	@Override
	public int max_stiffy_height()
	{
		if (autoHeight == true)
			throw new BoxError(BoxExceptionType.BET_WRONGTYPE);

		return contentHeight();
	}

	@Override
	public boolean isAutoWidth()
	{
		return autoWidth;
	}

	@Override
	public boolean isAutoHeight()
	{
		return autoHeight;
	}

	public void setAutoWidth(boolean autoWidth)
	{
		this.autoWidth = autoWidth;
	}

	public void setAutoHeight(boolean autoHeight)
	{
		this.autoHeight = autoHeight;
	}

	@Override
	public ReplaceableBoxPlugin replace_object()
	{
		return _replace_object;
	}

	@Override
	public int left()
	{
		return dimensions.left();
	}

	@Override
	public int top()
	{
		return dimensions.top();
	}

	@Override
	public int right()
	{
		return dimensions.right();
	}

	@Override
	public int bottom()
	{
		return dimensions.bottom();
	}

	@Override
	public int width()
	{
		return dimensions.width();
	}

	@Override
	public int height()
	{
		return dimensions.height();
	}

	@Override
	public void change_height(int difference)
	{
		dimensions.change_height(difference);
	}

	@Override
	public void change_width(int difference)
	{
		dimensions.change_width(difference);
	}

	@Override
	public List<Layable> getMembersAll()
	{
		return Collections.unmodifiableList(all);
	}

	@Override
	public List<Box> getMembersFlowing()
	{
		return Collections.unmodifiableList(flowing);
	}

	@Override
	public List<FloatBox> getMembersFloating()
	{
		return Collections.unmodifiableList(floating);
	}

	@Override
	public List<AbsoluteBox> getMembersPositioned()
	{
		return ImmutableList.copyOf(positionedBoxes);
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

	/*
	// set_borders(bd);
	//
	// _text_indent=tp.text_indent;
	// _text_align=tp.text_align;
	//
	// _line_height=(int) ld.lineHeight;
	// // no vertical-align for block boxes
	// _foreground_colour=cd.foreground;
	//
	// _clearance=bp.clear;
	// _overflow=bp.overflow;
	 */
	
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
	public void setLineHeight(int lineHeight)
	{
		this.lineHeight=lineHeight;
	}

	@Override
	public void setVerticalAlign(VerticalAlignment verticalAlign)
	{
		// no vertical-align for block boxes
	}

	@Override
	public void setFont(Font f)
	{
		this.font=f;
	}
	
	@Override
	public Font getFont()
	{
		return font;
	}	

	@Override
	public void setForegroundColour(Color foregroundColour)
	{
		this.foregroundColour=foregroundColour;
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

	@Override
	public void setClearance(Clearance clearance)
	{
		this.clearance=clearance;
	}

	public Clearance getClearance()
	{
		return clearance;
	}
	
	@Override
	public void setOverflow(Overflow overflow)
	{
		this.overflow=overflow;
	}

	public Overflow getOverflow()
	{
		return overflow;
	}
	
	@Override
	public void setRelLeft(int relLeft)
	{
		this.relLeft=relLeft;
	}

	@Override
	public void setRelTop(int relTop)
	{
		this.relTop=relTop;
	}

	@Override
	public int getRelLeft()
	{
		return relLeft;
	}

	@Override
	public int getRelTop()
	{
		return relTop;
	}

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this).add("id", id).toString();
	}	
}
