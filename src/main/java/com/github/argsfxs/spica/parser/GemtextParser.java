package com.github.argsfxs.spica.parser;

import com.github.argsfxs.spica.exception.GemtextInternalException;
import com.github.argsfxs.spica.exception.GemtextParseException;
import com.github.argsfxs.spica.types.HeadingLevel;
import com.github.argsfxs.spica.types.LineType;
import com.github.argsfxs.spica.types.ParserMode;
import com.github.argsfxs.spica.types.ParserState;
import com.github.argsfxs.spica.util.LineTypeMatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.regex.Matcher;

import static com.github.argsfxs.spica.parser.LineTypePattern.*;
import static com.github.argsfxs.spica.types.LineType.*;
import static com.github.argsfxs.spica.types.ParserMode.LENIENT;
import static com.github.argsfxs.spica.types.ParserMode.NORMAL;
import static com.github.argsfxs.spica.types.ParserState.NORMAL_STATE;
import static com.github.argsfxs.spica.types.ParserState.PRE_FORMATTED_STATE;

/**
 * This class represents a parser for Gemtext documents.
 */
public class GemtextParser
{
    private final ParserMode mode;
    
    private int lineNumber = 1;
    
    private ParserState state = NORMAL_STATE;
    
    /**
     * Creates a new parser instance.
     */
    public GemtextParser()
    {
        this( NORMAL );
    }
    
    /**
     * Creates a new parser instance with the provided parsing mode.
     *
     * @param mode the parsing mode
     */
    public GemtextParser( ParserMode mode )
    {
        this.mode = mode;
    }
    
    /**
     * Attempts to parse the input stream.
     *
     * @param in the input stream of the Gemtext data
     * @return the created document
     */
    public GemtextDocument parse( InputStream in )
    {
        GemtextDocument doc = new GemtextDocument();
        
        try ( BufferedReader br = new BufferedReader( new InputStreamReader( in ) ) )
        {
            String line;
            while ( ( line = br.readLine() ) != null )
            {
                Optional<GemtextElement<?>> element = processLine( line );
                if ( element.isPresent() )
                {
                    GemtextElement<?> value = element.get();
                    if ( value instanceof GemtextPreText )
                    {
                        processPreLine( ( GemtextPreText ) value, doc.getLast() );
                    }
                    else
                    {
                        doc.addElement( value );
                    }
                }
                lineNumber++;
            }
        }
        catch ( IOException e )
        {
            throw new GemtextInternalException( String.format( "I/O error while reading input " +
                "(%s).", e.getMessage() ) );
        }
        
        return doc;
    }
    
    private void processPreLine( GemtextPreText value, GemtextElement<?> preElement )
    {
        if ( !( preElement instanceof GemtextPreElement ) )
        {
            throw new GemtextInternalException( String.format( "Internal error parsing line %d: " +
                "can't assign preformatted line to parent.", lineNumber ) );
        }
        ( ( GemtextPreElement ) preElement ).addLine( value );
    }
    
    
    Optional<GemtextElement<?>> processLine( String line )
    {
        LineType type;
        if ( state == NORMAL_STATE )
        {
            type = readLineType( line );
            if ( type == PREFORMAT_TOGGLE )
            {
                state = PRE_FORMATTED_STATE;
            }
            return Optional.of( parseElement( line, type ) );
        }
        else
        {
            type = readLineTypePre( line );
            if ( type == PREFORMAT_TOGGLE )
            {
                state = NORMAL_STATE;
            }
            else
            {
                return Optional.of( parsePreText( line ) );
            }
        }
        return Optional.empty();
    }
    
    LineType readLineType( String line )
    {
        // string is empty, treat as text
        if ( line == null || line.isEmpty() )
        {
            return TEXT;
        }
        int length = line.length();
        // read first character
        char first = line.charAt( 0 );
        switch ( first )
        {
            case '=':
            {
                if ( length >= 2 && line.charAt( 1 ) == '>' ) return LINK;
                break;
            }
            case '#':
                return HEADING;
            case '*':
                return LIST_ITEM;
            case '>':
                return QUOTE;
            case '`':
            {
                if ( length >= 3 && line.charAt( 1 ) == '`' && line.charAt( 2 ) == '`' )
                    return PREFORMAT_TOGGLE;
                break;
            }
        }
        // no type identifier found, treat as text
        return TEXT;
    }
    
