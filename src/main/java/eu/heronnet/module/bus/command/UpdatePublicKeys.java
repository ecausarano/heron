package eu.heronnet.module.bus.command;

import eu.heronnet.model.Bundle;

import java.util.List;

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
