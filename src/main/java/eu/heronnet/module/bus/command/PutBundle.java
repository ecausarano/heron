package eu.heronnet.module.bus.command;

import eu.heronnet.model.Bundle;

/**
 * @author edoardocausarano
 */
public class PutBundle {

    private final Bundle bundle;

    public PutBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public Bundle getBundle() {
        return bundle;
    }
}
