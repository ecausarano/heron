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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
public class BinaryItem {

    private final Logger logger = LoggerFactory.getLogger(BinaryItem.class);
    public byte[] data;
    @Id
    private byte[] id;
    @ElementCollection
    private List<MetadataCollection> metadataCollections = new ArrayList<>();

    public List<MetadataCollection> getMetadataCollections() {
        return ImmutableList.copyOf(metadataCollections);
    }

    public void putMetadataItem(MetadataCollection item) {
        metadataCollections.add(item);
    }

    public byte[] getData() {
        return data;
    }

    public void setData(final byte[] data) {
        this.data = data;
    }

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }


}
