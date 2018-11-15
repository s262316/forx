package com.github.s262316.forx.box;

import java.awt.Font;
import java.awt.FontMetrics;
import java.util.Collections;
import java.util.List;

import com.google.common.base.MoreObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.box.cast.BoxTypes;
import com.github.s262316.forx.box.relayouter.LayoutResult;
import com.github.s262316.forx.box.util.SizeResult;
import com.github.s262316.forx.box.util.SpaceFlag;
import com.github.s262316.forx.box.util.VerticalAlignment;

import com.google.common.base.Objects;


public class Text extends AtomicInline
{
	private final static Logger logger=LoggerFactory.getLogger(Text.class);

    private String _text;
    private SpaceFlag _space;
    private int _bl_shift, _content_height;

    public Text(String text, int letter_spacing, SpaceFlag space, int bls)
    {
        _text=text;
        _space=space;
        _bl_shift=bls;
    }

    public String text()
    {
        return _text;
    }

    @Override
    public SizeResult compute_dimensions()
    {
        int width=0, height=0;
        SizeResult size=new SizeResult();
        FontMetrics fm;

        // TODO com.github.s262316.forx.support super/subscript extra-height sizes in here
//        container().canvas().setFont(container().font());
		fm=container().getVisual().getGraphicsContext().fontMetrics(container().getFont());

        if(whitespace()==true)
                width=fm.stringWidth(" ");
        else
                width=fm.stringWidth(_text);

        size.width.set(width);
        size.height.set(((InlineBox)container()).getLineHeight()+Math.abs(_bl_shift));

        _content_height=container().getVisual().getGraphicsContext().fontMetrics(container().getFont()).getHeight()+Math.abs(_bl_shift);

		logger.debug("fontMetrics={}, size={}, content_height={}", fm, size, _content_height);
        
        
        return size;
    }

    @Override
    public LayoutResult calculate_position(Layable member)
    {
        throw new BoxError(BoxExceptionType.BET_UNKNOWN);
    }

    @Override
    public void uncalculate_position(Layable member)
    {
        throw new BoxError(BoxExceptionType.BET_UNKNOWN);
    }

    @Override
    public void computeProperties()
    {}

    @Override
    public boolean whitespace()
    {
        return _space==SpaceFlag.SF_SPACE;
    }

    @Override
    public boolean explicit_newline()
    {
        return false;
    }

    @Override
    public void set_margins(int left, int top, int right, int bottom)
    {}

    @Override
    public VerticalAlignment getVerticalAlign()
    {
        return BoxTypes.toInlineBox(container()).getVerticalAlign();
    }

    @Override
    public int bl_shift()
    {
        return BoxTypes.toInlineBox(container()).bl_shift();
    }

    @Override
    public Font getFont()
    {
        return container().getFont();
    }

//    @Override
//    public int line_height()
//    {
//        return container().getLineHeight();
//    }

    @Override
    public int content_height()
    {
        return _content_height;
    }
    
    @Override
    public int content_width()
    {
        return this.width();
    }

    @Override
    public int content_top()
    {
        int gap=height()-content_height();
        int topgap=gap/2;
        int ct;

        ct=top()+topgap;

        return ct;
    }

    @Override
    public int content_bottom()
    {
        int gap=height()-content_height();
        int topgap=gap/2;
        int bottomgap=gap-topgap;
        int cb;

        cb=bottom()-bottomgap;

        return cb;
    }

    @Override
    public int atomic_width()
    {
        return width();
    }

    @Override
	public boolean flows()
	{
		return true;
	}

    @Override
    public List<Layable> getMembersAll()
    {
		return Collections.<Layable>emptyList();
	}

    @Override
    public List<Box> getMembersFlowing()
    {
		return Collections.<Box>emptyList();
	}

    @Override
    public List<FloatBox> getMembersFloating()
    {
		return Collections.<FloatBox>emptyList();
	}

    @Override
    public List<AbsoluteBox> getMembersPositioned()
    {
		return Collections.<AbsoluteBox>emptyList();
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
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
			       .add("id",id)
			       .add("text", _text)
			       .add("spaceFlag", _space)
			       .toString();
	}
}

