package com.github.argsfxs.spica.parser;

import com.github.argsfxs.spica.exception.GemtextParseException;
import com.github.argsfxs.spica.types.ParserMode;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Collections;

import static com.github.argsfxs.spica.types.HeadingLevel.*;
import static org.junit.jupiter.api.Assertions.*;

// TODO: idee für test hier bzw. STRICT mode: no leading whitespace erlaubt! siehe spec
public class LineProcessingTest
{
    private final GemtextParser parser = new GemtextParser();
    
    private final GemtextParser lenient = new GemtextParser( ParserMode.LENIENT );
    
    @Test
    public void testText_Success()
    {
        String line = "foo bar baz";
        GemtextElement<?> element = parser.processLine( line ).get();
        assertInstanceOf( GemtextText.class, element );
        assertEquals( line, element.getValue() );
        assertEquals( line, element.toString() );
        
        line = "";
        element = lenient.processLine( line ).get();
        assertInstanceOf( GemtextText.class, element );
        assertEquals( line, element.getValue() );
        assertEquals( line, element.toString() );
        
        line = "    ";
        element = lenient.processLine( line ).get();
        assertInstanceOf( GemtextText.class, element );
        assertEquals( line, element.getValue() );
        assertEquals( line, element.toString() );
        
        line = null;
        element = lenient.processLine( line ).get();
        assertInstanceOf( GemtextText.class, element );
        assertEquals( line, element.getValue() );
        assertEquals( line, element.toString() );
        
        line = "*list item 1";
        element = lenient.processLine( line ).get();
        assertInstanceOf( GemtextText.class, element );
        assertEquals( line, element.getValue() );
        assertEquals( line, element.toString() );
    }
    
    @Test
    public void testLink_Success()
    {
        String line = "=> http://www.example.com/";
        GemtextElement<?> element = parser.processLine( line ).get();
        assertInstanceOf( GemtextLink.class, element );
        URI uri = ( URI ) element.getValue();
        assertEquals( "http://www.example.com/", uri.toString() );
        assertFalse( ( ( GemtextLink ) element ).getUserFriendlyName().isPresent() );
        assertEquals( "http://www.example.com/", ( ( GemtextLink ) element ).getName() );
        assertEquals( line, element.toString() );
        
        line = "=>http://www.example.com/";
        element = parser.processLine( line ).get();
        assertInstanceOf( GemtextLink.class, element );
        uri = ( URI ) element.getValue();
        assertEquals( "http://www.example.com/", uri.toString() );
        assertFalse( ( ( GemtextLink ) element ).getUserFriendlyName().isPresent() );
        assertEquals( "http://www.example.com/", ( ( GemtextLink ) element ).getName() );
        assertEquals( "=> http://www.example.com/", element.toString() );
        
        line = "=>  http://www.example.com/ Example site";
        element = parser.processLine( line ).get();
        assertInstanceOf( GemtextLink.class, element );
        uri = ( URI ) element.getValue();
        assertEquals( "http://www.example.com/", uri.toString() );
        assertEquals( "Example site", ( ( GemtextLink ) element ).getUserFriendlyName().get() );
        assertEquals( "Example site", ( ( GemtextLink ) element ).getName() );
        assertEquals( "=> http://www.example.com/ Example site", element.toString() );
        
        line = "=> /relative/link";
        element = parser.processLine( line ).get();
        assertInstanceOf( GemtextLink.class, element );
        uri = ( URI ) element.getValue();
        assertEquals( "/relative/link", uri.toString() );
        assertFalse( ( ( GemtextLink ) element ).getUserFriendlyName().isPresent() );
        assertEquals( "/relative/link", ( ( GemtextLink ) element ).getName() );
        assertEquals( "=> /relative/link", element.toString() );
        
        line = "=>/relative/link    Example link";
        element = parser.processLine( line ).get();
        assertInstanceOf( GemtextLink.class, element );
        uri = ( URI ) element.getValue();
        assertEquals( "/relative/link", uri.toString() );
        assertEquals( "Example link", ( ( GemtextLink ) element ).getUserFriendlyName().get() );
        assertEquals( "Example link", ( ( GemtextLink ) element ).getName() );
        assertEquals( "=> /relative/link Example link", element.toString() );
    }
    
    @Test
    public void testLink_Fail()
    {
        String line = "=> http;\\www.example.com\\";
        GemtextParseException gpe =
            assertThrows( GemtextParseException.class,
                () -> parser.processLine( line ) );
        assertEquals( "Error parsing line 1: link with invalid URI syntax (Illegal character in " +
            "path at index 5: http;\\www.example.com\\).", gpe.getMessage() );
    }
    
