package com.github.argsfxs.spica.parser;

import com.github.argsfxs.spica.exception.GemtextDocumentException;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentTest
{
    @Test
    public void test() throws FileNotFoundException
    {
        GemtextParser parser = new GemtextParser();
        InputStream in = new FileInputStream( "resources/gemtext.gmi" );
        GemtextDocument doc = parser.parse( in );
        List<GemtextElement<?>> elements = doc.getElements();
        assertEquals( 152, elements.size() );
        
        GemtextElement<?> first = doc.getElement( 0 );
        assertInstanceOf( GemtextHeading.class, first );
        assertEquals( "Gemini hypertext format, aka \"gemtext\", specification", first.getValue() );
        
        GemtextDocumentException gde = assertThrows( GemtextDocumentException.class,
            () -> doc.getElement( 152 ) );
        assertEquals( "Error accessing element: index out of bounds.", gde.getMessage() );
        
        Iterator<GemtextElement<?>> it = doc.iterator();
        assertTrue( it.hasNext() );
        assertEquals( first, it.next() );
    }
}
