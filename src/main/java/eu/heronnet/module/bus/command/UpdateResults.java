package eu.heronnet.module.bus.command;

import java.util.List;

import eu.heronnet.core.model.Document;

/**
 * @author edoardocausarano
 */
public class UpdateResults {

    private final List<Document> documents;

    public UpdateResults(List<Document> documents) {
        this.documents = documents;
    }

    public List<Document> getDocuments() {
        return documents;
    }
}
