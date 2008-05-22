/*
 * $URL$
 * $Revision$
 * $Author$
 *
 * Copyright (C) 2007-2008 Joshua Shinavier
 */


package net.fortytwo.ripple.libs.string;

import net.fortytwo.ripple.RippleException;
import net.fortytwo.ripple.model.Library;
import net.fortytwo.ripple.model.LibraryLoader;
import net.fortytwo.ripple.URIMap;

/**
 * A collection of string manipulation primitives.
 */
public class StringLibrary extends Library
{
    public static final String
            NS_2008_06 = "http://fortytwo.net/2008/06/ripple/string#",
            NS_2007_08 = "http://fortytwo.net/2007/08/ripple/string#";

    public void load( final URIMap uf,
                      final LibraryLoader.LibraryLoaderContext context )
		throws RippleException
	{
		uf.put(
			NS_2008_06, getClass().getResource( "string.ttl" ) + "#" );

		registerPrimitive( EndsWith.class, context );
		registerPrimitive( IndexOf.class, context );
		registerPrimitive( LastIndexOf.class, context );
		registerPrimitive( Length.class, context );
        registerPrimitive( Matches.class, context );
        registerPrimitive( Md5.class, context );
		registerPrimitive( PercentDecode.class, context );
		registerPrimitive( PercentEncode.class, context );
		registerPrimitive( ReplaceAll.class, context );
		registerPrimitive( Sha1.class, context );
		registerPrimitive( Split.class, context );
		registerPrimitive( StartsWith.class, context );
		registerPrimitive( StrCat.class, context );
		registerPrimitive( Substring.class, context );
		registerPrimitive( ToLowerCase.class, context );
		registerPrimitive( ToUpperCase.class, context );
		registerPrimitive( Trim.class, context );
		registerPrimitive( UrlDecode.class, context );
		registerPrimitive( UrlEncode.class, context );
	}
}

