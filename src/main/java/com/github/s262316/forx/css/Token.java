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

	TokenType type;
	String syntax;
}
