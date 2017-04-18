package com.github.s262316.forx.css;

public class VisualState
{
	private boolean link=false;
	private boolean visited=false;
	private boolean hover=false;
	private boolean active=false;
	private boolean focus=false;

	public boolean isLink()
	{
		return link;
	}
	
	public void setLink(boolean link)
	{
		this.link = link;
	}
	
	public boolean isVisited()
	{
		return visited;
	}
	
	public void setVisited(boolean visited)
	{
		this.visited = visited;
	}
	
	public boolean isHover()
	{
		return hover;
	}
	public void setHover(boolean hover)
	{
		this.hover = hover;
	}
	
	public boolean isActive()
	{
		return active;
	}
	
	public void setActive(boolean active)
	{
		this.active = active;
	}
	
	public boolean isFocus()
	{
		return focus;
	}
	
	public void setFocus(boolean focus)
	{
		this.focus = focus;
	}
}
