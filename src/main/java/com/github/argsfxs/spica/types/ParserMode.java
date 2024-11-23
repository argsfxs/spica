package com.github.argsfxs.spica.types;

public enum ParserMode
{
    /**
     * Treats all lines with parsing errors as text lines.
     */
    LENIENT,
    /**
     * Throws an exception if the line causes parsing errors.
     */
    NORMAL
}
