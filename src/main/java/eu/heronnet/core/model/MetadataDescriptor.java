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

import com.google.common.collect.ImmutableList;

import javax.persistence.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name = "MetadataDescriptor.findAll", query = "select descriptor from MetadataDescriptor descriptor"),
        @NamedQuery(name = "MetadataDescriptor.byName", query = "select descriptor from MetadataDescriptor descriptor where descriptor.name = :name")
})
public class MetadataDescriptor {

    @Id
    private byte[] id;

    @Column(unique = true)
    private String name;

    @ElementCollection
    private List<String> properties;

    public MetadataDescriptor(String name, String... properties) {
        this.name = name;
        this.properties = Arrays.asList(properties);
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.reset();
            for (String property : properties) {
                messageDigest.update(property.getBytes());
            }
            id = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    protected MetadataDescriptor() {
    }

    public List<String> getFields() {
        return ImmutableList.copyOf(properties);
    }

    public byte[] getId() {
        return id;
    }


}
