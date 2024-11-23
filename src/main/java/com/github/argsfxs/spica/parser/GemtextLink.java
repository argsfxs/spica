package com.github.argsfxs.spica.parser;

import java.net.URI;
import java.util.Optional;

/**
 * This class represents a Gemtext link element.
 */
public class GemtextLink implements GemtextElement<URI>
{
    private final URI uri;
    
    private final String name;
    
    GemtextLink( URI uri, String name )
    {
        this.uri = uri;
        this.name = name;
    }
    
    /**
     * Returns the URI target of the link.
     *
     * @return the URI target
     */
    @Override
    public URI getValue()
    {
        return uri;
    }
    
    /**
     * Returns the link name, which is either the user-friendly name (if present) or the target.
     *
     * @return the link name
     */
    public String getName()
    {
        return getUserFriendlyName().isPresent() ? getUserFriendlyName().get() :
            getValue().toString();
    }
    
    /**
     * Returns the user-friendly link name (if present).
     *
     * @return the user-friendly link name
     */
    public Optional<String> getUserFriendlyName()
    {
        return Optional.ofNullable( name );
    }
    
    @Override
    public String toString()
    {
        return String.format( "=> %s%s", getValue().toString(),
            ( getUserFriendlyName().isPresent() ?
                String.format( " %s", getUserFriendlyName().get() ) : "" ) );
    }
}
