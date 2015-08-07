package eu.heronnet.module.bus.command;

import eu.heronnet.model.Bundle;

import java.util.List;


/**
 * @author edoardocausarano
 */
public class UpdateResults {

    private final List<Bundle> bundles;

    public UpdateResults(List<Bundle> bundles) {
        this.bundles = bundles;
    }

    public List<Bundle> getBundles() {
        return bundles;
    }
}
