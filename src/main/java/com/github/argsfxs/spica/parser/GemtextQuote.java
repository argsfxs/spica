package com.github.argsfxs.spica.parser;

/**
 * This class represents a Gemtext quote element.
 */
public class GemtextQuote extends GemtextText
{
    
    GemtextQuote( String text )
    {
        super( text );
    }
    
    @Override
    public String toString()
    {
        return String.format( ">%s", super.toString() );
    }
}
