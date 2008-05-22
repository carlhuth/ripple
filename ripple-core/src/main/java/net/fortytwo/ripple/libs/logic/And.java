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
import net.fortytwo.ripple.flow.Sink;
import net.fortytwo.ripple.model.PrimitiveStackMapping;
import net.fortytwo.ripple.model.RippleList;
import net.fortytwo.ripple.model.RippleValue;
import net.fortytwo.ripple.model.StackContext;

/**
 * A primitive which consumes two Boolean values and produces the result of
 * their logical conjunction.
 */
public class And extends PrimitiveStackMapping
{
    private static final int ARITY = 2;
    
    private static final String[] IDENTIFIERS = {
            LogicLibrary.NS_2008_06 + "and",
            StackLibrary.NS_2007_08 + "and",
            StackLibrary.NS_2007_05 + "and"};

    public And() throws RippleException
	{
		super();
	}

	public int arity()
	{
		return ARITY;
	}

    public String[] getIdentifiers()
    {
        return IDENTIFIERS;
    }

    public void applyTo( final StackContext arg,
						 final Sink<StackContext, RippleException> sink ) throws RippleException
	{
		RippleList stack = arg.getStack();

		RippleValue x, y;

		x = stack.getFirst();
		stack = stack.getRest();
		y = stack.getFirst();
		stack = stack.getRest();

		RippleValue trueValue = LogicLibrary.getTrueValue();

		// Note: everything apart from joy:true is considered false.
		RippleValue result = ( 0 == x.compareTo( trueValue ) && 0 == y.compareTo( trueValue ) )
			? trueValue
			: LogicLibrary.getFalseValue();

		sink.put( arg.with(
				stack.push( result ) ) );
	}
}

