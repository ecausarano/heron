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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Entity
public class Binary {

    @Id
    private byte[] id;

    private byte[] data;

    @OneToOne(cascade = CascadeType.DETACH)
    private MetadataBundle metadataBundle;

    protected Binary() {
    }

    public Binary(byte[] data) throws NoSuchAlgorithmException {
        this.data = data;
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        messageDigest.reset();
        messageDigest.update(data);
        this.id = messageDigest.digest();
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

    public MetadataBundle getMetadataBundle() {
        return metadataBundle;
    }

    public void setMetadataBundle(MetadataBundle metadataBundle) {
        this.metadataBundle = metadataBundle;
    }
}
