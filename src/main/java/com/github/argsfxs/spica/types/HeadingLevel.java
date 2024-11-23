package com.github.argsfxs.spica.types;

import java.util.Arrays;
import java.util.Optional;

public enum HeadingLevel
{
    /**
     * A top heading ("#").
     */
    TOP_HEADING( 1 ),
    /**
     * A sub heading ("##").
     */
    SUB_HEADING( 2 ),
    /**
     * A sub sub heading ("###").
     */
    SUB_SUB_HEADING( 3 );
    
    private final int hashCount;
    
    HeadingLevel( int hashCount )
    {
        this.hashCount = hashCount;
    }
    
    public static Optional<HeadingLevel> fromHashCount( int count )
    {
        return Arrays.stream( HeadingLevel.values() ).filter( value -> value.hashCount == count ).findAny();
    }
    
    public int getHashCount()
    {
        return hashCount;
    }
    
    
}
