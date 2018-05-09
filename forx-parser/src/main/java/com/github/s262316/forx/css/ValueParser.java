package com.github.s262316.forx.css;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;

import com.github.s262316.forx.style.FunctionValue;
import com.github.s262316.forx.style.HashValue;
import com.github.s262316.forx.style.Identifier;
import com.github.s262316.forx.style.NumericValue;
import com.github.s262316.forx.style.StringValue;
import com.github.s262316.forx.style.UrlValue;
import com.github.s262316.forx.style.Value;
import com.github.s262316.forx.style.ValueList;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public class ValueParser
{
	private Tokenizer tok;
	
	public ValueParser(Tokenizer tok)
	{
		this.tok=tok;
	}
	
    //term
    //: unary_operator?
    //	[ NUMBER S* | PERCENTAGE S* | LENGTH S* | EMS S* | EXS S* | ANGLE S* |
    //	TIME S* | FREQ S* ]
    //| STRING S* | IDENT S* | URI S* | hexcolor | function
    Value parseTerm() throws MalformedURLException, IOException, BadValueException
    {
        Value v=null, any;

        switch(tok.curr.type)
        {
                case CR_IDENT:
                        v=new Identifier(tok.curr.syntax);
                        break;
                case CR_NUMBER:
                        v=new NumericValue(extractDouble(tok.curr.syntax), "px");
                        break;
                case CR_PERCENT:
                        v=extractPercentage(tok.curr.syntax);
                        break;
                case CR_DIMENSION:
                        v=extractDimension(tok.curr.syntax);
                        break;
                case CR_STRING:
                        v=new StringValue(tok.curr.syntax);
                        break;
                case CR_URI:
                        v=new UrlValue(new URL(tok.curr.syntax));
                        break;
                case CR_HASH:
                        v=new HashValue(tok.curr.syntax);
                        break;
                case CR_FUNCTION:
                {
                        // urls come out of the tokenizer as TokenType.CR_URIs not TokenType.CR_FUNCTION
                        // (function S* >> (any ((comma | slash) S* any)* >> rparen)(TokenType.CR_ANY)),
                        FunctionValue fv;

                        fv=new FunctionValue();
                        fv.name=tok.curr.syntax;
                        fv.values=new ValueList();

                        tok.advance();
                        while(!tok.curr.syntax.equals(")") && tok.curr.type!=TokenType.CR_END)
                        {
                                any=parseTerm();
                                fv.values.members.add(any);

                                tok.advance();
                                if(tok.curr.syntax.equals(",") || tok.curr.syntax.equals("/"))
                                        tok.advance();
                        }

						if (!tok.curr.syntax.equals(")"))
							throw new BadValueException("found " + tok.curr.syntax + ", expected ')'");

                        v=fv;

                        break;
                }
        }
        return v;
    }

    //operator  : '/' S* | COMMA S* | /* empty */
    //expr  : term [ operator term ]*
    public ValueList parse() throws CSSParserException, MalformedURLException, IOException, BadValueException
    {
    	try
    	{
	        ValueList value=new ValueList();
	        Value v1;
	        ValueList subList=null;
	
	        v1=parseTerm();
	        while(v1!=null)
	        {
	        	tok.advance();
	        	
	        	// if next is a / or , use the sublist
	            if(tok.curr.syntax.equals("/") || tok.curr.syntax.equals(","))
	            {
	            	if(subList==null)
	            	{
	            		subList=new ValueList();
	            		value.members.add(subList);
	            	}
	            }

	        	MoreObjects.firstNonNull(subList, value).members.add(v1);

	            if(!(tok.curr.syntax.equals("/") || tok.curr.syntax.equals(",")))
            		subList=null; // the next symbol is not a comma, don't use the sublist

	        	
	        	if(tok.curr.syntax.equals("/") || tok.curr.syntax.equals(",") || tok.curr.type==TokenType.CR_WHITESPACE)
	            	tok.advance();

	            v1=parseTerm();
	        }
	
	        return value;
    	}
    	catch(TokenizationException te)
    	{
    		throw new BadValueException(te);
    	}
    }

	// num  [0-9]+|[0-9]*\.[0-9]+
	double extractDouble(String syntax) throws IOException
	{
		StreamTokenizer st=new StreamTokenizer(new StringReader(syntax));
		Number n=null;
		BigDecimal bd;

		st.nextToken();
		if(st.ttype==StreamTokenizer.TT_NUMBER)
		{
			return st.nval;
		}

		return 0;
	}

	
    //if 96dpi
    //1px = 1/96 inch = 0.26mm
    //1 inch = 2.54cm
    //in: inches - 1 inch is equal to 2.54 centimeters.
    //cm: centimeters
    //mm: millimeters
    //pt: points - the points used by CSS 2.1 are equal to 1/72th of an inch.
    //pc: picas - 1 pica is equal to 12 points.
	
	//DIMENSION  {num}{ident}
	Value extractDimension(String syntax) throws IOException
	{
		StreamTokenizer st=new StreamTokenizer(new StringReader(syntax));
		double amount;
		String unit;

		st.nextToken();
		if(st.ttype==StreamTokenizer.TT_NUMBER)
		{
			amount=st.nval;
			
			st.nextToken();
			if(st.ttype==StreamTokenizer.TT_WORD)
				unit=st.sval;
			
			switch(st.sval)
			{
			case "px":
				return new NumericValue(amount, "px");
			case "cm":
				return new NumericValue(amount, "cm");
			case "em":
				return new NumericValue(amount, "em");
			case "mm":
				return new NumericValue(amount, "mm");
			case "pt":
				return new NumericValue(amount, "pt");
			case "ex":
				return new NumericValue(amount, "ex");
			case "pc":
				return new NumericValue(amount, "pc");
			default:
				return new NumericValue(amount, "px");
			}
		}
		
		Preconditions.checkState(false);
		return null;
	}

	// PERCENTAGE  {num}%
	NumericValue extractPercentage(String syntax) throws IOException
	{
			StreamTokenizer st=new StreamTokenizer(new StringReader(syntax));
			NumericValue pv=null;
			BigDecimal bd;

			st.nextToken();
			if(st.ttype==StreamTokenizer.TT_NUMBER)
			{
				pv=new NumericValue(st.nval, "%");
			}

			return pv;
	}
}
