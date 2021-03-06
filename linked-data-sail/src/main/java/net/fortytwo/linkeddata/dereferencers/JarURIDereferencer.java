package net.fortytwo.linkeddata.dereferencers;

import net.fortytwo.linkeddata.Dereferencer;
import org.restlet.representation.Representation;
import org.restlet.representation.StreamRepresentation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public class JarURIDereferencer implements Dereferencer {
    public Representation dereference(final String uri) {
        return new JarRepresentation(uri);
    }

    private class JarRepresentation extends StreamRepresentation {
        private InputStream inputStream;

        public JarRepresentation(final String uri) {
            super(FileURIDereferencer.findMediaType(uri));

            JarURLConnection jc;

            try {
//System.out.println( "uri = " + uri );
                jc = (JarURLConnection) (new URL(uri).openConnection());
                inputStream = jc.getInputStream();
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }

        public ReadableByteChannel getChannel() throws IOException {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public InputStream getStream() throws IOException {
            return inputStream;
        }

        public void write(OutputStream outputStream) throws IOException {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void write(WritableByteChannel writableByteChannel) throws IOException {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public String toString() {
        return "JAR URI dereferencer";
    }
}
