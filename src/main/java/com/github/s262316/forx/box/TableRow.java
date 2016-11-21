package com.github.s262316.forx.box;

import com.github.s262316.forx.box.properties.DimensionsDescriptor;
import com.github.s262316.forx.box.properties.PropertyTableMemberAdaptor;
import com.github.s262316.forx.box.properties.Visual;
import com.github.s262316.forx.box.util.SizeResult;
import com.github.s262316.forx.box.util.SpecialLength;

public class TableRow implements TableMember, Rowular
{
	private Visual visual;
	private int _top;
	private int _height;
	private int _row_num;
	private Rowular _row_container;
	private TableBox _table;
	private boolean _anon;
	private int _min_height;

	public TableRow(Visual _visual, Drawer draw, boolean anon)
	{
		visual=_visual;
		_anon=anon;
	}

    @Override
	public void computeProperties()
	{
	}

    @Override
	public SizeResult compute_dimensions()
	{
		// The height of a 'table-row' element's box is calculated once
		// the user agent has all the cells in the row available: it is
		// the maximum of the row's specified 'height' and the minimum
		// height (MIN) required by the cells. A 'height' value of 'auto'
		// for a 'table-row' means the row height used for layout is MIN.
		// MIN depends on cell box heights and cell box alignment (much
		// like the calculation of a line box height). Percentage heights on
		// table cells, table rows, and table row groups compute to 'auto'.

		DimensionsDescriptor dd=new DimensionsDescriptor();
		SizeResult size=new SizeResult();

		visual.workOutFlowDimensions(new PropertyTableMemberAdaptor(this), dd);

		if(dd.height.specified==SpecialLength.SL_AUTO)
		{
			size.auto_height=true;
			size.min_height.set(0);
		}
		else
		{
			size.auto_height=false;
			size.min_height.set(dd.height);
		}

		return size;
	}

	public int top()
	{
		return _top;
	}

	public void set_position(int t)
	{
		_top=t;
	}

	public int height()
	{
		return _height;
	}

	public void set_height(int h)
	{
		_height=h;
		//cout << "row-height[" << row_num << "] now " << _height << endl;
	}

	public void change_height(int ch)
	{
		_height+=ch;
		//cout << "row-height[" <
	}

    @Override
	public void row_back(TableRow c)
	{
		throw new BoxError(BoxExceptionType.BET_UNKNOWN);
	}

    @Override
	public Rowular container()
	{
		return _row_container;
	}

    @Override
	public void set_table(TableBox tb)
	{
		_table=tb;
	}

	public int bottom()
	{
		return _top+_height-1;
	}

	public int row_num()
	{
		return _row_num;
	}

    @Override
	public TableBox table()
	{
		return _table;
	}

	public int begin_cell()
	{
		throw new RuntimeException();

/*
		boolean found=false;
		int i=0;
		Layable lay;

		Iterator<Layable> cit=table().begin_cell();
		while(cit.hasNext() && found==false)
		{
			lay=cit.next();
			if(lay.as_box().asCell().row(1)==_row_num)
				found=true;
			else
				i++;
		}

		return i;*/
	}

	public int cell_size()
	{
		throw new RuntimeException();

/*		int count=0;
		boolean found=false;
		int it=begin_cell();
		Layable lay;

		while(it.hasNext() && found==false)
		{
			lay=it.next();
			if(lay.as_box().asCell().row(1)!=_row_num)
				found=true;

			count++;
			it++;
		}

		return count;*/
	}

    @Override
	public void set_container(TableMember cont)
	{
		_row_container=(Rowular)cont;
	}

	public void set_row_num(int rn)
	{
		_row_num=rn;
	}

    @Override
	public void set_min_height(int minh)
	{
		_min_height=minh;
	}

    @Override
	public int min_height()
	{
		return _min_height;
	}

    @Override
	public void change_min_height(int minh)
	{
		_min_height+=minh;
	}


}



