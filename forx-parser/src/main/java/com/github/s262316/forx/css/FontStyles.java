package com.github.s262316.forx.css;

import java.util.List;

import com.github.s262316.forx.style.Declaration;
import com.github.s262316.forx.style.Value;

public interface FontStyles
{
	List<Declaration> expand(Declaration toExpand);	
	boolean validateFontStyle(Value v);
	boolean validateFontVariant(Value v);
	boolean validateFontWeight(Value v);
	boolean validateFontSize(Value v);
}
