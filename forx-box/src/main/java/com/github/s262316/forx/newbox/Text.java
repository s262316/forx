package com.github.s262316.forx.newbox;

import java.awt.Font;
import java.awt.FontMetrics;
import java.util.Optional;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.newbox.relayouter.LayoutResult;
import com.google.common.base.MoreObjects;

public class Text implements Box, Inline
{
	private static final Logger logger=LoggerFactory.getLogger(Text.class);

	private Dimensionable dimensions;
    private String text;
    private SpaceFlag space;
	private InterBoxOps boxRelations;
	private Visual visual;
    private int id=BoxCounter.next();

    public Text(String text, SpaceFlag space, InterBoxOps boxRelations, Visual visual)
    {
        this.text=text;
        this.space=space;
    	this.boxRelations=boxRelations;
    	this.visual=visual;
    }

    public String getText()
    {
        return text;
    }

    @Override
    public SizeResult computeDimensions()
    {
        int width;
        SizeResult size;
        FontMetrics fm;

		fm=visual.getGraphicsContext().fontMetrics(boxRelations.containerFont());

        if(isWhitespace())
        	width=fm.stringWidth(" ");
        else
            width=fm.stringWidth(text);

        size=new SizeResult(width, fm.getHeight());

        return size;
    }

    public void computeProperties()
    {}

    public boolean isWhitespace()
    {
        return space==SpaceFlag.SPACE;
    }

    public Font getFont()
    {
        return boxRelations.containerFont();
    }

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
			       .add("id", id)
			       .add("text", text)
			       .add("spaceFlag", space)
			       .toString();
	}

	@Override
	public LayoutResult calculatePosition(Box member)
	{
		throw new NotImplementedException("Text cannot have members");
	}

	@Override
	public void uncalculatePosition(Box member)
	{
		throw new NotImplementedException("Text cannot have members");
	}

	@Override
	public int getId()
	{
		return id;
	}

	@Override
	public Optional<PropertiesEndPoint> propertiesEndpoint()
	{
		return Optional.empty();
	}

	@Override
	public Optional<Box> layable()
	{
		return Optional.of(this);
	}

	@Override
	public int height()
	{
		return dimensions.height();
	}

	@Override
	public int width()
	{
		return dimensions.width();
	}

	@Override
	public int left()
	{
		return dimensions.left();
	}

	@Override
	public int right()
	{
		return dimensions.right();
	}

	@Override
	public int bottom()
	{
		return dimensions.top();
	}

	@Override
	public int top()
	{
		return dimensions.top();
	}

	@Override
	public void setDimensions(int width, int height)
	{
		dimensions.setWidth(width);
		dimensions.setHeight(height);
	}

	@Override
	public void setPosition(int x, int y)
	{
		dimensions.setPosition(x, y);
	}

}
