package com.github.s262316.forx.box;

import java.awt.Font;
import java.util.List;

import com.github.s262316.forx.box.properties.DimensionsDescriptor;
import com.github.s262316.forx.box.properties.HasAbsolutePosition;
import com.github.s262316.forx.box.properties.HasFontProperties;
import com.github.s262316.forx.box.properties.HasLineProperties;
import com.github.s262316.forx.box.properties.MarginDescriptor;
import com.github.s262316.forx.box.properties.PropertiesInjector;
import com.github.s262316.forx.box.properties.PropertyAtomicInlineAdaptor;
import com.github.s262316.forx.box.properties.Visual;
import com.github.s262316.forx.box.relayouter.LayoutResult;
import com.github.s262316.forx.box.util.SizeResult;
import com.github.s262316.forx.box.util.SpecialLength;
import com.github.s262316.forx.box.util.VerticalAlignment;
import com.github.s262316.forx.box.util.VerticalAlignmentSpecial;
import com.github.s262316.forx.graphics.FontUtils;

public class ReplacedInline extends AtomicInline implements HasLineProperties, HasAbsolutePosition, HasFontProperties
{
	private ReplaceableBoxPlugin plugin;
	private Visual visual;
	private VerticalAlignment verticalAlign;
	private int _bl_shift;
	private int id;
	private Font font;

	public ReplacedInline(Visual v, ReplaceableBoxPlugin p)
	{
		visual=v;
		plugin=p;
		_bl_shift=0;
	}

    @Override
    public void computeProperties()
    {
    	PropertiesInjector.inject(visual, new PropertyAtomicInlineAdaptor(this), this, null, null, null, null, null, null, this, this, null, null, null);
    }	
	
//    @Override
//	public void compute_properties()
//	{
//		LineDescriptor linesDescriptor=new LineDescriptor();
//		PositionDescriptor pd=new PositionDescriptor();
//		Font f;
//
//		visual.workOutLineProperties(this, linesDescriptor);
//		f=visual.workOutFontProperties(this);
//
//		if(is_relative()==true)
//		{
//			visual.workOutAbsolutePosition(this, pd);
//			if(pd.left.specified==SpecialLength.SL_AUTO && pd.right.specified==SpecialLength.SL_SPECIFIED)
//				set_rel_left(-pd.right.value);
//			if(pd.right.specified==SpecialLength.SL_AUTO && pd.left.specified==SpecialLength.SL_SPECIFIED)
//				set_rel_left(pd.left.value);
//
//			if(pd.top.specified==SpecialLength.SL_AUTO && pd.bottom.specified==SpecialLength.SL_SPECIFIED)
//				set_rel_top(-pd.bottom.value);
//			if(pd.bottom.specified==SpecialLength.SL_AUTO && pd.top.specified==SpecialLength.SL_SPECIFIED)
//				set_rel_top(pd.top.value);
//		}
//
//		_vertical_align=linesDescriptor.verticalAlign;
//		if(_vertical_align.specified==VerticalAlignmentSpecial.VAS_SUPER)
//			_bl_shift=-FontUtils.x_height(f, canvas());
//		else if(_vertical_align.specified==VerticalAlignmentSpecial.VAS_SUB)
//			_bl_shift=FontUtils.x_height(f, canvas());
//		else if(_vertical_align.specified==VerticalAlignmentSpecial.VAS_LENGTH)
//			_bl_shift=-_vertical_align.value;
//	}

    @Override
	public SizeResult compute_dimensions()
	{
		DimensionsDescriptor dd=new DimensionsDescriptor();
		MarginDescriptor md=new MarginDescriptor();
		SizeResult size=new SizeResult();

		visual.workOutFlowDimensions(new PropertyAtomicInlineAdaptor(this), dd);
		visual.computeMarginProperties(new PropertyAtomicInlineAdaptor(this), md);

		if(md.left.specified==SpecialLength.SL_AUTO)
			size.marginLeft.set(0);
		if(md.top.specified==SpecialLength.SL_AUTO)
			size.marginTop.set(0);
		if(md.right.specified==SpecialLength.SL_AUTO)
			size.marginRight.set(0);
		if(md.bottom.specified==SpecialLength.SL_AUTO)
			size.marginBottom.set(0);

		if(dd.width.specified==SpecialLength.SL_SPECIFIED && dd.height.specified==SpecialLength.SL_SPECIFIED)
		{
			size.width.set(dd.width);
			size.height.set(dd.height);
		}
		else if(dd.width.specified==SpecialLength.SL_SPECIFIED && dd.height.specified==SpecialLength.SL_AUTO)
		{
			size.width.set(dd.width);

			if(plugin.ratio()<0)
			{
				if(plugin.sheight().specified==SpecialLength.SL_SPECIFIED)
					size.height.set(plugin.sheight().value);
				else if(plugin.sheight().specified==SpecialLength.SL_AUTO)
					size.height.set(Math.max(150, size.width.value/2));
				else
					throw new BoxError(BoxExceptionType.BET_UNKNOWN);
			}
			else
			{
				size.height.set((int)(size.width.value / plugin.ratio()));
			}
		}
		else if(dd.width.specified==SpecialLength.SL_AUTO && dd.height.specified==SpecialLength.SL_SPECIFIED)
		{
			size.height.set(dd.height);

			if(plugin.ratio()<0)
			{
				if(plugin.swidth().specified==SpecialLength.SL_SPECIFIED)
					size.width.set(plugin.swidth().value);
				else if(plugin.swidth().specified==SpecialLength.SL_AUTO)
					size.width.set(Math.max(300, size.height.value/2));
				else
					throw new BoxError(BoxExceptionType.BET_UNKNOWN);
			}
			else
			{
				size.width.set((int)(size.height.value * plugin.ratio()));
			}
		}
		else if(dd.width.specified==SpecialLength.SL_AUTO && dd.height.specified==SpecialLength.SL_AUTO)
		{
			if(plugin.swidth().specified==SpecialLength.SL_SPECIFIED && plugin.sheight().specified==SpecialLength.SL_SPECIFIED)
			{
				size.width.set(plugin.swidth().value);
				size.height.set(plugin.sheight().value);
			}
			else if(plugin.swidth().specified==SpecialLength.SL_SPECIFIED && plugin.sheight().specified==SpecialLength.SL_AUTO)
			{
				size.width.set(plugin.swidth().value);
				if(plugin.ratio()<0)
					size.height.set(Math.max(150, size.width.value/2));
				else
					size.height.set((int)(size.width.value / plugin.ratio()));
			}
			else if(plugin.swidth().specified==SpecialLength.SL_AUTO && plugin.sheight().specified==SpecialLength.SL_SPECIFIED)
			{
				size.height.set(plugin.sheight().value);
				if(plugin.ratio()<0)
					size.width.set(Math.max(300, size.height.value/2));
				else
					size.width.set((int)(size.height.value * plugin.ratio()));
			}
			else if(plugin.swidth().specified==SpecialLength.SL_AUTO && plugin.sheight().specified==SpecialLength.SL_AUTO)
			{
				size.width.set(300);
				if(plugin.ratio()<0)
					size.height.set(Math.max(150, size.width.value/2));
				else
					size.height.set((int)(size.width.value / plugin.ratio()));
			}
			else
				throw new BoxError(BoxExceptionType.BET_UNKNOWN);
		}
		else
			throw new BoxError(BoxExceptionType.BET_UNKNOWN);

		return size;
	}

