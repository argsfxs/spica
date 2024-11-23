package com.github.argsfxs.spica.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineTypeMatcher
{
    public static Matcher getMatcher( String regex, String line )
    {
        Pattern pattern = Pattern.compile( regex );
        return pattern.matcher( line );
    }
}
