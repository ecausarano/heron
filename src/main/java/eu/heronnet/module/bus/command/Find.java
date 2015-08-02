/*
 * Copyright (C) 2014 edoardocausarano
 *
 * heron is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * heron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with heron. If not, see http://www.gnu.org/licenses
 */

package eu.heronnet.module.bus.command;

import eu.heronnet.core.model.Bundle;

public class Find {

    private final Bundle bundle;
    private final String term;
    private final byte[] hash;
    private boolean local = false;

    public Find(Bundle bundle) {
        this.hash = null;
        this.term = null;
        this.bundle = bundle;
    }

    public Find(Bundle bundle, boolean local) {
        this.hash = null;
        this.bundle = bundle;
        this.term = null;
        this.local = local;
    }

    public Find(String term, boolean local) {
        this.hash = null;
        this.term = term;
        this.bundle = Bundle.emptyBundle();
        this.local = local;
    }

    public Find(byte[] hash, boolean local) {
        this.hash = hash;
        this.term = null;
        this.bundle = Bundle.emptyBundle();
        this.local = local;
    }


    public Find(String term) {
        this.hash = null;
        this.term = term;
        this.bundle = Bundle.emptyBundle();
    }

    public byte[] getHash() {
        return hash;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public String getTerm() {
        return term;
    }

    public boolean isLocal() {
        return local;
    }
}
