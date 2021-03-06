package net.fortytwo.ripple.model.impl.sesame;

import net.fortytwo.ripple.RippleException;
import net.fortytwo.ripple.model.ModelConnection;
import net.fortytwo.ripple.test.RippleTestCase;
import org.junit.Test;
import org.openrdf.model.IRI;
import org.openrdf.model.Value;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;

/**
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public class URITest extends RippleTestCase {

    @Test
    public void testValueOf() throws Exception {
        String s = "http://example.org/foo";
        java.net.URI u = java.net.URI.create(s);
        Value v = modelConnection.valueOf(u);
        assertEquals(s, v.stringValue());
    }

    private void namespaceTest(final String uri,
                               final String ns,
                               final String localName,
                               final ModelConnection mc)
            throws Exception {
        IRI uriCreated = createIRI(uri, mc);
        String nsCreated = uriCreated.getNamespace();
        String localNameCreated = uriCreated.getLocalName();

        assertEquals(uriCreated.toString(), uri);
        assertEquals(nsCreated, ns);
        assertEquals(localNameCreated, localName);
    }

    @Test
    public void testURINamespace() throws Exception {
        ModelConnection mc = getTestModel().createConnection();

        InputStream is = URITest.class.getResourceAsStream("UriNamespaceTest.txt");

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is));
        int lineno = 0;

        // Break out when end of stream is reached.
        while (true) {
            String line = reader.readLine();
            lineno++;

            if (null == line)
                break;

            line = line.trim();

            if (!line.startsWith("#") && !line.equals("")) {
                String[] args = line.split("\t");
                if (args.length != 3)
                    throw new RippleException("wrong number of aguments on line " + lineno);
                namespaceTest(args[0], args[1], args[2], mc);
            }
        }

        is.close();
        mc.close();
    }
}

