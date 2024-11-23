package com.github.argsfxs.spica.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * This class represent a Gemtext preformatted element.<br/>
 * It contains the preformatted lines.
 */
public class GemtextPreElement implements GemtextElement<List<GemtextPreText>>
{
    private final List<GemtextPreText> lines = new ArrayList<>();
    
    private final String meta; //TODO: better name
    
    GemtextPreElement( String meta )
    {
        this.meta = meta.isBlank() ? null : meta;
    }
    
    void addLine( GemtextPreText line )
    {
        lines.add( line );
    }
    
    /**
     * Returns the meta information after the preformat marker (if present).
     *
     * @return the meta information
     */
    public Optional<String> getMeta()
    {
        return Optional.ofNullable( meta );
    }
    
    /**
     * Returns a list containing the preformatted lines.
     *
     * @return a list of the preformatted lines
     */
    @Override
    public List<GemtextPreText> getValue()
    {
        return Collections.unmodifiableList( lines );
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        lines.forEach( line -> {
            sb.append( line );
            sb.append( System.lineSeparator() );
        } );
        return sb.toString();
    }
}
