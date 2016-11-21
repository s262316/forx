package com.github.s262316.forx.css;

public class CSSParserException extends Exception
{
	public enum Type
	{
		DECLARATION_BAD_SYNTAX,
		SELECTOR_BAD_SYNTAX,
		IMPORT_RULE_SYNTAX,
		PAGE_RULE_RULE_SYNTAX,
		MEDIA_RULE_RULE_SYNTAX
	}

	public CSSParserException(Type type, String msg)
	{
		super(msg);
	}

}
