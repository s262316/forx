package com.github.s262316.forx.box.properties;

import java.awt.Font;

import com.google.common.base.Preconditions;

import com.github.s262316.forx.box.TableMember;

public class PropertyTableMemberAdaptor implements PropertyAdaptor
{
	private TableMember tableMember;
	
	public PropertyTableMemberAdaptor(TableMember tableMember)
	{
		this.tableMember=tableMember;
	}

	@Override
	public int contentWidth()
	{
		Preconditions.checkState(false);
		return 0;
	}

	@Override
	public int contentHeight()
	{
		Preconditions.checkState(false);
		return 0;
	}

	@Override
	public PropertyAdaptor getContainer()
	{
		Preconditions.checkState(false);
		return null;
	}

	@Override
	public Font getFont()
	{
		Preconditions.checkState(false);
		return null;
	}
}

