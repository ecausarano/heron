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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

@Entity
public class MetadataCollection {

    ListMultimap<String, String> metadata = ArrayListMultimap.create();
    @Id
    private byte[] id;
    //    @OneToMany(mappedBy = )
    private byte[] referencedBinary;
    private boolean dirty = true;

    public byte[] getId() {
        if (dirty) {
            try {
                final MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
                messageDigest.reset();
                for (Map.Entry<String, String> entry : metadata.entries()) {
                    messageDigest.digest((entry.getKey() + entry.getValue()).getBytes());
                }
                id = messageDigest.digest(null);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            dirty = false;
        }

        return id;
    }

    public ListMultimap<String, String> getMetadata() {
        return metadata;
    }

    public void put(String key, String value) {
        metadata.put(key, value);
        dirty = true;
    }

    public List<String> get(String key) {
        return metadata.get(key);
    }

    public void remove(String key) {
        metadata.removeAll(key);
        dirty = true;
    }

    public byte[] getReferencedBinary() {
        return referencedBinary;
    }

    public void setReferencedBinary(byte[] referencedBinary) {
        this.referencedBinary = referencedBinary;
    }
}