    LineType readLineTypePre( String line )
    {
        if ( line != null && line.length() >= 3 && line.charAt( 0 ) == '`' && line.charAt( 1 ) == '`' && line.charAt( 2 ) == '`' )
        {
            return PREFORMAT_TOGGLE;
        }
        return TEXT;
    }
    
    private GemtextElement<?> parseElement( String line, LineType type )
    {
        try
        {
            switch ( type )
            {
                case TEXT:
                    return parseText( line );
                case LINK:
                    return parseLink( line );
                case HEADING:
                    return parseHeading( line );
                case LIST_ITEM:
                    return parseListItem( line );
                case QUOTE:
                    return parseQuote( line );
                case PREFORMAT_TOGGLE:
                    return parsePreElement( line );
            }
        }
        catch ( GemtextParseException gpe )
        {
            if ( mode != LENIENT )
            {
                throw gpe;
            }
        }
        return parseText( line );
    }
    
    private GemtextText parseText( String line )
    {
        return new GemtextText( line );
    }
    
    private GemtextLink parseLink( String line )
    {
        Matcher matcher = LineTypeMatcher.getMatcher( LINK_PATTERN, line );
        if ( matcher.find() )
        {
            URI uri;
            try
            {
                uri = new URI( matcher.group( 1 ) );
            }
            catch ( URISyntaxException e )
            {
                throw new GemtextParseException( String.format( "Error parsing line %d: link with" +
                    " invalid URI syntax (%s).", lineNumber, e.getMessage() ) );
            }
            return new GemtextLink( uri, trim( matcher.group( 2 ) ) );
        }
        else
        {
            throw new GemtextParseException( String.format( "Error parsing line %d: link with " +
                "invalid syntax.", lineNumber ) );
        }
    }
    
    private GemtextHeading parseHeading( String line )
    {
        Matcher matcher = LineTypeMatcher.getMatcher( HEADING_PATTERN, line );
        if ( matcher.find() )
        {
            String hashes = matcher.group( 1 );
            int hashCount = hashes.length();
            Optional<HeadingLevel> level = HeadingLevel.fromHashCount( hashCount );
            if ( level.isEmpty() )
            {
                throw new GemtextParseException( String.format( "Error parsing line %d: invalid " +
                    "heading level.", lineNumber ) );
            }
            return new GemtextHeading( trim( matcher.group( 2 ) ), level.get() );
        }
        else
        {
            throw new GemtextParseException( String.format( "Error parsing line %d: heading with " +
                "invalid syntax.", lineNumber ) );
        }
    }
    
    private GemtextListItem parseListItem( String line )
    {
        Matcher matcher = LineTypeMatcher.getMatcher( LIST_ITEM_PATTERN, line );
        if ( matcher.find() )
        {
            return new GemtextListItem( trim( matcher.group( 1 ) ) );
        }
        else
        {
            throw new GemtextParseException( String.format( "Error parsing line %d: list item " +
                "with invalid syntax.", lineNumber ) );
        }
    }
    
    private GemtextQuote parseQuote( String line )
    {
        Matcher matcher = LineTypeMatcher.getMatcher( QUOTE_PATTERN, line );
        if ( matcher.find() )
        {
            return new GemtextQuote( trim( matcher.group( 1 ) ) );
        }
        else
        {
            throw new GemtextParseException( String.format( "Error parsing line %d: quote with " +
                "invalid syntax.", lineNumber ) );
        }
    }
    
    private GemtextPreElement parsePreElement( String line )
    {
        Matcher matcher = LineTypeMatcher.getMatcher( PREFORMAT_TOGGLE_PATTERN, line );
        if ( matcher.find() )
        {
            return new GemtextPreElement( trim( matcher.group( 1 ) ) );
        }
        else
        {
            throw new GemtextParseException( String.format( "Error parsing line %d: " +
                "preformat-toggle line with invalid syntax.", lineNumber ) );
        }
    }
    
    private GemtextPreText parsePreText( String line )
    {
        return new GemtextPreText( line );
    }
    
    private String trim( String input )
    {
        if ( input == null || input.isBlank() )
        {
            return input;
        }
        return input.trim();
    }
}
