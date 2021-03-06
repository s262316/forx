package com.github.s262316.forx.css;

import java.util.function.Predicate;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tokenizer
{
	private final static Logger logger= LoggerFactory.getLogger(Tokenizer.class);

	private CharSequence source;
	private boolean skipping_enabled;
	private int begin;
	private SyntaxPairs syntaxPairs=new SyntaxPairs();
	
	public Token curr;

	public Tokenizer(CharSequence source)
	{
		this.begin=0;
		this.source=source;
		skipping_enabled=true;
	}

	public void skipping(boolean on)
	{
		skipping_enabled=on;
	}

	public void advancePast(TokenType type, String syntax)
	{
		while(!(curr.type==type && curr.syntax.equals(syntax)) && curr.type!=TokenType.CR_END)
		{
			advance();
		}
		
		if(curr.type!=TokenType.CR_END)
			advance();
	}
	
	public void advanceTo(TokenType type, String syntax)
	{
		while(!(curr.type==type && curr.syntax.equals(syntax)) && curr.type!=TokenType.CR_END)
		{
			advance();
		}
	}
	
	// finishes on untilTrue=true
	public void advanceUntil(Predicate<Tokenizer> untilTrue)
	{
		logger.debug("current token {}", curr.syntax);

		while(!(untilTrue.test(this)) && curr.type!=TokenType.CR_END)
		{
			logger.debug("tests true, current token {}", curr.syntax);
			advance();
		}
	}
	
	public void advance()
	{
		Token t=nextToken();

		if(skipping_enabled || t.type==TokenType.CR_COMMENT)
		{
			while((skipping_enabled && t.type==TokenType.CR_WHITESPACE) || t.type==TokenType.CR_COMMENT)
			{
				t = nextToken();
				t.precedingWhitespaceSkipped=true;
			}
		}

		Token next=peekToken();

		if(t.type==TokenType.CR_NUMBER && next.type==TokenType.CR_IDENT && next.precedingWhitespaceSkipped==false)
		{
			curr=new Token(TokenType.CR_DIMENSION, t.syntax+next.syntax, t.precedingWhitespaceSkipped);
			nextToken();
		}
		else if(t.type==TokenType.CR_NUMBER && next.syntax.equals("%") && next.precedingWhitespaceSkipped==false)
		{
			curr=new Token(TokenType.CR_PERCENT, t.syntax, t.precedingWhitespaceSkipped);
			nextToken();
		}
		else if(t.syntax.equals("#"))
		{
			String hash_value="";

			t=peekToken();
			while ((t.type == TokenType.CR_IDENT || t.type == TokenType.CR_NUMBER) &&
					t.precedingWhitespaceSkipped==false)
			{
				nextToken();

				hash_value += t.syntax;
				t = peekToken();
			}

			curr=new Token(TokenType.CR_HASH, hash_value);
	    }
		else if(t.syntax.equals("url") && next.syntax.equals("(") && next.precedingWhitespaceSkipped==false)
		{
			String url="";

			t=nextToken(); // (
			t=nextToken();
			if(t.type==TokenType.CR_WHITESPACE)
				t=nextToken();

			if(t.type==TokenType.CR_STRING)
			{
				url=t.syntax;

				t=nextToken();
				if(t.type==TokenType.CR_WHITESPACE)
					t=nextToken();

				// should be a rparen here. but just accept it if missing

				if(!(t.type==TokenType.CR_PUNCT && t.syntax.equals(")")))
				{
				    logger.warn("Missing rparen in url function");
				}
			}
			else
			{
				// scoop up whatever's there
				while(!t.syntax.equals(")") && t.type!=TokenType.CR_END)
				{
					url+=t.syntax;
					t=nextToken();
				}
			}

			curr=new Token(TokenType.CR_URI, url);
		}
		else if(t.type==TokenType.CR_IDENT && next.syntax.equals("(") && next.precedingWhitespaceSkipped==false)
		{
			curr=new Token(TokenType.CR_FUNCTION, t.syntax);
			nextToken();
		}
		else if(t.syntax.equals("!") && next.syntax.equals("important"))
		{
			curr=new Token(TokenType.CR_PRI, t.syntax+next.syntax);
			t=nextToken();
			if(t.type==TokenType.CR_WHITESPACE)
				t=nextToken();
		}
		else
			curr=t;
	}

	Token peekToken()
	{
		Token peek;

		int mark=begin;

		peek=nextToken();
		if(skipping_enabled || peek.type==TokenType.CR_COMMENT)
		{
			while((skipping_enabled && peek.type==TokenType.CR_WHITESPACE) || peek.type==TokenType.CR_COMMENT)
			{
				peek = peekToken();
				peek.precedingWhitespaceSkipped=true;
			}
		}

		begin=mark;

		return peek;
	}

	Token nextToken()
	{
		if(begin>=source.length())
			return new Token(TokenType.CR_END, "");

		char input=source.charAt(begin);
		int start=begin;

		if(isWhitespace(input))
		{
			++begin;
			while(begin<source.length() && isWhitespace(source.charAt(begin)))
				++begin;

			return new Token(TokenType.CR_WHITESPACE, source.subSequence(start, begin).toString());
		}
		// nmstart
		else if(Character.isLetter(input))
		{
			// ident
			Range<Integer> identRange=tok_ident();

			String ident=StringUtils.substring(source.toString(), identRange.getMinimum(), identRange.getMaximum()+1);
			String decodedIdent=replaceEscapes(new StringBuilder(ident));

			return new Token(TokenType.CR_IDENT, decodedIdent);
		}
		else if(input=='-')
		{
			// could be a negative number or ident or cdc
			++begin;
			if(Character.isDigit(source.charAt(begin)) || source.charAt(begin)=='.')
			{
				tok_num();
				return new Token(TokenType.CR_NUMBER, source.subSequence(start, begin).toString());
			}
			else if(source.charAt(begin)=='-')
			{
				tok_cd();
				return new Token(TokenType.CR_CD, source.subSequence(start, begin).toString());
			}
			else
			{
			    --begin;
				Range<Integer> identRange=tok_ident();
				String ident=StringUtils.substring(source.toString(), identRange.getMinimum(), identRange.getMaximum()+1);
				String decodedIdent=replaceEscapes(new StringBuilder(ident));

				return new Token(TokenType.CR_IDENT, decodedIdent);
			}
		}
		else if(input=='_')
		{
			//ident
			Range<Integer> identRange=tok_ident();
			String ident=StringUtils.substring(source.toString(), identRange.getMinimum(), identRange.getMaximum()+1);
            String decodedIdent=replaceEscapes(new StringBuilder(ident));

			return new Token(TokenType.CR_IDENT, decodedIdent);
		}
		else if(!(input>='\0' && input<='\237'))
		{
			// ident (nonascii)
			tok_ident();

			StringBuilder ident=new StringBuilder(source.subSequence(start, begin));
            String decodedIdent=replaceEscapes(ident);

			return new Token(TokenType.CR_IDENT, decodedIdent);
		}
		else if(input=='\\')
		{
			// ident (escape)
            Range<Integer> charRange=tok_ident();
            String ident=StringUtils.substring(source.toString(), charRange.getMinimum(), charRange.getMaximum()+1);
            String decodedIdent=replaceEscapes(new StringBuilder(ident));

            return new Token(TokenType.CR_IDENT, decodedIdent);
		}
		else if(input=='\'')
		{
			String str;
			StringBuilder sb;

			++begin;
			if(tok_str(input))
			{
				if (source.charAt(begin - 1) == '\'')
					str = source.subSequence(start + 1, begin - 1).toString();
				else
					str = source.subSequence(start + 1, begin).toString();

				// replace \\{nl}
				str = str.replaceAll("\r\n|\r|\n|\f", "");
				sb = new StringBuilder(str);

				String decodedString=replaceEscapes(sb);

				return new Token(TokenType.CR_STRING, decodedString);
			}
			else
			{
				// bad string
				throw new TokenizationException("invalid string");
			}
		}
		else if(input=='\"')
		{
			String str;
			StringBuilder sb;

			++begin;
			if(tok_str(input))
			{
				str = source.subSequence(start + 1, begin - 1).toString();

				// replace \\{nl}
				str = str.replaceAll("\r\n|\r|\n|\f", "");
				sb = new StringBuilder(str);

                String decodedString=replaceEscapes(sb);

				return new Token(TokenType.CR_STRING, decodedString);
			}
			else
			{
				// bad string
				throw new TokenizationException("invalid string");
			}
		}
		else if(Character.isDigit(input))
		{
			tok_num();
			// num
			return new Token(TokenType.CR_NUMBER, source.subSequence(start, begin).toString());
		}
		else if(input=='.')
		{
			++begin;
			if(Character.isDigit(source.charAt(begin)))
			{
				tok_num();
				// num
				return new Token(TokenType.CR_NUMBER, source.subSequence(start, begin).toString());
			}
		    else
		    {
				// class
				return new Token(TokenType.CR_PUNCT, ".");
			}
		}
		else if(input=='<')
		{
			++begin;
			tok_cd();
			return new Token(TokenType.CR_CD, source.subSequence(start, begin).toString());
		}
		else if(input=='~')
		{
			++begin;
			if(source.charAt(begin)=='=')
			{
				++begin;
				return new Token(TokenType.CR_INCLUDES, "~=");
			}
			else
				return new Token(TokenType.CR_PUNCT, "~");
		}
		else if(input=='|')
		{
			++begin;
			if(source.charAt(begin)=='=')
			{
				++begin;
				return new Token(TokenType.CR_DASHMATCH, "|=");
			}
			else
				return new Token(TokenType.CR_PUNCT, "|");
		}
		else if(input=='/')
		{
			boolean done=false;
			++begin;
			if(source.charAt(begin)=='*')
			{
				++begin;
				while(!done)
				{
					if(source.charAt(begin)=='*')
					{
						++begin;
						if(source.charAt(begin)=='/')
							done=true;
					}
					++begin;
				}
				return new Token(TokenType.CR_COMMENT, "");
			}
			else
				return new Token(TokenType.CR_PUNCT, "/");			
		}
		else if(input=='+')
		{
			++begin;
			if(Character.isDigit(source.charAt(begin)))
			{
				tok_num();
				return new Token(TokenType.CR_NUMBER, source.subSequence(start, begin).toString());
			}
			else
				return new Token(TokenType.CR_PUNCT, "+");
		}
		else
		{
			++begin;
			return new Token(TokenType.CR_PUNCT, String.valueOf(input));
		}
	}

	boolean tok_cd()
	{
		char input=source.charAt(begin);
		boolean result=false;

		if(input=='-')
		{
			++begin;
			if(source.charAt(begin)=='>')
			{
				++begin;
				result=true;
			}
		}
		else if(input=='!')
		{
			++begin;
			if(source.charAt(begin)=='-')
			{
				++begin;
				if(source.charAt(begin)=='-')
				{
					++begin;
					result=true;
				}
			}
		}

		return result;
	}

	// [0-9]+|[0-9]*\.[0-9]+
	boolean tok_num()
	{
		char input=source.charAt(begin);
		boolean isDigit=true;

		if(input=='-' || input=='+')
			++begin;

		// before dot
		while(isDigit && begin<source.length())
		{
			input=source.charAt(begin);
			isDigit=Character.isDigit(input);
			if(isDigit)
				++begin;
		}

		// after dot
		if(input=='.')
		{
			input=source.charAt(++begin);
			isDigit=true;

			while(isDigit && begin<source.length())
			{
				input=source.charAt(begin);
				isDigit=Character.isDigit(input);
				if(isDigit)
					++begin;
			}
		}

		return true;
	}

	// \"([^\n\r\f\\"]|\\{nl}|{escape})*\"
	// must close strings upon reaching the end of a line
	// but then drop the construct (declaration or rule) in which the string was found
	boolean tok_str(char quote)
	{
		char input;
		boolean collect=true;

		while(collect && begin!=source.length())
		{
			input=source.charAt(begin);
			if(input=='\r' || input=='\n')
				return false;

			if(input==quote)
				collect=false;
			else if(input=='\\')
			{
				// could be a linebreak or escape
				++begin;
				if(source.charAt(begin)=='\r' || source.charAt(begin)=='\n' || source.charAt(begin)=='\f')
					tok_nl();
				else
					tok_escape();
			}

			++begin;
		}

		return true;
	}

	boolean tok_nl()
	{
		boolean result=false;

		if(source.charAt(begin)=='\r')
		{
			++begin;
			if(source.charAt(begin)=='\n')
				++begin;

			result=true;
		}
		else if(source.charAt(begin)=='\n')
		{
			++begin;
			result=true;
		}
		else if(source.charAt(begin)=='\f')
		{
			++begin;
			result=true;
		}

		return result;
	}

	// input: first number/digit
	boolean tok_escape()
	{
		boolean cont=true, result=true;
		char input;

		input=source.charAt(begin);
		if((input>='A' && input<='F') ||
			(input>='a' && input<='f') ||
			(input>='0' && input<='9'))
		{
			++begin;
			input=source.charAt(begin);

			for(int i=2; i<=6 && cont && begin+1<source.length(); ++i)
			{
				if((input>='A' && input<='F') ||
					(input>='a' && input<='f') ||
					(input>='0' && input<='9'))
				{
					++begin;
					input=source.charAt(begin);
				}
				else
					cont=false;
			}

			if(isWhitespace(input))
			{
				if(input=='\r')
				{
					++begin;
					input=source.charAt(begin);
					if(input!='\n')
						--begin;
				}
				else
				    ++begin;
			}
			else
			{
				--begin;
				result=false;
			}
		}
		else if(input!='\n' && input!='\r' && input!='\f' &&
				!(input>='0' && input<='9') &&
				!(input>='a' && input<='f') &&
				!(input>='A' && input<='F'))
		{
			++begin;
		}
		else
			result=false;

		return result;
	}

	Range<Integer> tok_ident()
	{
		char input;
		boolean collect=true;
		int start=begin;
		int i=0;

		while(collect && begin<source.length())
		{
			input=source.charAt(begin);

			if(Character.isLetterOrDigit(input))
				++begin;
			else if(input=='_')
				++begin;
			else if(input=='-')
				++begin;
			else if(!(input>='\0' && input<='\237'))
				++begin;
			else if(input=='\\')
			{
                // could be a newline.
                int beforeNlCheck=begin;
				++begin;
                if(tok_nl())
                {
                    if(i==0)
                    {
                        // if the beginning of the identifier then skip it
                        start=begin;
                    }
                    else
                    {
                        // leave the newline for someone else (probably this same function on a subequent call)
                        begin = beforeNlCheck-1;
                        collect = false;
                    }
                }
                else
                {
                    tok_escape();
                }
			}
			else
            {
                collect = false;
                --begin;
            }

			i++;
		}

		// this.begin should be 1 greater than the range
		begin++;
		return Range.between(start, begin-1);
	}

	/*
	unicode  \\[0-9a-f]{1,6}(\r\n|[ \n\r\t\f])?
	escape  {unicode}|\\[^\n\r\f0-9a-f]
	*/
	String replaceEscapes(StringBuilder str)
	{
		int slashPos;
		int escapeLength;
		String unicode;
		char ch;
		int codepoint;
		StringBuilder syntax=new StringBuilder(str); // build the replacement here

		logger.debug("replaceEscapes({})", str);

		slashPos=StringUtils.indexOf(syntax, "\\");
		while(slashPos!=-1)
		{
			logger.debug("slashPos is at {}", slashPos);

			if(syntax.charAt(slashPos+1)!='\n' && syntax.charAt(slashPos+1)!='\r' && syntax.charAt(slashPos+1)!='\f' &&
				!(syntax.charAt(slashPos+1) >='0' && syntax.charAt(slashPos+1) <= '9') &&
				!(syntax.charAt(slashPos+1) >='a' && syntax.charAt(slashPos+1) <= 'f') &&
				!(syntax.charAt(slashPos+1) >='A' && syntax.charAt(slashPos+1) <= 'F'))
			{
				// deals with \" and \{ etc
				syntax.deleteCharAt(slashPos);
				escapeLength=1;

				logger.debug("syntax is now:\"{}\"", syntax);
			}
			else
			{
				// backslash with six hex digits
				// must be in the range 0-9, A-F, a-f
				// whitespace afterwards is discarded, even with 6 hex digits

				unicode="";
				escapeLength=1;

				// gather up to 6 hex digits
				while(slashPos+escapeLength<syntax.length() && syntax.charAt(slashPos+escapeLength)!='\r' && syntax.charAt(slashPos+escapeLength)!='\n' && syntax.charAt(slashPos+escapeLength)!='\t' &&
					syntax.charAt(slashPos+escapeLength)!='\f' && syntax.charAt(slashPos+escapeLength)!=' ' && escapeLength<=6 && (
					(syntax.charAt(slashPos+escapeLength) >='0' && syntax.charAt(slashPos+escapeLength) <= '9') ||
					(syntax.charAt(slashPos+escapeLength) >='a' && syntax.charAt(slashPos+escapeLength) <= 'f') ||
					(syntax.charAt(slashPos+escapeLength) >='A' && syntax.charAt(slashPos+escapeLength) <= 'F')))
				{
					unicode+=syntax.charAt(slashPos+escapeLength);
					escapeLength++;
				}

				if(slashPos+escapeLength<syntax.length() &&
						(syntax.charAt(slashPos+escapeLength)=='\r' || syntax.charAt(slashPos+escapeLength)=='\n' ||
								syntax.charAt(slashPos+escapeLength)=='\t' || syntax.charAt(slashPos+escapeLength)=='\f' ||
								syntax.charAt(slashPos+escapeLength)==' '))
				{
					// want to replace the trailing space
					escapeLength++;

					// look for a \r\n
					if(slashPos+escapeLength<syntax.length() &&
							syntax.charAt(slashPos+escapeLength)=='\r')
					{
						// \r\n is counted as one whitespace
						escapeLength++;
					}
				}

				codepoint=Integer.parseInt(unicode, 16);

				ch=(char)codepoint;
				syntax.replace(slashPos, slashPos+escapeLength, String.valueOf(ch));

				logger.debug("syntax is now:\"{}\"", syntax);
			}

			slashPos=StringUtils.indexOf(syntax, "\\", slashPos+1);
		}

		return syntax.toString();
	}

	// com.github.s262316.forx.css whitespace not the same as Java Character.isWhitespace
	boolean isWhitespace(char ch)
	{
		return ch=='\u0020' || ch=='\u0009' || ch==0xA || ch==0xD || ch==0xC;
	}
}
