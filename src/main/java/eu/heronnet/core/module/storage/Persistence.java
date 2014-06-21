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

package eu.heronnet.core.module.storage;

import com.google.common.util.concurrent.Service;
import eu.heronnet.core.model.Binary;
import eu.heronnet.core.model.MetadataBundle;
import eu.heronnet.core.model.MetadataDescriptor;

import java.io.IOException;
import java.util.List;

public interface Persistence extends Service {

    void putBinary(Binary item, MetadataBundle bundle) throws IOException;

    Binary getBinaryById(byte[] id);

    MetadataBundle findByID(byte[] id);

    List<MetadataBundle> fetchAllMetadataItems();

    List<MetadataDescriptor> fetchAllMetadataDescriptors();

    void putMetadataDescriptor(MetadataDescriptor descriptor);

    MetadataDescriptor getMetadataDescriptorByName(String name);

    void putMetadata(List<MetadataBundle> metadataBundles);

    void deleteBinaryById(byte[] id);

}
