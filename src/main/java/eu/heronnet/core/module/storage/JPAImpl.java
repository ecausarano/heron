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

import com.google.common.util.concurrent.AbstractIdleService;
import eu.heronnet.core.model.Binary;
import eu.heronnet.core.model.MetadataBundle;
import eu.heronnet.core.model.MetadataDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

import static javax.persistence.Persistence.createEntityManagerFactory;

public class JPAImpl extends AbstractIdleService implements Persistence {

    private static final Logger logger = LoggerFactory.getLogger(JPAImpl.class);

    protected EntityManagerFactory managerFactory;

    private String persistenceUnit;

    public JPAImpl() {
        persistenceUnit = "heron-unit";
    }

    public JPAImpl(String unit) {
        persistenceUnit = unit;
    }

    @Override
    @Transactional
    protected void startUp() throws Exception {
        logger.debug("initializing persistence unit: {}", persistenceUnit);
        managerFactory = createEntityManagerFactory(persistenceUnit);
        if (managerFactory == null) {
            throw new RuntimeException("unable to initialize EntityManagerFactory");
        }
        final EntityManager manager = managerFactory.createEntityManager();
        final Query descriptorQuery = manager.createNamedQuery("MetadataDescriptor.findAll");
        if (descriptorQuery.getResultList().size() == 0) {
            final MetadataDescriptor basicDescriptor = new MetadataDescriptor("basic");
            manager.persist(basicDescriptor);
        }
        manager.close();
    }

    @Override
    protected void shutDown() throws Exception {
        managerFactory.close();
    }

    @Override
    @Transactional
    public void putBinary(Binary item, MetadataBundle bundle) throws IOException {
        final EntityManager manager = managerFactory.createEntityManager();
        manager.persist(item);
        manager.persist(bundle);
        manager.close();
    }

    @Override
    @Transactional
    public Binary getBinaryById(byte[] id) {
        final EntityManager manager = managerFactory.createEntityManager();
        final Binary binary = manager.find(Binary.class, id);
        manager.close();
        return binary;
    }

    @Override
    @Transactional
    public void deleteBinaryById(byte[] id) {
        final EntityManager manager = managerFactory.createEntityManager();
        final Binary binary = manager.find(Binary.class, id);
        manager.remove(binary);
        manager.close();
    }

    @Override
    @Transactional
    public MetadataBundle findByID(byte[] id) {
        final EntityManager manager = managerFactory.createEntityManager();
        final MetadataBundle bundle = manager.find(MetadataBundle.class, id);
        manager.close();
        return bundle;
    }


    @Override
    @Transactional
    public List<MetadataBundle> fetchAllMetadataItems() {
        final EntityManager manager = managerFactory.createEntityManager();
        final TypedQuery<MetadataBundle> query = manager.createNamedQuery("MetadataBundle.findAll", MetadataBundle.class);
        final List<MetadataBundle> resultList = query.getResultList();
        return resultList;
    }

    @Override
    @Transactional
    public List<MetadataDescriptor> fetchAllMetadataDescriptors() {
        final EntityManager manager = managerFactory.createEntityManager();
        final TypedQuery<MetadataDescriptor> query = manager.createNamedQuery("MetadataDescriptor.findAll", MetadataDescriptor.class);
        final List<MetadataDescriptor> resultList = query.getResultList();
        return resultList;
    }

    @Override
    @Transactional
    public void putMetadataDescriptor(MetadataDescriptor descriptor) {
        final EntityManager manager = managerFactory.createEntityManager();
        manager.persist(descriptor);
    }

    @Override
    @Transactional
    public MetadataDescriptor getMetadataDescriptorByName(String name) {
        final EntityManager manager = managerFactory.createEntityManager();
        final TypedQuery<MetadataDescriptor> query = manager.createNamedQuery("MetadataDescriptor.byName", MetadataDescriptor.class);
        query.setParameter("name", name);
        return query.getSingleResult();
    }

    @Override
    public void putMetadata(List<MetadataBundle> metadataBundles) {

    }

}
