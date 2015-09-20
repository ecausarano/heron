package eu.heronnet.module.bus.command;

import java.util.List;

import eu.heronnet.model.Bundle;

/**
 * @author edoardocausarano
 */
public class UpdatePublicKeys {

    private final List<Bundle> bundles;

    public UpdatePublicKeys(List<Bundle> bundles) {
        this.bundles = bundles;
    }

    public List<Bundle> getBundles() {
        return bundles;
    }
}
