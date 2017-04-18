package com.github.s262316.forx.css;

class Token
{
	Token()
	{}

	Token(TokenType t, String s)
	{
		type=t;
		syntax=s;
	}

	Token(TokenType t, String s, boolean precedingWhitespaceSkipped)
	{
		type=t;
		syntax=s;
		this.precedingWhitespaceSkipped=precedingWhitespaceSkipped;
	}

	TokenType type;
	String syntax;
	boolean precedingWhitespaceSkipped=false;
}
