package net.fortytwo.flow;

import net.fortytwo.ripple.RippleException;

/**
 * A "tee" pipeline which passes data to both of two downstream sinks.
 *
 * @param <T> the type of data being passed
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public class Tee<T> implements Sink<T> {
    private final Sink<T> left, right;

    /**
     * Constructs a new tee using the given sinks
     *
     * @param left  one of the two downstream sinks
     * @param right the other of the two downstream sinks
     */
    public Tee(final Sink<T> left, final Sink<T> right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Receives the next data item
     *
     * @param t the data item being passed
     * @throws RippleException if a data handling error occurs
     */
    public void accept(final T t) throws RippleException {
        left.accept(t);
        right.accept(t);
    }
}
