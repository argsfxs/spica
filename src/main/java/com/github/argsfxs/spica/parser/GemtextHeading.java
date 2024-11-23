package com.github.argsfxs.spica.parser;

import com.github.argsfxs.spica.types.HeadingLevel;

/**
 * This class represents a Gemtext heading element.
 */
public class GemtextHeading extends GemtextText
{
    private final HeadingLevel level;
    
    GemtextHeading( String text, HeadingLevel level )
    {
        super( text );
        this.level = level;
    }
    
    /**
     * Returns the level of the heading.
     *
     * @return the heading level
     */
    public HeadingLevel getLevel()
    {
        return level;
    }
    
    @Override
    public String toString()
    {
        int hashCount = level.getHashCount();
        return String.format( "%s %s", "#".repeat( hashCount ), getValue() );
    }
}
