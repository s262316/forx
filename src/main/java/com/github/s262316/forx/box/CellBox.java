package com.github.s262316.forx.box;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.LinkedList;

import com.github.s262316.forx.box.cast.BoxTypes;
import com.github.s262316.forx.box.properties.DimensionsDescriptor;
import com.github.s262316.forx.box.properties.PropertyBoxAdaptor;
import com.github.s262316.forx.box.properties.Visual;
import com.github.s262316.forx.box.util.FloatPosition;
import com.github.s262316.forx.box.util.SizeResult;

public class CellBox extends BlockBox implements TableMember
{
	private int _min_width, _max_width, _min_height;
	private int origin_col, origin_row;
	private int span_high, span_wide;
	private TableRow _row_container;
	private TableBox _table;
	private boolean _anon;
	private FlowContext flowContext;

	public CellBox(Visual visual, Drawer draw, int col_span, int row_span, boolean anon)
	{
		super(visual, draw, null);

		span_high=col_span;
		span_wide=row_span;
		_anon=anon;
	}

    @Override
	public void computeProperties()
	{
		super.computeProperties();
	}

	// In CSS 2.1, the height of a cell box is the maximum of
	// the table cell's 'height' property and the minimum height
	// required by the content (MIN). A value of 'auto' for 'height'
	// implies that the value MIN will be used for layout. CSS 2.1
	// does not define what percentage values of 'height' refer to
	// when specified for table cells.
    @Override
	public SizeResult compute_dimensions()
	{
		SizeResult result=new SizeResult();
		int min_width, max_width;
		int min_height;
		DimensionsDescriptor dd=new DimensionsDescriptor();

		visual.workOutFlowDimensions(new PropertyBoxAdaptor(this), dd);
		min_width=dd.width.value;
		min_height=dd.height.value;
		max_width=0;

		for(Layable lit : all)
		{
			if(BoxTypes.isBlockBox(lit)==true)
			{
				max_width+=lit.preferred_width();
				min_width=Math.max(min_width, lit.preferred_min_width());
			}
			else if(BoxTypes.isInline(lit)==true)
			{
				max_width+=BoxTypes.toInline(lit).preferred_width();
				min_width=Math.max(min_width, lit.preferred_min_width());
			}
		}

		result.min_width.set(min_width);
		result.max_width.set(Math.max(Math.max(max_width, dd.width.value), min_width));
		result.min_height.set(min_height);

		return result;
	}

//    @Override
//	public Layable resize_height(int from, int amount)
//	{
//		Layable requester=all.get(from);
//		Layable earliest_affected=null;
//
//	//	cout << "cellbox/resize_height, from : " << (*from).id << ", amount : " << amount << endl;
//
//		// force the change in height
//		requester.change_height(amount);
//
//		if(requester.bottom() > contentBottom())
//		{
//			earliest_affected=table().resize_height(table().find_cell(this), requester.bottom() - contentBottom()+1);
//		}
//
//		return earliest_affected;
//	}

	public int span_cols()
	{
		return span_wide;
	}

	public int span_rows()
	{
		return span_high;
	}

	public int col(int n)
	{
		return origin_col+n-1;
	}

	public int row(int n)
	{
		return origin_row+n-1;
	}

	public void set_origin(int col_num, int row_num)
	{
		origin_col=col_num;
		origin_row=row_num;
	}

	public void set_min_max_width(int minw, int maxw)
	{
		_min_width=minw;
		_max_width=maxw;
	}

	public void set_min_height(int minh)
	{
		_min_height=minh;
	}

    @Override
	public TableBox table()
	{
		return BoxTypes.toTableBox(super.container());
	}

    @Override
	public void set_container(TableMember cont)
	{
		_row_container=BoxTypes.toRow(cont);
	}

    @Override
	public int contentWidth()
	{
		return width();
	}

    @Override
	public int contentHeight()
	{
		return height();
	}

    @Override
	public int height()
	{
		int i;
		int h=0;

		for(i=origin_row; i<=origin_row+span_high-1; i++)
		{
			h+=table().row(i).height();
		}

		return h;
	}

    @Override
	public int width()
	{
		int i;
		int w=0;

		for(i=origin_col; i<=origin_col+span_wide-1; i++)
		{
			w+=table().column(i).width();
		}

		return w;
	}

    @Override
	public int bottom()
	{
		if(top()==Dimensionable.INVALID || height()==Dimensionable.INVALID)
			return Dimensionable.INVALID;

		if(height()==0)
			return top();

		return top()+height()-1;
	}

    @Override
	public int right()
	{
		if(left()==Dimensionable.INVALID || width()==Dimensionable.INVALID)
			return Dimensionable.INVALID;

		if(width()==0)
			return left();

		return left()+width()-1;
	}

    @Override
	public void set_table(TableBox tb)
	{
		_table=tb;
	}

    @Override
	public Font getFont()
	{
		return super.getFont();
	}

    @Override
	public boolean flows()
	{
		return false;
	}

}

