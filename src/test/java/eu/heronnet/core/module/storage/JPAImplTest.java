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

import com.google.common.util.concurrent.ServiceManager;
import eu.heronnet.core.model.Binary;
import eu.heronnet.core.model.MetadataBundle;
import eu.heronnet.core.model.MetadataDescriptor;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Random;

import static org.testng.Assert.*;

public class JPAImplTest {

    private static final String TEST_UNIT = "test-unit";

    @Test
    public void testStartUpShutdown() throws Exception {
        final JPAImpl service = new JPAImpl(TEST_UNIT);
        ServiceManager manager = new ServiceManager(Arrays.asList(service));

        manager.startAsync();
        manager.awaitHealthy();
        assertTrue(service.isRunning());
        assertNotNull(service.managerFactory);
        assertTrue(service.managerFactory.isOpen());

        manager.stopAsync();
        manager.awaitStopped();
        assertFalse(service.isRunning());
        assertFalse(service.managerFactory.isOpen());
    }

    @Test
    public byte[] testPutBinary() throws Exception {
        final JPAImpl service = new JPAImpl(TEST_UNIT);
        ServiceManager manager = new ServiceManager(Arrays.asList(service));

        manager.startAsync();
        manager.awaitHealthy();

        final Random random = new Random();
        byte[] randomPayload = new byte[4096];
        random.nextBytes(randomPayload);

        final Binary binary = new Binary(randomPayload);
        final MetadataDescriptor descriptor = new MetadataDescriptor("filename");
        final MetadataBundle bundle = new MetadataBundle(Arrays.asList(descriptor));
        bundle.setMetadata("filename", "random stuff");
        bundle.setBinary(binary);

        service.putMetadataDescriptor(descriptor);
        service.putBinary(binary, bundle);

        return binary.getId();
    }

    @Test
    public void testGetBinaryById() throws Exception {
        byte[] id = testPutBinary();
        final JPAImpl service = new JPAImpl(TEST_UNIT);
        ServiceManager manager = new ServiceManager(Arrays.asList(service));

        manager.startAsync();
        manager.awaitHealthy();

        final Binary binary = service.getBinaryById(id);
        assertNotNull(binary);
    }

    @Test
    public void testDeleteBinaryById() throws Exception {

    }

    @Test
    public void testFindByID() throws Exception {

    }

    @Test
    public void testFetchAllMetadataItems() throws Exception {

    }

    @Test
    public void testFetchAllMetadataDescriptors() throws Exception {

    }

    @Test
    public void testPuttMetadata() throws Exception {

    }
}
