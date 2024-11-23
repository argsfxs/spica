import com.github.argsfxs.spica.parser.GemtextDocument;
import com.github.argsfxs.spica.parser.GemtextParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Main
{
    public static void main( String[] args ) throws FileNotFoundException
    {
        GemtextParser parser = new GemtextParser();
        InputStream in = new FileInputStream( "resources/gemtext.gmi" );
        GemtextDocument doc = parser.parse( in );
        System.out.println( doc );
    }
}