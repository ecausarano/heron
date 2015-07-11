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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.heronnet.core.model.Field;

public class Find {

    private final List<Field> fields;
    private final String term;
    private final byte[] hash;
    private boolean local = false;

    public Find(List<Field> fields) {
        this.hash = null;
        this.term = null;
        this.fields = new ArrayList<>(fields);
    }

    public Find(List<Field> fields, boolean local) {
        this.hash = null;
        this.fields = fields;
        this.term = null;
        this.local = local;
    }

    public Find(String term, boolean local) {
        this.hash = null;
        this.term = term;
        this.fields = Collections.emptyList();
        this.local = local;
    }

    public Find(byte[] hash, boolean local) {
        this.hash = hash;
        this.term = null;
        this.fields = Collections.emptyList();
        this.local = local;
    }


    public Find(String term) {
        this.hash = null;
        this.term = term;
        this.fields = Collections.emptyList();
    }

    public byte[] getHash() {
        return hash;
    }

    public List<Field> getFields() {
        return fields;
    }

    public String getTerm() {
        return term;
    }

    public boolean isLocal() {
        return local;
    }
}
