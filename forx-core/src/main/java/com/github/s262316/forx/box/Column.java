package com.github.s262316.forx.box;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.LinkedList;

import com.github.s262316.forx.box.properties.DimensionsDescriptor;
import com.github.s262316.forx.box.properties.PropertyTableMemberAdaptor;
import com.github.s262316.forx.box.properties.Visual;
import com.github.s262316.forx.box.util.SpecialLength;
import com.github.s262316.forx.box.util.SizeResult;

public class Column implements TableMember, Columnular
{
	private int _width;
	private int _max_width;
	private int _col_num;
	private int _left;
	private TableBox _table;
	private Columnular _col_container;
	private Visual visual;
	private boolean _anon;
	private int _min_width;

	public Column(Visual v, Drawer draw, boolean anon)
	{
		visual=v;
		_anon=anon;
	}

    @Override
	public void computeProperties()
	{

	}

    @Override
	public SizeResult compute_dimensions()
	{
		DimensionsDescriptor dd=new DimensionsDescriptor();
		SizeResult size=new SizeResult();

		visual.workOutFlowDimensions(new PropertyTableMemberAdaptor(this), dd);

		size.auto_height=(dd.width.specified==SpecialLength.SL_AUTO);
		size.min_width=dd.width;
		size.auto_width=true;
		size.max_width=dd.width;

		return size;
	}

	public void set_max_width(int mw)
	{
		_max_width=mw;
	}

	public int max_width()
	{
		return _max_width;
	}


	public void change_max_width(int diff)
	{
		_max_width+=diff;
	}

	public int col_num()
	{
		return _col_num;
	}

	public void col_num(int cn)
	{
		_col_num=cn;
	}

	public int width()
	{
		return _width;
	}

	public void set_width(int w)
	{
		_width=w;
	}

	public void change_width(int dw)
	{
		_width+=dw;
	}

	public void set_position(int left)
	{
		_left=left;
	}

	public int left()
	{
		return _left;
	}

    @Override
	public void set_container(TableMember cont)
	{
		_col_container=(Columnular)cont;
	}

    @Override
	public void col_back(Column c)
	{
		throw new BoxError(BoxExceptionType.BET_UNKNOWN);
	}

    @Override
	public TableBox table()
	{
		return _table;
	}

    @Override
	public void set_table(TableBox tb)
	{
		_table=tb;
	}

    @Override
	public void set_min_width(int mw)
	{
		_min_width=mw;
	}

    @Override
	public void change_min_width(int amount)
	{
		_min_width+=amount;
	}

    @Override
	public int min_width()
	{
		return _min_width;
	}

}

















