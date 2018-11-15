package com.github.s262316.forx.box;

import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.LinkedList;

import com.github.s262316.forx.box.cast.BoxTypes;
import com.github.s262316.forx.box.properties.DimensionsDescriptor;
import com.github.s262316.forx.box.properties.PropertyTableMemberAdaptor;
import com.github.s262316.forx.box.properties.Visual;
import com.github.s262316.forx.box.util.SizeResult;
import com.github.s262316.forx.box.util.SpecialLength;

public class RowGroup implements TableMember, Rowular
{
	private LinkedList<Rowular> rowulars;
	private Visual visual;
	private Graphics2D _canvas;
	private Rowular _row_container;
	private TableBox _table;
	private boolean _anon;
	private int _min_height;

	public RowGroup(Visual v, Drawer draw, boolean anon)
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
		SizeResult size=new SizeResult();
		int minh;
		DimensionsDescriptor dd=new DimensionsDescriptor();

		for(Rowular rg : rowulars)
		{
			if(BoxTypes.isRowGroup(rg)==true)
			{
				size=rg.compute_dimensions();
				rg.set_min_height(size.min_height.value);
			}
		}

		visual.workOutFlowDimensions(new PropertyTableMemberAdaptor(this), dd);

		if(dd.height.specified==SpecialLength.SL_AUTO)
			size.auto_height=true;
		else
		{
			size.auto_height=false;
			size.min_height=dd.height;

			// check if there are any rows present first
			if(rowulars.size()>0)
			{
				minh=dd.height.value;
				// if the rowgroup is taller than the rows
				if(minh > sum_row_min_heights_recurse())
				{
					// widen the rows equally
					int num_rows=high_row_num()-low_row_num()+1;
					int sum_row_heights=sum_row_min_heights_recurse();
					int diff=(minh-sum_row_heights)/num_rows;
					int remainder=(minh-sum_row_heights)%num_rows;
					int i;
					TableRow r;

					if(diff>0 || remainder>0)
					{
						for(i=low_row_num(); i<=high_row_num(); i++)
						{
							r=table().row(i);

							r.change_min_height(diff+(remainder>0?1:0));

							--remainder;
						}
					}
				}
			}
		}

		return size;
	}

	public int low_row_num()
	{
		int lowest=0;
		Rowular r;

		Iterator<Rowular> it=rowulars.iterator();
		while(it.hasNext() && lowest==0)
		{
			r=it.next();
			if(BoxTypes.isRow(r))
				lowest=BoxTypes.toRow(r).row_num();
			else if(BoxTypes.isRowGroup(r))
				lowest=BoxTypes.toRowGroup(r).low_row_num();
			else
	            throw new BoxError(BoxExceptionType.BET_UNKNOWN);
		}

		return lowest;
	}

	public int high_row_num()
	{
		int highest=0;
		Rowular r;

		Iterator<Rowular> it=rowulars.descendingIterator();
		while(it.hasNext() && highest==0)
		{
			r=it.next();

			if(BoxTypes.isRow(r))
				highest=BoxTypes.toRow(r).row_num();
			else if(BoxTypes.isRowGroup(r))
				highest=BoxTypes.toRowGroup(r).high_row_num();
			else
	            throw new BoxError(BoxExceptionType.BET_UNKNOWN);
		}

		return highest;
	}

    @Override
	public void row_back(TableRow c)
	{
		rowulars.add(c);
	}

	public void rowgroup_back(RowGroup c)
	{
		rowulars.add(c);
	}

	private int sum_row_min_heights_recurse()
	{
		int w=0;

		for(Rowular r : rowulars)
		{
			if(BoxTypes.isRow(r))
				w+=BoxTypes.toRow(r).min_height();
			else if(BoxTypes.isRowGroup(r))
				w+=BoxTypes.toRowGroup(r).sum_row_min_heights_recurse();
			else
	            throw new BoxError(BoxExceptionType.BET_UNKNOWN);
		}

		return w;
	}
    
    @Override
	public TableBox table()
	{
		return _table;
	}

    @Override
	public void set_container(TableMember tb)
	{
		_row_container=(Rowular)tb;
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










