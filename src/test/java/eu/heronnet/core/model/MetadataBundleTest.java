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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class MetadataBundleTest {

    private MetadataDescriptor descriptor;
    private MetadataBundle bundle;

    @BeforeMethod
    public void setup() {
        descriptor = new MetadataDescriptor("one", "two");
        bundle = new MetadataBundle(Arrays.asList(descriptor));
//        for(String key : descriptor.getFields()) {
//            metadataBundle.
//        }
    }

    @Test
    public void testGetMetaTypeDescriptors() throws Exception {
        final List<MetadataDescriptor> descriptors = bundle.getMetaTypeDescriptors();
        assertEquals(Arrays.asList(descriptor), descriptors);
    }

    @Test
    public void testAddMetaTypeDescriptor() throws Exception {
        final MetadataDescriptor anotherDescriptor = new MetadataDescriptor("three", "four");
        bundle.addMetaTypeDescriptor(anotherDescriptor);
        assertEquals(Arrays.asList(descriptor, anotherDescriptor), bundle.getMetaTypeDescriptors());
    }

    @Test
    public void testGetMetadataItem() throws Exception {
        final Map<String, String> metadata = bundle.getMetadata(descriptor);
        assertEquals(metadata.size(), 0);
        final List<String> fields = descriptor.getFields();
        metadata.put(fields.get(0), "foo");
        assertEquals(1, metadata.size());
        assertTrue(metadata.containsKey(fields.get(0)));
        assertEquals(metadata.get(fields.get(0)), "foo");
    }

    @Test
    public void testGetMetadata() throws Exception {
        final Map<String, String> metadata = bundle.getMetadata(descriptor);
        assertEquals(metadata.size(), 0);
        for (String key : descriptor.getFields()) {
            metadata.put(key, "value:" + key);
        }
        assertEquals(metadata.size(), descriptor.getFields().size());
        final Map<String, String> modified = bundle.getMetadata(descriptor);
        for (String key : modified.keySet()) {
            assertEquals(metadata.get(key), "value:" + key);
        }
    }
}
