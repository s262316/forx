package com.github.s262316.forx.newbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.s262316.forx.newbox.relayouter.LayoutResult;
import com.google.common.collect.Iterables;

public class InlineContainerBox implements Box
{
	private Dimensionable dimensions=new Dimensionable();
	private List<Line> lines=new ArrayList<>();
	private InterBoxOps interBoxOps;
	private List<InlineHeadless> inlines=new ArrayList<>();

	public InlineContainerBox(InterBoxOps interBoxOps)
	{
		this.interBoxOps = interBoxOps;
	}

	public void flow(Box inlineContent)
	{
		interBoxOps.memberWasAdded(this);
		inlineContent.propertiesEndpoint().ifPresent(PropertiesEndPoint::computeProperties);
		interBoxOps.doLoadingLayout(inlineContent);
	}

	public void flow(InlineHeadless inlineContent)
	{
		inlines.add(inlineContent);
		interBoxOps.memberWasAdded(this);
		inlineContent.propertiesEndpoint().ifPresent(PropertiesEndPoint::computeProperties);
	}

	@Override
	public SizeResult computeDimensions()
	{
		return new SizeResult(width(), 0);
	}

	@Override
	public LayoutResult calculatePosition(Box box)
	{
		int width=box.width();
		int height=box.height();
		Line currentLine;

		if(lines.isEmpty())
			lines.add(new Line(left(), top(), width, height));

		currentLine=Iterables.getLast(lines);
		int rightOfLastInline=currentLine.lastBox().map(Box::right).orElse(currentLine.right());

		int proposedLeft=rightOfLastInline+1;
		int proposedTop=currentLine.top();

		// fits on line?
		if(proposedLeft+box.width() > right())
		{
			// doesn't fit on line, create new line
			lines.add(new Line(left(), top(), width, height));
			currentLine=Iterables.getLast(lines);
			proposedLeft=currentLine.left();
			proposedTop=currentLine.top();
		}

		box.setPosition(proposedLeft, proposedTop);

		return new LayoutResult(true, Optional.empty());
	}

	@Override
	public void uncalculatePosition(Box member)
	{
		interBoxOps.invalidatePosition(member);
	}

	@Override
	public int getId()
	{
		return 0;
	}

	@Override
	public Optional<PropertiesEndPoint> propertiesEndpoint()
	{
		return Optional.empty();
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

	public int canIncreaseWidthBy(int delta)
	{
		return 0;
	}

	public int canIncreaseHeightBy(int delta)
	{
		return delta;
	}
}
