package com.github.s262316.forx.box;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.github.s262316.forx.box.cast.BoxTypes;
import com.github.s262316.forx.box.properties.DimensionsDescriptor;
import com.github.s262316.forx.box.properties.PropertyBoxAdaptor;
import com.github.s262316.forx.box.properties.Visual;
import com.github.s262316.forx.box.relayouter.LayoutResult;
import com.github.s262316.forx.box.relayouter.Relayouter;
import com.github.s262316.forx.box.relayouter.util.LayoutUtils;
import com.github.s262316.forx.box.util.SizeResult;
import com.github.s262316.forx.box.util.SpaceFlag;
import com.github.s262316.forx.box.util.SpecialLength;
import com.github.s262316.forx.tree.visual.AnonReason;
import com.google.common.base.Optional;

public class TableBox extends BlockBox
{
	private LinkedList<Layable> cells;//CellBox's only
	private LinkedList<Column> cols;
	private LinkedList<TableRow> rows;
	private LinkedList<ColGroup> colgroups;
	private LinkedList<RowGroup> rowgroups;
	private boolean _anon;

    public TableBox(Visual visual, Drawer drawer, boolean anon)
    {
        super(visual, drawer, null);
        _anon=anon;
    }

    public CellBox get_anon_cell()
    {
        CellBox last;
        if(cells.size() > 0)
        {
            if(BoxTypes.isAnon(cells.getLast()) == true)
                last=BoxTypes.toCellBox(cells.getLast());
            else
            {
                last=visual.createAnonCellBox(AnonReason.CELL);

                table_back(null, last);
            }
        }
        else
        {
            last=visual.createAnonCellBox(AnonReason.CELL);
            table_back(null, last);
        }
        return last;
    }

    @Override
    public void flow_back(Box b)
    {
        CellBox anon=get_anon_cell();
        anon.flow_back(b);
    }

    @Override
    public void flow_back(Inline il)
    {
        CellBox anon=get_anon_cell();
        anon.flow_back(il);
    }

    @Override
    public void flow_insert(Box b, int before)
    {
        CellBox anon=get_anon_cell();
        anon.flow_insert(b, before);
    }

    @Override
    public void float_back(FloatBox b)
    {
        CellBox anon=get_anon_cell();
        anon.float_back(b);
    }

    @Override
    public void float_insert(FloatBox b, int before)
    {
        CellBox anon=get_anon_cell();
        anon.float_insert(b, before);
    }

    @Override
    public void flow_back(String str, SpaceFlag space)
    {
        CellBox anon=get_anon_cell();
        anon.flow_back(str, space);
    }

    @Override
    public void absBack(AbsoluteBox b)
    {
        CellBox anon=get_anon_cell();
        anon.absBack(b);
    }

    @Override
	public void absBackStatic(AbsoluteBox b)
    {
	}

