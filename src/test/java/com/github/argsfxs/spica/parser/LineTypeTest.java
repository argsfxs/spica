package com.github.argsfxs.spica.parser;

import org.junit.jupiter.api.Test;

import static com.github.argsfxs.spica.types.LineType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LineTypeTest
{
    @Test
    public void test()
    {
        GemtextParser parser = new GemtextParser();
        
        assertEquals( TEXT, parser.readLineType( "foo" ) );
        assertEquals( LIST_ITEM, parser.readLineType( "*" ) );
        assertEquals( LIST_ITEM, parser.readLineType( "*foo" ) );
        assertEquals( LIST_ITEM, parser.readLineType( "**foo" ) );
        assertEquals( LIST_ITEM, parser.readLineType( "* foo" ) );
        assertEquals( TEXT, parser.readLineType( " * foo" ) );
        assertEquals( LIST_ITEM, parser.readLineType( "*     foo" ) );
        assertEquals( LIST_ITEM, parser.readLineType( "* *foo" ) );
        assertEquals( LIST_ITEM, parser.readLineType( "* * foo" ) );
        assertEquals( LIST_ITEM, parser.readLineType( "** foo" ) );
        assertEquals( LINK, parser.readLineType( "=> foo" ) );
        assertEquals( LINK, parser.readLineType( "=> foo bar" ) );
        assertEquals( LINK, parser.readLineType( "=>foo bar" ) );
        assertEquals( TEXT, parser.readLineType( "   =>foo bar" ) );
        assertEquals( HEADING, parser.readLineType( "#" ) );
        assertEquals( HEADING, parser.readLineType( "#foo" ) );
        assertEquals( HEADING, parser.readLineType( "##" ) );
        assertEquals( HEADING, parser.readLineType( "###" ) );
        assertEquals( HEADING, parser.readLineType( "### foo" ) );
        assertEquals( TEXT, parser.readLineType( " ### foo" ) );
        assertEquals( QUOTE, parser.readLineType( ">" ) );
        assertEquals( QUOTE, parser.readLineType( "> foo" ) );
        assertEquals( QUOTE, parser.readLineType( ">foo" ) );
        assertEquals( TEXT, parser.readLineType( " >foo" ) );
        assertEquals( PREFORMAT_TOGGLE, parser.readLineType( "```" ) );
        assertEquals( PREFORMAT_TOGGLE, parser.readLineType( "```foo" ) );
        assertEquals( PREFORMAT_TOGGLE, parser.readLineType( "``` foo" ) );
        assertEquals( PREFORMAT_TOGGLE, parser.readLineType( "````" ) );
        assertEquals( TEXT, parser.readLineType( "``" ) );
        assertEquals( TEXT, parser.readLineType( "`" ) );
        assertEquals( TEXT, parser.readLineType( "   ```" ) );
    }
}
