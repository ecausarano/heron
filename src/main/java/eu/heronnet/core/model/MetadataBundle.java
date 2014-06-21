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

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import javax.persistence.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@NamedQueries({
        @NamedQuery(name = "MetadataBundle.findAll", query = "select bundle from MetadataBundle bundle")
})
public class MetadataBundle {

    @Id
    private byte[] id;

    @OneToOne
    private Binary binary;

    @OneToMany
    private List<MetadataDescriptor> descriptors = new ArrayList<>();

    @ElementCollection
    private Map<String, String> metadata = new HashMap<>();

    public MetadataBundle(List<MetadataDescriptor> descriptors) {
        this.descriptors = new ArrayList<>(descriptors);
    }

    protected MetadataBundle() {
    }

    public List<MetadataDescriptor> getMetaTypeDescriptors() {
        return ImmutableList.copyOf(descriptors);
    }

    public void addMetaTypeDescriptor(MetadataDescriptor descriptor) {
        descriptors.add(descriptor);
    }

    public String getMetadataItem(String key) throws IllegalArgumentException {
        return metadata.get(key);
    }

    public void setMetadata(String key, String value) throws IllegalArgumentException {
        // TODO - add an RTE if key is not in in any descriptor
        metadata.put(key, value);
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.reset();
            if (id != null) {
                messageDigest.digest(id);
            }
            messageDigest.update(key.getBytes());
            messageDigest.update(value.getBytes());
            id = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }


    public Map<String, String> getMetadata(MetadataDescriptor descriptor) {
        final List<String> fields = descriptor.getFields();

        return Maps.filterEntries(metadata, new Predicate<Map.Entry<String, String>>() {
            @Override
            public boolean apply(Map.Entry<String, String> input) {
                return fields.contains(input.getKey());
            }
        });
    }

    public Binary getBinary() {
        return binary;
    }

    public void setBinary(Binary binary) {
        this.binary = binary;
    }
}
