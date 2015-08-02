package eu.heronnet.module.bus.command;

import java.util.List;

import eu.heronnet.core.model.Bundle;

/**
 * @author edoardocausarano
 */
public class UpdateLocalResults {

    private final List<Bundle> bundles;

    public UpdateLocalResults(List<Bundle> bundles) {
        this.bundles = bundles;
    }

    public List<Bundle> getBundles() {
        return bundles;
    }
}
