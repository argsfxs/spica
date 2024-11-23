package com.github.argsfxs.spica.parser;

class LineTypePattern
{
    static final String LINK_PATTERN = "^=>[\\s]*(\\S+)([\\s]+.*)?$";
    
    static final String HEADING_PATTERN = "^(#{1,3})(.*)$";
    
    static final String LIST_ITEM_PATTERN = "^\\*\\x20(.*)$";
    
    static final String QUOTE_PATTERN = "^>(.*)$";
    
    static final String PREFORMAT_TOGGLE_PATTERN = "^```(.*)$";
}
