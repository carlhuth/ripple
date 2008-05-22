/*
 * $URL$
 * $Revision$
 * $Author$
 *
 * Copyright (C) 2007-2008 Joshua Shinavier
 */


package net.fortytwo.ripple.libs.logic;

import net.fortytwo.ripple.RippleException;
import net.fortytwo.ripple.libs.stack.StackLibrary;
import net.fortytwo.ripple.model.PrimitiveStackMapping;
import net.fortytwo.ripple.model.RippleValue;
import net.fortytwo.ripple.model.StackContext;
import net.fortytwo.ripple.model.RippleList;
import net.fortytwo.ripple.flow.Sink;

/**
 * A primitive which consumes two items and produces the first item.
 */
public class True extends PrimitiveStackMapping
{
	private static final int ARITY = 2;

    private static final String[] IDENTIFIERS = {
            LogicLibrary.NS_2008_06 + "true",
            StackLibrary.NS_2007_08 + "true",
            StackLibrary.NS_2007_05 + "true"};

    public String[] getIdentifiers()
    {
        return IDENTIFIERS;
    }
    
    public True() throws RippleException
	{
		super();
    }

	public int arity()
	{
		return ARITY;
	}

	public void applyTo( final StackContext arg,
						 final Sink<StackContext, RippleException> sink
	)
		throws RippleException
	{
		RippleList stack = arg.getStack();
		RippleValue x;

		x = stack.getFirst();
		stack = stack.getRest();
		stack = stack.getRest();

		sink.put( arg.with(
				stack.push( x ) ) );
	}
}

