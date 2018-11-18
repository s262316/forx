package com.github.s262316.forx.newbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.s262316.forx.newbox.relayouter.LayoutResult;
import com.google.common.collect.Iterables;

public class InlineContainerBox implements Box
{
	private List<Line> lines=new ArrayList<>();

	public void flow(Box box, InlineHeadless parent)
	{
		int width=box.width();
		int height=box.height();
		Line currentLine;

		if(lines.isEmpty())
			lines.add(new Line(left(), top(), width, height));

		currentLine=Iterables.getLast(lines);
		int rightOfLastInline=currentLine.lastBox().map(v -> v.right()).orElse(currentLine.right());

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
	}

	@Override
	public SizeResult computeDimensions()
	{
		return null;
	}

	@Override
	public LayoutResult calculatePosition(Box member)
	{
		return null;
	}

	@Override
	public void uncalculatePosition(Box member)
	{
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
	public int height() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int width() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int left() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int right() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int bottom() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int top() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setDimensions(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPosition(int x, int y) {
		// TODO Auto-generated method stub
		
	}
}
