package com.github.argsfxs.spica.parser;

import com.github.argsfxs.spica.exception.GemtextDocumentException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a Gemtext document.
 */
public class GemtextDocument implements Iterable<GemtextElement<?>>
{
    private final List<GemtextElement<?>> elements = new ArrayList<>();
    
    GemtextDocument() { }
    
    void addElement( GemtextElement<?> element )
    {
        elements.add( element );
    }
    
    //only called in parser to get pre block
    GemtextElement<?> getLast()
    {
        return elements.get( elements.size() - 1 );
    }
    
    /**
     * Returns the element at the specified index.
     *
     * @param index the index of the element
     * @return the element or an {@link GemtextDocumentException} if the index is invalid
     */
    public GemtextElement<?> getElement( int index )
    {
        try
        {
            return elements.get( index );
        }
        catch ( IndexOutOfBoundsException e )
        {
            throw new GemtextDocumentException( "Error accessing element: index out of bounds." );
        }
    }
    
    /**
     * Returns a list of all elements in this document.
     *
     * @return the list of elements
     */
    public List<GemtextElement<?>> getElements()
    {
        return Collections.unmodifiableList( elements );
    }
    
    @Override
    public Iterator<GemtextElement<?>> iterator()
    {
        return getElements().iterator();
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        elements.forEach( element -> {
            sb.append( element );
            sb.append( System.lineSeparator() );
        } );
        return sb.toString();
    }
}
