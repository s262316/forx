package com.github.s262316.forx.box.properties;

import java.awt.Font;

import com.github.s262316.forx.box.Box;

public class PropertyBoxAdaptor implements PropertyAdaptor
{
	private Box box;
	
	public PropertyBoxAdaptor(Box box)
	{
		this.box=box;
	}
	
	/* (non-Javadoc)
	 * @see com.github.s262316.forx.box.properties.PropertyAdaptor#contentWidth()
	 */
	@Override
	public int contentWidth()
	{
		return box.contentWidth();
	}
	
    /* (non-Javadoc)
	 * @see com.github.s262316.forx.box.properties.PropertyAdaptor#contentHeight()
	 */
    @Override
	public int contentHeight()
    {
    	return box.contentHeight();
    }
    
    /* (non-Javadoc)
	 * @see com.github.s262316.forx.box.properties.PropertyAdaptor#getContainer()
	 */
    @Override
	public PropertyAdaptor getContainer()
    {
    	return new PropertyBoxAdaptor(box.container());
    }
    
    /* (non-Javadoc)
	 * @see com.github.s262316.forx.box.properties.PropertyAdaptor#getFont()
	 */
    @Override
	public Font getFont()
    {
    	return box.getFont();
    }
}