	@Override
    public void table_back(TableMember cont, TableMember memb)
    {
        if(BoxTypes.isColumn(memb) == true)
        {
            Column c=BoxTypes.toColumn(memb);
            cols.add(c);
            c.col_num(cols.size());
            if(cont != null)
            {
                if(BoxTypes.isColGroup(cont))
                    BoxTypes.toColGroup(cont).col_back(c);
            }
        }
        else if(BoxTypes.isColGroup(memb) == true)
        {
            ColGroup cg=BoxTypes.toColGroup(memb);
            colgroups.add(cg);
            if(cont != null)
            {
                if(BoxTypes.isColGroup(cont))
                    BoxTypes.toColGroup(cont).colgroup_back(cg);
            }
        }
        else if(BoxTypes.isCellBox(memb) == true)
        {
            CellBox c=BoxTypes.toCellBox(memb);
            boolean anon_needed=false;
            TableRow cont_row=null;
            int where_col, where_row;

            if(cont == null)
                anon_needed=true;
            else if(BoxTypes.isRow(cont) == false)
                anon_needed=true;
            else
                cont_row=BoxTypes.toRow(cont);
            if(anon_needed == true)
            {
                if(rows.size() > 0)
                {
                    if(BoxTypes.isAnon(rows.getLast()) == true)
                        cont_row=rows.getLast();
                    else
                    {
                        cont_row=visual.createAnonRowBox(AnonReason.ROW);

                        table_back(null, cont_row);

                    }
                }
                else
                {
                    cont_row=visual.createAnonRowBox(AnonReason.ROW);

                    table_back(null, cont_row);
                }
                cont=cont_row;
            }
            if(cells.size() > 0)
            {
                CellBox last_cell;
                last_cell=BoxTypes.toCellBox(cells.getLast());
                where_row=cont_row.row_num();
                if(cont_row.cell_size() == 0)
                    where_col=1;
                else
                    where_col=last_cell.col(1) + 1;
            }
            else
            {
                where_col=1;
                where_row=1;
            }
            if(where_col == cols.size() + 1)
            {
                Column anon_column;
                // create anon column
                anon_column=visual.createAnonColBox(AnonReason.COLUMN);
                table_back(null, anon_column);
            }
            c.set_origin(where_col, where_row);
            c.set_container(this);
            cells.add(c);

        }
        else if(BoxTypes.isRow(memb) == true)
        {
            TableRow tr=BoxTypes.toRow(memb);
            tr.set_row_num(rows.size() + 1);
            rows.add(tr);
            if(cont != null)
            {
                if(BoxTypes.isRowGroup(cont) == true)
                    BoxTypes.toRowGroup(cont).row_back(tr);
            }
        }
        else if(BoxTypes.isRowGroup(memb))
        {
            RowGroup rg=BoxTypes.toRowGroup(memb);
            rowgroups.add(rg);
            if(cont != null)
            {
                if(BoxTypes.isRowGroup(cont) == true)
                    BoxTypes.toRowGroup(cont).rowgroup_back(rg);
            }
        }
        else
            throw new BoxError(BoxExceptionType.BET_UNKNOWN);

        memb.set_container(cont);
        memb.set_table(this);
        memb.computeProperties();
        if(cells.size() > 0)
            LayoutUtils.doLoadingLayout(null);
    }

    @Override
    public void table_insert(TableMember cont, TableMember memb)
    {
    }

    public List<Layable> getMembersCells()
    {
		return Collections.unmodifiableList(cells);
	}

    @Override
    public void computeProperties()
    {
//        super.compute_properties();
    }

