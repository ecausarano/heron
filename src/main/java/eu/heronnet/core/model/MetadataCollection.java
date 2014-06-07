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

package eu.heronnet.core.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MetadataCollection {


    private byte[] referencedBinary;

    private Map<String, String> metadata = new HashMap<>();

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public String put(String key, String value) {
        return metadata.put(key, value);
    }

    public String get(String key) {
        return metadata.get(key);
    }

    public String remove(String key) {
        return metadata.remove(key);
    }

    public byte[] getReferencedBinary() {
        return referencedBinary;
    }

    public void setReferencedBinary(byte[] referencedBinary) {
        this.referencedBinary = referencedBinary;
    }
}