    @Test
    public void testHeading_Success()
    {
        String line = "#    First heading";
        GemtextElement<?> element = parser.processLine( line ).get();
        assertInstanceOf( GemtextHeading.class, element );
        assertEquals( "First heading", element.getValue() );
        assertEquals( TOP_HEADING, ( ( GemtextHeading ) element ).getLevel() );
        assertEquals( "# First heading", element.toString() );
        
        line = "##Second heading";
        element = parser.processLine( line ).get();
        assertInstanceOf( GemtextHeading.class, element );
        assertEquals( "Second heading", element.getValue() );
        assertEquals( SUB_HEADING, ( ( GemtextHeading ) element ).getLevel() );
        assertEquals( "## Second heading", element.toString() );
        
        line = "### Third heading";
        element = parser.processLine( line ).get();
        assertInstanceOf( GemtextHeading.class, element );
        assertEquals( "Third heading", element.getValue() );
        assertEquals( SUB_SUB_HEADING, ( ( GemtextHeading ) element ).getLevel() );
        assertEquals( "### Third heading", element.toString() );
        
        line = "####First heading";
        element = parser.processLine( line ).get();
        assertInstanceOf( GemtextHeading.class, element );
        assertEquals( "#First heading", element.getValue() );
        assertEquals( SUB_SUB_HEADING, ( ( GemtextHeading ) element ).getLevel() );
        assertEquals( "### #First heading", element.toString() );
    }
    
    @Test
    public void testListItem_Success()
    {
        String line = "* list item 1";
        GemtextElement<?> element = parser.processLine( line ).get();
        assertInstanceOf( GemtextListItem.class, element );
        assertEquals( "list item 1", element.getValue() );
        assertEquals( line, element.toString() );
        
        line = "*    list item 2";
        element = parser.processLine( line ).get();
        assertInstanceOf( GemtextListItem.class, element );
        assertEquals( "list item 2", element.getValue() );
        assertEquals( "* list item 2", element.toString() );
        
        line = "*  list item 3";
        element = parser.processLine( line ).get();
        assertInstanceOf( GemtextListItem.class, element );
        assertEquals( "list item 3", element.getValue() );
        assertEquals( "* list item 3", element.toString() );
        
        line = "* *list item 4";
        element = parser.processLine( line ).get();
        assertInstanceOf( GemtextListItem.class, element );
        assertEquals( "*list item 4", element.getValue() );
        assertEquals( line, element.toString() );
    }
    
    @Test
    public void testListItem_Fail()
    {
        String line = "*list item 1";
        GemtextParseException gpe =
            assertThrows( GemtextParseException.class,
                () -> parser.processLine( line ) );
        assertEquals( "Error parsing line 1: list item with invalid syntax.", gpe.getMessage() );
    }
    
    @Test
    public void testQuote_Success()
    {
        String line = ">quote 1";
        GemtextElement<?> element = parser.processLine( line ).get();
        assertInstanceOf( GemtextQuote.class, element );
        assertEquals( "quote 1", element.getValue() );
        assertEquals( line, element.toString() );
        
        line = "> quote 2";
        element = parser.processLine( line ).get();
        assertInstanceOf( GemtextQuote.class, element );
        assertEquals( "quote 2", element.getValue() );
        assertEquals( ">quote 2", element.toString() );
        
        line = ">   quote 3";
        element = parser.processLine( line ).get();
        assertInstanceOf( GemtextQuote.class, element );
        assertEquals( "quote 3", element.getValue() );
        assertEquals( ">quote 3", element.toString() );
    }
    
    @Test
    public void testPre_Success()
    {
        String line = "```";
        GemtextElement<?> element = parser.processLine( line ).get();
        assertInstanceOf( GemtextPreElement.class, element );
        assertEquals( Collections.emptyList(), element.getValue() );
        assertFalse( ( ( GemtextPreElement ) element ).getMeta().isPresent() );
        assertEquals( "", element.toString() );
        // switch back to normal mode
        line = "```";
        parser.processLine( line );
        
        line = "```java";
        element = parser.processLine( line ).get();
        assertInstanceOf( GemtextPreElement.class, element );
        assertEquals( Collections.emptyList(), element.getValue() );
        assertEquals( "java", ( ( GemtextPreElement ) element ).getMeta().get() );
        assertEquals( "", element.toString() );
        // switch back to normal mode
        line = "```";
        parser.processLine( line );
        
        line = "``` java";
        element = parser.processLine( line ).get();
        assertInstanceOf( GemtextPreElement.class, element );
        assertEquals( Collections.emptyList(), element.getValue() );
        assertEquals( "java", ( ( GemtextPreElement ) element ).getMeta().get() );
        assertEquals( "", element.toString() );
        // switch back to normal mode
        line = "```";
        parser.processLine( line );
    }
    
    @Test
    public void testPreText_Success()
    {
        String line = "```";
        parser.processLine( line );
        line = "foo bar baz";
        GemtextElement<?> element = parser.processLine( line ).get();
        assertInstanceOf( GemtextPreText.class, element );
        assertEquals( line, element.getValue() );
        assertEquals( line, element.toString() );
        // switch back to normal mode
        line = "```";
        parser.processLine( line );
    }
    
}