    @Override
    public SizeResult compute_dimensions()
    {
        SizeResult size=new SizeResult();
        int col_num, row_num;
        int spans;
        int minw=0, maxw=0;
        int minh=0;
        int h=0;
        DimensionsDescriptor dd=new DimensionsDescriptor();
        // reset column sizes

        for(Column col : cols)
        {
            size=col.compute_dimensions();
            col.set_min_width(size.min_width.value);
            col.set_max_width(size.max_width.value);
        }
        // reset row heights
        for(TableRow row : rows)
        {
            size=row.compute_dimensions();
            row.set_min_height(size.min_height.value);
        }
        for(Layable it : cells)
        {
           CellBox cell=BoxTypes.toCellBox(it);

		   size=cell.compute_dimensions();
		   cell.set_min_max_width(size.min_width.value, size.max_width.value);
		   cell.set_min_height(size.min_height.value);
                    // update row
            if(cell.span_rows()==1)
            {
                row_num=cell.row(1);
                if(rows.get(row_num - 1).min_height() < size.min_height.value)
                    rows.get(row_num - 1).set_min_height(size.min_height.value);
            }
            else
            {
                // rowspan not implemented yet
            }   // update column
            if(cell.span_cols() == 1)
            {
                col_num=cell.col(1);
                if(cols.get(col_num - 1).min_width() < size.min_width.value)
                    cols.get(col_num - 1).set_min_width(size.min_width.value);
                if(cols.get(col_num - 1).max_width() < size.max_width.value)
                    cols.get(col_num - 1).set_max_width(size.max_width.value);
            }
            else
            {
                int combined_col_width_min=0;
                int combined_col_width_max=0;
                int i;    // For each cell that spans more than one column, increase
                // the minimum widths of the columns it spans so that together,
                // they are at least as wide as the cell. Do the same for the
                // maximum widths. If possible, widen all spanned columns by
                // approximately the same amount
                spans=cell.span_cols();
                for(i=1; i <= spans; i++)
                {
                    col_num=cell.col(i);
                    combined_col_width_min+=cols.get(col_num - 1).min_width();
                    combined_col_width_max+=cols.get(col_num - 1).max_width();
                }
                if(combined_col_width_min < size.min_width.value)
                {
                    int diff=(size.min_width.value - combined_col_width_min) / spans;
                    int remainder=(size.min_width.value - combined_col_width_min) % spans;
                    for(i=1; i <= spans; i++)
                    {
                        col_num=cell.col(i);
                        cols.get(col_num - 1).change_min_width(diff + remainder > 0?1:0);
                        remainder--;
                    }
                }
                if(combined_col_width_max < size.max_width.value)
                {
                    int diff=(size.max_width.value - combined_col_width_max) / spans;
                    int remainder=(size.max_width.value - combined_col_width_max) % spans;
                    for(i=1; i <= spans; i++)
                    {
                        col_num=cell.col(i);
                        cols.get(col_num - 1).change_max_width(diff + remainder > 0?1:0);
                        remainder--;
                    }
                }
            }
        }

		// For each column group element with a 'width' other than 'auto',
		// increase the minimum widths of the columns it spans, so that
		// together they are at least as wide as the column group's 'width'.
		for(ColGroup colgroup : colgroups)
		{
			// this call will increase the min width of its member columns
			// if necessary
			size=colgroup.compute_dimensions();
			colgroup.set_min_width(size.min_width.value);
		}

		// For each row group element with a 'height' other than 'auto',
		// increase the minimum widths of the rows it spans, so that
		// together they are at least as wide as the row group's 'height'.
		for(RowGroup rowgroup : rowgroups)
		{
			// this call will increase the min height of its member rows
			// if necessary
			size=rowgroup.compute_dimensions();
			rowgroup.set_min_height(size.min_height.value);
		}

		// now we have a min/max value for every column in the table, even
		// nested columns
		visual.workOutFlowDimensions(new PropertyBoxAdaptor(this), dd);

		// add up the min/max width
		for(Column col : cols)
		{
			minw+=col.min_width();
			maxw+=col.max_width();
		}

		if(dd.width.specified==SpecialLength.SL_AUTO)
		{
			// 2. If the 'table' or 'inline-table' element has 'width: auto', the
			// table width used for layout is the greater of the table's containing
			// block width and MIN. However, if the maximum width required by
			// the columns plus cell spacing or borders (MAX) is less than
			// that of the containing block, use MAX.
			size.auto_width=true;

            if(maxw <= container().contentWidth())
            {
                // maxw
                // column widths are their max

                for(Column col : cols)
                {
                    col.set_width(col.max_width());
                }
            }
            else if(minw > container().contentWidth())
            {
                size.width.set(minw);
                // column widths are their mi

                for(Column col : cols)
                {
                    col.set_width(col.min_width());
                }
            }
            else
            {
                int used=0;
                int sum_cols=0;
                int amount;
                int remainder, extra, cols_with_size=0, each;
                size.width.set(Math.max(minw, container().contentWidth()));
                // column widths are apportioned
                for(Column col : cols)
                {
                    if(size.width.value > 0)
                    {
                        amount=(col.max_width() * size.width.value) / maxw;
                        col.set_width(amount);
                        used+=amount;
                        if(col.width() != 0)
                            ++cols_with_size;
                    }
                    else
                        col.set_width(0);
                }
                // share out the remainder
                remainder=size.width.value - used;
                each=remainder / cols_with_size;
                extra=remainder % cols_with_size;

                for(Column col : cols)
                {
                    if(col.width() != 0)
                    {
                        col.change_width(each);
                        if(extra > 0)
                        {
                            col.change_width(1);
                            --extra;
                        }
                    }
                }

                // are any cols less than their min width?
                remainder=0;
                cols_with_size=0;

                for(Column col : cols)
                {
                    if(col.width() < col.min_width())
                    {
                        remainder+=(col.min_width() - col.width());
                        col.set_width(col.min_width());
                    }
                    else if(col.width() != 0 && col.min_width() != 0)
                        ++cols_with_size;
                }

                // subtract this extra width from wider columns
                each=remainder / cols_with_size;
                extra=remainder % cols_with_size;

                for(Column col : cols)
                {
                    if(col.width() > col.min_width())
                    {
                        col.change_width(-each);
                        if(extra > 0)
                        {
                            col.change_width(-1);
                            --extra;
                        }
                    }
                }
            }
        }
        else
        {
            // 1. If the 'table' or 'inline-table' element's 'width' property
            // has a computed value (W) other than 'auto', the property's
            // value as used for layout is the greater of W and the minimum
            // width required by all the columns plus cell spacing or borders
            // (MIN). If W is greater than MIN, the extra width should be
            // distributed over the columns.
            size.auto_width=false;
            if(dd.width.value > minw)
            {
                // space is bigger than content
                // ratio out the rest of the space
                int num_cols=cols.size();
                if(num_cols > 0)
                {
                    int diff=(dd.width.value - minw) / num_cols;
                    int remainder=(dd.width.value - minw) % num_cols;
                    if(diff > 0 || remainder > 0)
                    {
                        for(Column c : cols)
                        {
                            c.set_width(c.min_width() + diff + (remainder > 0?1:0));
                            --remainder;
                        }
                    }
                }
                size.width=dd.width;
            }
            else
            {
                // content is bigger than space or equals

                for(Column col : cols)
                {
                    col.set_width(col.min_width());
                }
                size.width.set(minw);
            }
        }

        for(TableRow row : rows)
        {
            minh+=row.min_height();
        }

        if(dd.height.specified == SpecialLength.SL_AUTO)
        {
            int used=0;
            int sum_rows=0;
            int amount;
            int remainder, extra, rows_with_size=0, each;
            size.auto_height=true;
            size.height.set(Math.max(minh, dd.height.value));
            if(size.height.value > minh)
            {
                // row heights must be apportioned

                for(TableRow row : rows)
                {
                    if(size.height.value > 0)
                    {
                        amount=(row.min_height() * size.height.value) / size.height.value;
                        row.set_height(amount);
                        used+=amount;
                        if(row.height() != 0)
                            ++rows_with_size;
                    }
                    else
                        row.set_height(0);
                }
                // share out the remainder
                remainder=size.height.value - used;
                each=remainder / rows_with_size;
                extra=remainder % rows_with_size;

                for(TableRow row : rows)
                {
                    if(row.height() != 0)
                    {
                        row.change_height(each);
                        if(extra > 0)
                        {
                            row.change_height(1);
                            --extra;
                        }
                    }
                }
                // are any rows less than their min height?
                remainder=0;
                rows_with_size=0;

                for(TableRow row : rows)
                {
                    if(row.height() < row.min_height())
                    {
                        remainder+=(row.min_height() - row.height());
                        row.set_height(row.min_height());
                    }
                    else if(row.height() != 0 && row.min_height() != 0)
                        ++rows_with_size;
                }
                // subtract this extra height from wider rows
                each=remainder / rows_with_size;
                extra=remainder % rows_with_size;

                for(TableRow row : rows)
                {
                    if(row.height() > row.min_height())
                    {
                        row.change_height(-each);
                        if(extra > 0)
                        {
                            row.change_height(-1);
                            --extra;
                        }
                    }
                }
            }
            else
            {
                // set rows to their min height

                for(TableRow row : rows)
                {
                    row.set_height(row.min_height());
                }
            }
        }
        else
        {
            size.auto_height=false;
            if(dd.height.value > minh)
            {
                // space is bigger than content
                // ratio out the rest of the space
                int num_rows=rows.size();
                if(num_rows > 0)
                {
                    int diff=(dd.height.value - minh) / num_rows;
                    int remainder=(dd.height.value - minw) % num_rows;
                    if(diff > 0 || remainder > 0)
                    {

                        for(TableRow r : rows)
                        {
                            r.set_height(r.min_height() + diff + (remainder > 0?1:0));
                            --remainder;
                        }
                    }
                }
                size.height=dd.height;
            }
            else
            {
                // content is bigger than space or equals

                for(TableRow r : rows)
                {
                    r.set_height(r.min_height());
                }
                size.height.set(minh);
            }
        }
        return size;
    }

