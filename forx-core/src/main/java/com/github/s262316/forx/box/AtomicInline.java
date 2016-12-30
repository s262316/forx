package com.github.s262316.forx.box;

import java.awt.Font;
import java.util.Iterator;

import com.google.common.base.Preconditions;

import com.github.s262316.forx.box.cast.BoxTypes;
import com.github.s262316.forx.box.util.VerticalAlignment;

public abstract class AtomicInline implements Inline
{
    public int id=BoxCounter.count++;
    private AtomicInline _prev=null, _next=null;
    private Line _line=null;
    private int _baseline;
    private Dimensionable dimensions=new Dimensionable();
    private boolean relative=false;
    private int relTop, relLeft;
    private Box _container;

    public AtomicInline()
    {
    }

    @Override
    public ReplaceableBoxPlugin replace_object()
    {
        return null;
    }

    public AtomicInline next_atomic()
    {
        return _next;
    }

    public AtomicInline prev_atomic()
    {
        return _prev;
    }

    void set_prev_atomic(AtomicInline ail)
    {
        _prev=ail;
    }

    public void set_next_atomic(AtomicInline ail)
    {
        _next=ail;
    }

    public void set_baseline(int bl)
    {
        _baseline=bl;
    }

    public int baseline()
    {
        return _baseline;
    }

    public Line line()
    {
        return _line;
    }

    public void set_line(Line line)
    {
        _line=line;
    }

    public abstract VerticalAlignment getVerticalAlign();

    public boolean whitespace()
    {
        return false;
    }

    public abstract Font getFont();

    @Deprecated
    public abstract int content_height();
    @Deprecated
    public abstract int content_width();
    @Deprecated
    public abstract int content_top();
    @Deprecated
    public abstract int content_bottom();

    public void set_margins(int left, int top, int right, int bottom)
    {
    }

    @Override
    public void set_dimensions(int width, int height)
    {
    	Preconditions.checkArgument(width >= 0);
    	Preconditions.checkArgument(height >= 0);
    	
        dimensions.set_width(width);
        dimensions.set_height(height);
    }

    @Override
    public void set_width(int width)
    {
        dimensions.set_width(width);
    }

    @Override
    public void set_height(int height)
    {
        dimensions.set_height(height);
    }

    @Override
    public boolean is_descendent_of(Box b)
    {
        Box cont=container();

        while(cont != null && cont != b)
            cont=cont.container();

        if(cont == null)
            return false;

        return true;
    }

    @Override
    public int preferred_width()
    {
        return width();
    }

    @Override
    public int preferred_min_width()
    {
        return width();
    }

    @Override
    public int preferred_shrink_width(int avail_width)
    {
        return width();
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
    public boolean is_relative()
    {
        return relative;

    }

    @Override
    public void set_relative(boolean to)
    {
        relative=to;
    }

    @Override
    public int getRelTop()
    {
        return relTop;
    }

    @Override
    public int getRelLeft()
    {
        return relLeft;
    }

    @Override
    public TableBox table_root()
    {
        Layable layable=this;
        boolean found=false;

        while(found == false && layable != null)
        {
            if(BoxTypes.isBox(layable))
            {
                found=BoxTypes.isTableBox(layable);
                if(found)
                    return BoxTypes.toTableBox(layable);
            }

            layable=layable.container();
        }

        return null;
    }

    @Override
    public void set_container(Box cont)
    {
        _container=cont;
    }

    @Deprecated
    @Override
    public Box container()
    {
        return _container;
    }
    
    @Override
    public Box getContainer()
    {
        return _container;
    }

    @Override
    public void set_position(int x, int y)
    {
        dimensions.set_position(x, y);
        if(replace_object() != null)
            replace_object().set_position(x, y);
    }

    @Override
	public void unsetPosition()
	{
    	dimensions.set_position(Dimensionable.INVALID, Dimensionable.INVALID);
        if(replace_object() != null)
            replace_object().set_position(Dimensionable.INVALID, Dimensionable.INVALID);
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

	public void setRelTop(int relTop)
	{
		this.relTop = relTop;
	}

	public void setRelLeft(int relLeft)
	{
		this.relLeft = relLeft;
	}
    
    

}

