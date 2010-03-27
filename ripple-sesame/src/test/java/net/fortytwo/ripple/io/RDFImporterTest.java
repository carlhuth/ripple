/*
 * $URL$
 * $Revision$
 * $Author$
 *
 * Copyright (C) 2007-2010 Joshua Shinavier
 */


package net.fortytwo.ripple.io;

import net.fortytwo.flow.rdf.SesameInputAdapter;
import net.fortytwo.ripple.RippleException;
import net.fortytwo.ripple.model.ModelConnection;
import net.fortytwo.ripple.test.RippleTestCase;
import net.fortytwo.ripple.util.RDFHTTPUtils;
import net.fortytwo.ripple.util.RDFUtils;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFFormat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

public class RDFImporterTest extends RippleTestCase
{
    void addGraph( final InputStream is,
                    final URI context,
                    final RDFFormat format,
                    final ModelConnection mc )
        throws RippleException
    {
        RDFImporter importer = new RDFImporter( mc, context );
        SesameInputAdapter sc = new SesameInputAdapter( importer );
        RDFUtils.read( is, sc, context.toString(), format );
mc.commit();
    }

    void addGraph( final URL url,
                    final URI context,
                    final RDFFormat format,
                    final ModelConnection mc )
        throws RippleException
    {
        RDFImporter importer = new RDFImporter( mc, context );
        SesameInputAdapter sc = new SesameInputAdapter( importer );
        RDFHTTPUtils.read( url, sc, context.toString(), format );
mc.commit();
    }

    public void testImporter() throws Exception
    {
        ModelConnection mc = getTestModel().getConnection( "for ImporterTest" );

        {
            URI ctxA = mc.createURI( "urn:org.example.test.addGraphTest.turtleStrA#" );

            String s = "@prefix foo:  <http://example.org/foo#>.\n"
                + "foo:a foo:b foo:c." ;
            InputStream is = new ByteArrayInputStream( s.getBytes() );

            addGraph( is, ctxA, RDFFormat.TURTLE, mc );

            //assertEquals( mc.countStatements( null ), 1 );
            assertEquals( mc.countStatements( ctxA ), 1 );
        }

        {
            URL test1Url = RDFImporterTest.class.getResource( "rdfImporterTest1.ttl" );
            URL test2Url = RDFImporterTest.class.getResource( "rdfImporterTest2.ttl" );

            URI ctxA = mc.createURI( "urn:org.example.test.addGraphTest.turtleA#" );
            URI ctxB = mc.createURI( "urn:org.example.test.addGraphTest.turtleB#" );

            addGraph( test1Url, ctxA, RDFFormat.TURTLE, mc );
            assertEquals( mc.countStatements( ctxA ), 2 );
            addGraph( test2Url, ctxA, RDFFormat.TURTLE, mc );
            assertEquals( mc.countStatements( ctxA ), 4 );

            addGraph( test1Url, ctxB, RDFFormat.TURTLE, mc );
            assertEquals( mc.countStatements( ctxB ), 2 );
            addGraph( test2Url, ctxB, RDFFormat.TURTLE, mc );
            assertEquals( mc.countStatements( ctxB ), 4 );
        }

        {
            URL test1Url = RDFImporterTest.class.getResource( "rdfImporterTest1.rdf" );
            URL test2Url = RDFImporterTest.class.getResource( "rdfImporterTest2.rdf" );

            URI ctxA = mc.createURI( "urn:org.example.test.addGraphTest.rdfxmlA#" );
            URI ctxB = mc.createURI( "urn:org.example.test.addGraphTest.rdfxmlB#" );

            addGraph( test1Url, ctxA, RDFFormat.RDFXML, mc );
            assertEquals( mc.countStatements( ctxA ), 2 );
            addGraph( test2Url, ctxA, RDFFormat.RDFXML, mc );
            assertEquals( mc.countStatements( ctxA ), 4 );

            addGraph( test1Url, ctxB, RDFFormat.RDFXML, mc );
            assertEquals( mc.countStatements( ctxB ), 2 );
            addGraph( test2Url, ctxB, RDFFormat.RDFXML, mc );
            assertEquals( mc.countStatements( ctxB ), 4 );
        }

        mc.close();
    }
}

