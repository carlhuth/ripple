package net.fortytwo.linkeddata.dereferencers;

import net.fortytwo.linkeddata.Dereferencer;
import net.fortytwo.linkeddata.LinkedDataCache;
import net.fortytwo.linkeddata.RedirectManager;
import net.fortytwo.linkeddata.util.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.restlet.representation.Representation;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

// Note: throughout this implementation, the caching context of a URI and
//       is the same as its success or failure 'memo'.
// The web location may be different, depending on IRI --> URI mapping.

/**
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public class HTTPURIDereferencer implements Dereferencer {
    private static final Logger logger = Logger.getLogger(HTTPURIDereferencer.class);

    // FIXME: temporary
    private final LinkedDataCache linkedDataCache;

    private final RedirectManager redirectManager;

    private final Set<String> badExtensions;

    public HTTPURIDereferencer(final LinkedDataCache linkedDataCache,
                               final RedirectManager redirectManager) {
        this.linkedDataCache = linkedDataCache;
        this.redirectManager = redirectManager;

        badExtensions = new HashSet<>();

    }

    public Representation dereference(final String uri) {
        // Don't dereference a URI which appears to point to a file which is not
        // an RDF document.
        int l = uri.lastIndexOf('.');
        if (l >= 0 && badExtensions.contains(uri.substring(l + 1))) {
            throw new IllegalArgumentException("URI <" + StringUtils.escapeURIString(uri) + "> has blacklisted extension");
            // TODO: we can throw exceptions or return nulls to indicate an error, but we shouldn't do both
        }

        try {
            return new HTTPRepresentation(uri, redirectManager, linkedDataCache.getAcceptHeader());
        } catch (HTTPRepresentation.RedirectToExistingDocumentException e) {
            return null;
        } catch (IOException e) {
            logger.log(Level.ERROR, "failed to dereference URI " + uri, e);
            return null;
        }
    }

    public void blackListExtension(final String ext) {
        badExtensions.add(ext);
    }

    public void whitelistExtension(final String ext) {
        badExtensions.remove(ext);
    }

    @Override
    public String toString() {
        return "HTTP URI dereferencer";
    }
}

