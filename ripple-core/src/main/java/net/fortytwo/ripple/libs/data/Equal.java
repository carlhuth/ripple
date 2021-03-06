package net.fortytwo.ripple.libs.data;

import net.fortytwo.flow.Sink;
import net.fortytwo.ripple.RippleException;
import net.fortytwo.ripple.libs.graph.GraphLibrary;
import net.fortytwo.ripple.model.ModelConnection;
import net.fortytwo.ripple.model.PrimitiveStackMapping;
import net.fortytwo.ripple.model.RippleList;

/**
 * A primitive which consumes two items and produces a Boolean value of true if
 * they are equal according to their data types, otherwise false.
 *
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public class Equal extends PrimitiveStackMapping {
    public String[] getIdentifiers() {
        return new String[]{
                DataLibrary.NS_2013_03 + "equal",
                GraphLibrary.NS_2008_08 + "equal",
                GraphLibrary.NS_2007_08 + "equal",
                GraphLibrary.NS_2007_05 + "equal"};
    }

    public Equal() {
        super();
    }

    public Parameter[] getParameters() {
        return new Parameter[]{
                new Parameter("x", null, true),
                new Parameter("y", null, true)};
    }

    public String getComment() {
        return "x y  =>  b  -- where b is true if x and y are equal, otherwise false";
    }

    public void apply(final RippleList arg,
                      final Sink<RippleList> solutions,
                      final ModelConnection mc) throws RippleException {

        RippleList stack = arg;

        Object a, b, result;

        a = stack.getFirst();
        stack = stack.getRest();
        b = stack.getFirst();
        stack = stack.getRest();

        // Note: equals() is not suitable for this operation (for instance,
        //       it may yield false for RdfValues containing identical
        //       Literals).
        result = 0 == mc.getComparator().compare(a, b);

        solutions.accept(stack.push(result));
    }
}
