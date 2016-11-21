package com.github.s262316.forx.tree.style;

import java.nio.charset.StandardCharsets;

public class StylesheetFactory
{
    public static Stylesheet createEmptyStylesheet()
    {
        return new Stylesheet(StandardCharsets.UTF_8, null);
    }

}
