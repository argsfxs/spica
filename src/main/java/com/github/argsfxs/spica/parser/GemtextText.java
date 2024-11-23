package com.github.argsfxs.spica.parser;

/**
 * This class represents a Gemtext text element.
 */
public class GemtextText implements GemtextElement<String>
{
    private final String text;
    
    GemtextText( String text )
    {
        this.text = text;
    }
    
    /**
     * Returns the value of this element.
     *
     * @return the value of the element
     */
    @Override
    public String getValue()
    {
        return text;
    }
    
    @Override
    public String toString()
    {
        return getValue();
    }
}
