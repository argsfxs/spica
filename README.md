# Spica

A Java [Gemtext](https://geminiprotocol.net/docs/gemtext-specification.gmi) parser.

## Usage

~~~java
GemtextParser parser = new GemtextParser();
InputStream in = new FileInputStream( "resources/gemtext.gmi" );
GemtextDocument doc = parser.parse( in );
System.out.println( doc );
~~~