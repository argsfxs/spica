package com.github.argsfxs.spica.parser;

/**
 * This class represents a Gemtext list item.
 */
public class GemtextListItem extends GemtextText
{
    GemtextListItem( String text )
    {
        super( text );
    }
    
    @Override
    public String toString()
    {
        return String.format( "* %s", super.toString() );
    }
}
