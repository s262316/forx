package com.github.s262316.forx.cast;

import com.github.s262316.forx.box.Layable;
import com.github.s262316.forx.box.Box;
import com.github.s262316.forx.box.TableBox;
import com.github.s262316.forx.box.InlineBox;
import com.github.s262316.forx.box.Inline;
import com.github.s262316.forx.box.BlockBox;
import com.github.s262316.forx.box.FloatBox;
import com.github.s262316.forx.box.AbsoluteBox;
import com.github.s262316.forx.box.CellBox;
import com.github.s262316.forx.box.Column;
import com.github.s262316.forx.box.ColGroup;
import com.github.s262316.forx.box.TableMember;
import com.github.s262316.forx.box.TableRow;
import com.github.s262316.forx.box.RowGroup;
import com.github.s262316.forx.box.AtomicInline;
import com.github.s262316.forx.box.Columnular;
import com.github.s262316.forx.box.Rowular;
import com.github.s262316.forx.box.RootBox;
import com.github.s262316.forx.box.Text;
import com.github.s262316.forx.box.ReplacedInline;
import com.github.s262316.forx.box.InlineBlockBox;

public class BoxTypes
{
	public static boolean isInlineBox(Layable box)
	{
		return box instanceof InlineBox;
	}

	public static InlineBox toInlineBox(Layable box)
	{
		return (InlineBox)box;
	}

	public static boolean isFloatBox(Layable box)
	{
		return box instanceof FloatBox;
	}

	public static FloatBox toFloatBox(Layable box)
	{
		return (FloatBox)box;
	}

	public static boolean isAbsoluteBox(Layable box)
	{
		return box instanceof AbsoluteBox;
	}

	public static AbsoluteBox toAbsoluteBox(Layable box)
	{
		return (AbsoluteBox)box;
	}

	public static boolean isTableBox(Layable box)
	{
		return box instanceof TableBox;
	}

	public static TableBox toTableBox(Layable box)
	{
		return (TableBox)box ;
	}

	public static boolean isAnon(Layable box)
	{
		throw new RuntimeException("not supported");
//		return box.getVisual() instanceof AnonVisual;
	}

	public static boolean isAnon(TableMember box)
	{
		throw new RuntimeException("not supported");
	}

	public static boolean isBlockBox(Layable box)
	{
		return box instanceof BlockBox;
	}

	public static BlockBox toBlockBox(Layable box)
	{
		return (BlockBox)box;
	}

	public static boolean isBox(Layable lay)
	{
		return lay instanceof Box;
	}

	public static boolean isInline(Layable lay)
	{
		return lay instanceof Inline;
	}

	public static Box toBox(Layable lay)
	{
		return (Box)lay;
	}

	public static Inline toInline(Layable lay)
	{
		return (Inline)lay;
	}

	public static CellBox toCellBox(Layable lay)
	{
		return (CellBox)lay;
	}

	public static CellBox toCellBox(TableMember tm)
	{
		return (CellBox)tm;
	}

	public static boolean isCellBox(Layable lay)
	{
		return lay instanceof CellBox;
	}

	public static boolean isCellBox(TableMember tm)
	{
		return tm instanceof CellBox;
	}

	public static Column toColumn(TableMember lay)
	{
		return (Column)lay;
	}

	public static boolean isColumn(TableMember lay)
	{
		return lay instanceof Column;
	}

	public static ColGroup toColGroup(TableMember lay)
	{
		return (ColGroup)lay;
	}

	public static boolean isColGroup(TableMember lay)
	{
		return lay instanceof ColGroup;
	}

	public static ColGroup toColGroup(Columnular lay)
	{
		return (ColGroup)lay;
	}

	public static boolean isColGroup(Columnular lay)
	{
		return lay instanceof ColGroup;
	}

	public static Column toColumn(Columnular lay)
	{
		return (Column)lay;
	}

	public static boolean isColumn(Columnular lay)
	{
		return lay instanceof Column;
	}

	public static RowGroup toRowGroup(TableMember lay)
	{
		return (RowGroup)lay;
	}

	public static boolean isRowGroup(TableMember lay)
	{
		return lay instanceof RowGroup;
	}

	public static RowGroup toRowGroup(Rowular lay)
	{
		return (RowGroup)lay;
	}

	public static boolean isRowGroup(Rowular lay)
	{
		return lay instanceof RowGroup;
	}

	public static TableRow toRow(Rowular lay)
	{
		return (TableRow)lay;
	}

	public static boolean isRow(Rowular lay)
	{
		return lay instanceof TableRow;
	}

	public static TableRow toRow(TableMember lay)
	{
		return (TableRow)lay;
	}

	public static boolean isRow(TableMember lay)
	{
		return lay instanceof TableRow;
	}

	public static boolean isAtomic(Layable lay)
	{
		return lay instanceof AtomicInline;
	}

	public static AtomicInline toAtomic(Layable lay)
	{
		return (AtomicInline)lay;
	}

	public static boolean isRootBox(Layable lay)
	{
		return lay instanceof RootBox;
	}

	public static RootBox toRootBox(Layable lay)
	{
		return (RootBox)lay;
	}

	public static boolean isText(Layable lay)
	{
		return lay instanceof Text;
	}

	public static Text toText(Layable lay)
	{
		return (Text)lay;
	}

	public static boolean isReplacedInline(Layable lay)
	{
		return lay instanceof ReplacedInline;
	}

	public static ReplacedInline toReplacedInline(Layable lay)
	{
		return (ReplacedInline)lay;
	}

	public static boolean isInlineBlockBox(Layable lay)
	{
		return lay instanceof InlineBlockBox;
	}

	public static InlineBlockBox toInlineBlockBox(Layable lay)
	{
		return (InlineBlockBox)lay;
	}

}
