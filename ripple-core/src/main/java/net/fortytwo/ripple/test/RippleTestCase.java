package net.fortytwo.ripple.test;

import junit.framework.AssertionFailedError;
import net.fortytwo.flow.Collector;
import net.fortytwo.ripple.Ripple;
import net.fortytwo.ripple.RippleException;
import net.fortytwo.ripple.URIMap;
import net.fortytwo.ripple.model.Model;
import net.fortytwo.ripple.model.ModelConnection;
import net.fortytwo.ripple.model.RippleList;
import net.fortytwo.ripple.model.impl.sesame.SesameModel;
import net.fortytwo.ripple.query.LazyEvaluatingIterator;
import net.fortytwo.ripple.query.QueryEngine;
import net.fortytwo.ripple.query.QueryPipe;
import net.fortytwo.ripple.query.StackEvaluator;
import org.junit.After;
import org.junit.Before;
import org.openrdf.model.IRI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;
import org.openrdf.sail.memory.MemoryStore;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public abstract class RippleTestCase {
    // TODO: add a shutdown hook to clean up these objects
    private static Sail sail = null;
    private static URIMap uriMap = null;
    private static Model model = null;
    private static QueryEngine queryEngine = null;

    // The only three possible values of analysis:compare.
    private static final double
            LT = -1.0,
            GT = 1.0,
            EQ = 0.0;

    protected static final double ASSERTEQUALS_EPSILON = 0.001;

    protected ModelConnection modelConnection = null;
    private Comparator<Object> comparator = null;

    @Before
    public void setUp() throws Exception {
        Ripple.initialize();

        modelConnection = getTestModel().createConnection();
        comparator = modelConnection.getComparator();

        SailConnection sc = getTestSail().getConnection();

        try {
            sc.begin();
            sc.clear();
            sc.commit();
        } finally {
            sc.close();
        }
    }

    @After
    public void tearDown() throws Exception {
        if (null != modelConnection) {
            modelConnection.close();
            modelConnection = null;
        }
    }

    protected Sail getTestSail() throws RippleException {
        if (null == sail) {
            sail = new MemoryStore();

            try {
                sail.initialize();

                SailConnection sc = sail.getConnection();
                try {
                    sc.begin();
                    // Define some common namespaces
                    sc.setNamespace("rdf", RDF.NAMESPACE);
                    sc.setNamespace("rdfs", RDFS.NAMESPACE);
                    sc.setNamespace("xsd", XMLSchema.NAMESPACE);
                    sc.commit();
                } finally {
                    sc.close();
                }
            } catch (SailException e) {
                throw new RippleException(e);
            }
        }

        return sail;
    }

    protected URIMap getTestURIMap() {
        if (null == uriMap) {
            uriMap = new URIMap();
        }

        return uriMap;
    }

    protected Model getTestModel() throws RippleException {
        if (null == model) {
            Ripple.initialize();

            // Asynchronous queries can cause certain tests cases to fail, as
            // they are not set up to wait on other threads.
            Ripple.enableAsynchronousQueries(false);

            model = new SesameModel(getTestSail());
        }

        return model;
    }

    protected QueryEngine getTestQueryEngine() throws RippleException {
        if (null == queryEngine) {
            StackEvaluator eval = new LazyEvaluatingIterator.WrappingEvaluator();
            //StackEvaluator eval = new LazyStackEvaluator();
            queryEngine = new QueryEngine(getTestModel(), eval, System.out, System.err);
        }

        return queryEngine;
    }

    protected RippleList createStack(final ModelConnection mc,
                                     final Object... values) throws RippleException {
        if (0 == values.length) {
            return mc.list();
        }

        RippleList l = mc.list().push(values[0]);
        for (int i = 1; i < values.length; i++) {
            l = l.push(values[i]);
        }

        return l;
    }

    protected RippleList createQueue(final ModelConnection mc,
                                     final Object... values) throws RippleException {
        return createStack(mc, values).invert();
    }

    protected void assertCollectorsEqual(final Collector<RippleList> expected,
                                         final Collector<RippleList> actual) throws Exception {
        int size = expected.size();

        assertEquals("wrong number of results.", size, actual.size());
        if (0 == size) {
            return;
        }
//for (RippleList l : expected) {System.out.println("expected: " + l);}
//for (RippleList l : actual) {System.out.println("actual: " + l);}

        // Sort the results.
        RippleList[] expArray = new RippleList[size];
        RippleList[] actArray = new RippleList[size];
        Iterator<RippleList> expIter = expected.iterator();
        Iterator<RippleList> actIter = actual.iterator();
        for (int i = 0; i < size; i++) {
            expArray[i] = expIter.next();
            actArray[i] = actIter.next();
        }
        Arrays.sort(expArray, comparator);
        Arrays.sort(actArray, comparator);

        // Compare the results by pairs.
        for (int i = 0; i < size; i++) {
            assertRippleEquals(expArray[i], actArray[i]);
        }
    }

    protected void assertRippleEquals(final Object first, final Object second) {
        int cmp = comparator.compare(first, second);
        if (0 != cmp) {
            throw new AssertionFailedError("expected <" + first + "> but was <" + second + ">");
        }
    }

    protected Collection<RippleList> reduce(final InputStream from) throws RippleException {
        Collector<RippleList>
                results = new Collector<>();

        QueryEngine qe = getTestQueryEngine();

        QueryPipe actualPipe = new QueryPipe(qe, results);
        actualPipe.put(from);
        actualPipe.close();

        Collection<RippleList> c = results.stream().collect(Collectors.toCollection(() -> new LinkedList<>()));

        return c;
    }

    protected void assertLegal(final String from) throws Exception {
        Collection<RippleList> result = reduce("(" + from + ")");
        assertTrue("expression is illegal: " + from, 1 == result.size());
    }

    protected void assertIllegal(final String from) throws Exception {
        Collection<RippleList> result = reduce("(" + from + ")");
        assertTrue("expression is legal: " + from, 0 == result.size());
    }

    protected Collection<RippleList> reduce(final String from) throws RippleException {
        Collector<RippleList>
                results = new Collector<>();

        QueryEngine qe = getTestQueryEngine();

        QueryPipe actualPipe = new QueryPipe(qe, results);
        actualPipe.accept(from + "\n");
        actualPipe.close();

        Collection<RippleList> c = results.stream().collect(Collectors.toCollection(() -> new LinkedList<>()));

        return c;
    }

    protected void assertReducesTo(final String from, final String... to) throws Exception {
        Collector<RippleList>
                expected = new Collector<>(),
                actual = new Collector<>();

        QueryEngine qe = getTestQueryEngine();

        QueryPipe actualPipe = new QueryPipe(qe, actual);
        actualPipe.accept(from + "\n");
        actualPipe.close();

        QueryPipe expectedPipe = new QueryPipe(qe, expected);
        for (String t : to) {
            expectedPipe.accept(t + "\n");
        }
        expectedPipe.close();

        assertCollectorsEqual(expected, actual);
    }

    protected IRI createIRI(final String s,
                            final ModelConnection mc) throws RippleException {
        return mc.valueOf(java.net.URI.create(s));
    }

    private double compare(final String expr1, final String expr2) throws Exception {
        Collection<RippleList> results = reduce(expr1 + " " + expr2 + " compare.");
        assertEquals(1, results.size());
        RippleList l = results.iterator().next();
        assertEquals(1, l.length());
        Object v = l.getFirst();
        assertTrue(v instanceof Number);
        return ((Number) v).doubleValue();
    }

    protected void assertLt(final String expr1, final String expr2) throws Exception {
        assertEquals(LT, compare(expr1, expr2), ASSERTEQUALS_EPSILON);
    }

    protected void assertGt(final String expr1, final String expr2) throws Exception {
        assertEquals(GT, compare(expr1, expr2), ASSERTEQUALS_EPSILON);
    }

    protected void assertEq(final String expr1, final String expr2) throws Exception {
        assertEquals(EQ, compare(expr1, expr2), ASSERTEQUALS_EPSILON);
    }
}
