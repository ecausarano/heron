package eu.heronnet.module.storage.rdf.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by edo on 07/08/15.
 */
public class RawBundle {

    private final byte[] subjectId;

    private final HashSet<RawStatement> rawStatements = new HashSet<>();

    public RawBundle(byte[] subjectId) {
        this.subjectId = subjectId;
    }

    public RawBundle(byte[] subject, Set<RawStatement> rawStatements) {
        this.subjectId = subject;
        this.rawStatements.addAll(rawStatements);
    }

    public void add(RawStatement rawStatement) {
        rawStatements.add(rawStatement);
    }

    public byte[] getSubjectId() {
        return subjectId;
    }

    public Set<RawStatement> getRawStatements() {
        return new HashSet<>(rawStatements);
    }
}
