package com.github.s262316.forx.newbox;

import java.awt.Font;
import java.awt.FontMetrics;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.s262316.forx.newbox.relayouter.LayoutResult;
import com.google.common.base.MoreObjects;

public class Text implements Box
{
	private static final Logger logger=LoggerFactory.getLogger(Text.class);

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
	public boolean isPropertiesEndpoint()
	{
		return false;
	}
}
