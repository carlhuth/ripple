package net.fortytwo.ripple.libs.stack;

import net.fortytwo.flow.Sink;
import net.fortytwo.ripple.RippleException;
import net.fortytwo.ripple.model.ModelConnection;
import net.fortytwo.ripple.model.PrimitiveStackMapping;
import net.fortytwo.ripple.model.RippleList;

/**
 * A primitive which consumes a list an an item, prepends the item to the list,
 * and produces the resulting list.
 *
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public class Swons extends PrimitiveStackMapping {
    private static final String[] IDENTIFIERS = {
            StackLibrary.NS_2013_03 + "swons",
            StackLibrary.NS_2008_08 + "swons",
            StackLibrary.NS_2007_08 + "swons",
            StackLibrary.NS_2007_05 + "swons"};

    public String[] getIdentifiers() {
        return IDENTIFIERS;
    }

    public Swons() {
        super();
    }

    public Parameter[] getParameters() {
        return new Parameter[]{
                new Parameter("l", "a list", true),
                new Parameter("x", null, true)};
    }

    public String getComment() {
        return "l x  =>  l2  -- where the first member of l2 is x and the rest of l2 is l";
    }

    public void apply(final RippleList arg,
                      final Sink<RippleList> solutions,
                      final ModelConnection mc) throws RippleException {
        RippleList stack = arg;

        Object l;

        final Object x = stack.getFirst();
        stack = stack.getRest();
        l = stack.getFirst();
        final RippleList rest = stack.getRest();

        Sink<RippleList> listSink = list -> solutions.accept(
                rest.push(list.push(x)));

        mc.toList(l, listSink);
    }
}

