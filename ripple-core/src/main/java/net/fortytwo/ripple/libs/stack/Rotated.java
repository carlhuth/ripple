/*
 * $URL$
 * $Revision$
 * $Author$
 *
 * Copyright (C) 2007-2008 Joshua Shinavier
 */


package net.fortytwo.ripple.libs.stack;

import net.fortytwo.ripple.RippleException;
import net.fortytwo.ripple.flow.Sink;
import net.fortytwo.ripple.model.PrimitiveStackMapping;
import net.fortytwo.ripple.model.RippleValue;
import net.fortytwo.ripple.model.StackContext;
import net.fortytwo.ripple.model.RippleList;

/**
 * A primitive which permutes the second, third and fourth items on the stack
 * such that (... x y z ...) becomes (... z y x ...).
 */
public class Rotated extends PrimitiveStackMapping
{
	private static final int ARITY = 4;

    private static final String[] IDENTIFIERS = {
            StackLibrary.NS_2008_06 + "rotated",
            StackLibrary.NS_2007_08 + "rotated",
            StackLibrary.NS_2007_05 + "rotated"};

    public String[] getIdentifiers()
    {
        return IDENTIFIERS;
    }

	public Rotated()
		throws RippleException
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
		RippleValue w, z, y, x;

		w = stack.getFirst();
		stack = stack.getRest();
		z = stack.getFirst();
		stack = stack.getRest();
		y = stack.getFirst();
		stack = stack.getRest();
		x = stack.getFirst();
		stack = stack.getRest();

		sink.put( arg.with(
				stack.push( z ).push( y ).push( x ).push( w ) ) );
	}
}