	@Override
	public void set_position(int left, int top)
	{
		super.set_position(left, top);
		plugin.set_position(left, top);
	}

    @Override
	public void set_dimensions(int width, int height)
	{
		super.set_dimensions(width, height);

		plugin.use_width(width);
		plugin.use_height(height);
	}

    @Override
	public void set_width(int width)
	{
		super.set_width(width);

		plugin.use_width(width);
	}

    @Override
	public void set_height(int height)
	{
		super.set_height(height);

		plugin.use_height(height);
	}

    @Override
	public boolean explicit_newline()
	{
		return false;
	}

    @Override
	public int atomic_width()
	{
		return width();
	}

    @Override
	public VerticalAlignment getVerticalAlign()
	{
		return verticalAlign;
	}

    @Override
	public Font getFont()
	{
		return container().getFont();
	}

    @Override
	@Deprecated
	public int content_height()
	{
		return height();
	}

    @Override
	@Deprecated
	public int content_top()
	{
		return top();
	}

    @Override
	@Deprecated
	public int content_bottom()
	{
		return bottom();
	}
    
    @Override
	@Deprecated
	public int content_width()
    {
		return width();
	}

	@Override
	public LayoutResult calculate_position(Layable lay)
	{
		throw new BoxError(BoxExceptionType.BET_UNKNOWN);
	}

    @Override
	public void uncalculate_position(Layable lay)
	{
		throw new BoxError(BoxExceptionType.BET_UNKNOWN);
	}

    @Override
	public int bl_shift()
	{
		return _bl_shift;
	}

    @Override
	public boolean flows()
	{
		return true;
	}

    @Override
    public List<Layable> getMembersAll()
    {
		return null;
	}

    @Override
    public List<Box> getMembersFlowing()
    {
		return null;
	}

    @Override
    public List<FloatBox> getMembersFloating()
    {
		return null;
	}

    @Override
    public List<AbsoluteBox> getMembersPositioned()
    {
		return null;
	}

    @Override
	public int getId()
	{
		return id;
	}
    
    @Override
    public void setId(int id)
    {
    	this.id=id;
    }

	@Override
	public int stretchAmountWidth(Layable whichChild, int maxExtraAmount)
	{
		return 0;
	}

	@Override
	public int stretchAmountHeight(Layable whichChild, int y, int maxExtraAmount)
	{
		return 0;
	}
	
	@Override
	public void setFutureWidth(int futureWidth)
	{
	}

	@Override
	public void setFutureHeight(int futureHeight)
	{
	}

	@Override
	public void setRelLeft(int relLeft)
	{
		super.setRelLeft(relLeft);
	}

	@Override
	public void setRelTop(int relTop)
	{
		super.setRelTop(relTop);
	}

	@Override
	public void setLineHeight(int lineHeight)
	{
		// do nothing?
	}

	@Override
	public void setVerticalAlign(VerticalAlignment verticalAlign)
	{
		if(verticalAlign.specified==VerticalAlignmentSpecial.VAS_SUPER)
			_bl_shift=-FontUtils.x_height(font, visual.getGraphicsContext().fontMetrics(font));
		else if(verticalAlign.specified==VerticalAlignmentSpecial.VAS_SUB)
			_bl_shift=FontUtils.x_height(font, visual.getGraphicsContext().fontMetrics(font));
		else if(verticalAlign.specified==VerticalAlignmentSpecial.VAS_LENGTH)
			_bl_shift=-verticalAlign.value;
	}

	@Override
	public void setFont(Font font)
	{
		this.font=font;
	}

}
