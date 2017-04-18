package com.github.s262316.forx.css;

import com.github.s262316.forx.style.Declaration;
import com.github.s262316.forx.style.Value;

import java.util.List;

public interface BorderStyles
{
    List<Declaration> expandBorder(Declaration dec);
    boolean validatePaddingOne(Value v);
    boolean validateBorderOneColor(Value v);
    boolean validateBorderOneWidth(Value v);
    boolean validateBorderOneStyle(Value v);
}
