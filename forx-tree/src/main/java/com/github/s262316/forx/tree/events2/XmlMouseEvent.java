package com.github.s262316.forx.tree.events2;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;


public class XmlMouseEvent implements Event
{
	private MouseEventType eventType;
	private int mouseX, mouseY;
	
	public XmlMouseEvent(MouseEventType subtype, int mouseX, int mouseY)
	{
		this.eventType=subtype;
		this.mouseX=mouseX;
		this.mouseY=mouseY;		
	}

	public MouseEventType getEventType()
	{
		return eventType;
	}

	public int getMouseX()
	{
		return mouseX;
	}

	public int getMouseY()
	{
		return mouseY;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hashCode(eventType, mouseX, mouseY);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) {return false; }
		
		XmlMouseEvent rhs = (XmlMouseEvent) obj;
		
		return Objects.equal(eventType, rhs.eventType) &&
				Objects.equal(mouseX, rhs.mouseX) &&
				Objects.equal(mouseY, rhs.mouseY);
	}

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
			.addValue(eventType)
			.add("mouseX", mouseX)
			.add("mouseY", mouseY)
			.toString();
	}

}