    public Column column(int n)
    {
        return cols.get(n - 1);
    }
    // columns, cells, rows
    // no colgroups or rowgroups
    // pos is a cellbox

    @Override
    public LayoutResult calculate_position(Layable member)
    {
        // cout << "table/calculate_position from " << member.id << endl;
		CellBox cell;
		int pos=-99;//TODO temp to make compile

		int start_cell;

		TableRow start_row;
		int x=contentLeft();
		int y=contentTop();
		int row_num;
		// start row
		row_num=BoxTypes.toCellBox(all.get(pos)).row(1);

        start_row=row(row_num);
        if(pos == 0)
        {
            // complete layout
            // columns
            for(Column c : cols)
            {
                c.set_position(x);
                x+=c.width();
            }
            // rows
            for(TableRow r : rows)
            {
                //   cout << "  row_y : " << y << endl;
                r.set_position(y);
                y+=r.height();
            }
        }

        // cells
        // for(; pos!=end_all(); pos++)
        // {
        cell=BoxTypes.toCellBox(all.get(pos));

        x=cols.get(cell.col(1) - 1).left();
        y=rows.get(cell.row(1) - 1).top();
        cell.set_position(x, y);
        cell.set_margins(0, 0, 0, 0);
        //  cell.relayout();
        // }
        return new LayoutResult(true, Optional.<Relayouter>absent());
    }

