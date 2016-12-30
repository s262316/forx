package com.github.s262316.forx.box;

import java.util.Iterator;
import java.util.LinkedList;

import com.github.s262316.forx.box.cast.BoxTypes;
import com.github.s262316.forx.box.properties.DimensionsDescriptor;
import com.github.s262316.forx.box.properties.PropertyTableMemberAdaptor;
import com.github.s262316.forx.box.properties.Visual;
import com.github.s262316.forx.box.util.SizeResult;
import com.github.s262316.forx.box.util.SpecialLength;

public class ColGroup implements TableMember, Columnular
{
	private LinkedList<Columnular> columulars;
	private Visual visual;
	private TableBox _table;
	private Columnular _col_container;
	private boolean _anon;
	private int _min_width;

	public ColGroup(Visual v, Drawer draw, boolean anon)
	{
		visual=v;
		_col_container=null;
		_anon=anon;
	}

    @Override
	public void computeProperties()
	{

	}

    @Override
	public void col_back(Column c)
	{
		columulars.add(c);
	}

	// For each column group element with a 'width' other than 'auto',
	// increase the minimum widths of the columns it spans, so that
	// together they are at least as wide as the column group's 'width'.
    @Override
	public SizeResult compute_dimensions()
	{
		SizeResult size=new SizeResult();
		DimensionsDescriptor dd=new DimensionsDescriptor();
		int minw;

		for(Columnular c : columulars)
		{
			if(BoxTypes.isColGroup(c))
			{
				size=c.compute_dimensions();
				c.set_min_width(size.min_width.value);
			}
		}

		visual.workOutFlowDimensions(new PropertyTableMemberAdaptor(this), dd);

		// width property, but it means min-width in table
		if(dd.width.specified==SpecialLength.SL_AUTO)
			size.auto_width=true;
		else
		{
			size.auto_width=false;
			size.min_width=dd.width;
			size.max_width=dd.width;

			minw=dd.width.value;
			// if the colgroup is wider than the columns
			if(minw > sum_column_min_widths_recurse())
			{
				// widen the columns equally
				int num_cols=high_col_num()-low_col_num()+1;
				int sum_col_widths=sum_column_min_widths_recurse();
				int diff=(minw-sum_col_widths)/num_cols;
				int remainder=(minw-sum_col_widths)%num_cols;
				int i;
				Column c;

				if(diff>0 || remainder>0)
				{
					for(i=low_col_num(); i<=high_col_num(); i++)
					{
						c=table().column(i);

						c.change_min_width(diff+(remainder>0?1:0));
						if(c.max_width() < c.min_width())
							c.set_max_width(c.min_width());
						--remainder;
					}
				}
			}
		}

		return size;
	}


	public int low_col_num()
	{
		int lowest=0;
		Columnular c;

		Iterator<Columnular> it=columulars.iterator();
		while(it.hasNext() && lowest==0)
		{
			c=it.next();

			if(BoxTypes.isColumn(c))
				lowest=BoxTypes.toColumn(c).col_num();
			else if(BoxTypes.isColGroup(c))
				lowest=BoxTypes.toColGroup(c).low_col_num();
			else
	            throw new BoxError(BoxExceptionType.BET_UNKNOWN);
		}

		return lowest;
	}

	public int high_col_num()
	{
		int highest=0;
		Columnular c;

		Iterator<Columnular> it=columulars.descendingIterator();
		while(it.hasNext() && highest==0)
		{
			c=it.next();
			if(BoxTypes.isColumn(c))
				highest=BoxTypes.toColumn(c).col_num();
			else if(BoxTypes.isColGroup(c))
				highest=BoxTypes.toColGroup(c).high_col_num();
			else
	            throw new BoxError(BoxExceptionType.BET_UNKNOWN);
		}

		return highest;
	}

	public void colgroup_back(ColGroup cg)
	{
		columulars.add(cg);
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

	private int sum_column_min_widths_recurse()
	{
		int w=0;

		for(Columnular c : columulars)
		{
			if(BoxTypes.isColumn(c))
				w+=c.min_width();
			else if(BoxTypes.isColGroup(c))
				w+=BoxTypes.toColGroup(c).sum_column_min_widths_recurse();
			else
	            throw new BoxError(BoxExceptionType.BET_UNKNOWN);
		}

		return w;
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

	@Override
	public void set_container(TableMember tb) {
		// TODO Auto-generated method stub
		
	}
    
    
}