    @Override
    public void uncalculate_position(Layable member)
    {
    }

//    @Override
//    public Box resize_width(int from, int amount)
//    {
//        throw new BoxError(BoxExceptionType.BET_UNKNOWN);
//    }
//
//    @Override
//    public Layable resize_height(int from, int amount)
//    {
//        // cout << "Table/resize_height" << (*from).id << ", amount: " << amount << endl;
//        int row_num;
//        TableRow affected_row;
//        Layable requester=all.get(from);
//        Layable earliest_affected=null;
//        // start row
//        row_num=BoxTypes.toCellBox(all.get(from)).row(1);
//
//        affected_row=rows.get(row_num - 1);
//        // no point in calling this because a cell doesn't keep its own sizes
//        // requester.change_height(amount);
//        if(requester.height() + amount > affected_row.height())
//        {
//            // change the row height if necessary
//            amount=requester.height() + amount - affected_row.height();
//        }
//        else
//        {
//            // row already big enough
//            amount=0;
//        }
//
//        if(affected_row.height() + amount < affected_row.min_height())
//        {
//            // this shrink is not allowed
//            amount=affected_row.min_height() - affected_row.height();
//        }
//
//        if(amount != 0)
//        {
//            TableRow r;
//            int y;
//            affected_row.change_height(amount);
//
//            int rit=find_row(affected_row);
//            ++rit;
//            y=affected_row.bottom() + 1;
//            // reposition rows
//            for(; rit < rows.size(); rit++)
//            {
//
//                r=(TableRow)all.get(rit);
//
//                r.set_position(y);
//                y+=r.height();
//            }
//        }
//
//        if(requester.bottom() > contentBottom())
//        {
//            //  cout << "changing height of table by " << requester.bottom() - contentBottom() << endl;
//            earliest_affected=container().resize_height(container().getMembersAll().indexOf(this), requester.bottom() - contentBottom());
//        }
//
//        if(amount < 0)
//        {
//            // may need to recalcuate height of rows in rowgroup
//            Rowular r=affected_row.container();
//
//            while(r != null)
//            {
//                r.compute_dimensions();
//                r=r.container();
//            }
//        }
//
//        return earliest_affected;
//    }

    public int find_row(TableRow tr)
    {
        return rows.indexOf(tr);
    }

    public int find_cell(CellBox cb)
    {
        return cells.indexOf(cb);
    }

    public TableRow row(int n)
    {
        return rows.get(n - 1);
    }

    public int col_count()
    {
        return cols.size();
    }

    public int row_count()
    {
        return rows.size();
    }
}

