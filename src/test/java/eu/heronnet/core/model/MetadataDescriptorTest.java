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

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.persistence.*;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class MetadataDescriptorTest {


    private EntityManager entityManager;
    private EntityManagerFactory entityManagerFactory;

    @BeforeMethod
    public void setup() {
        entityManagerFactory = Persistence.createEntityManagerFactory("test-unit");
        entityManager = entityManagerFactory.createEntityManager();
    }

    @Test
    public void testMeta() {
        final String[] parameters = {"one", "two", "three"};
        final MetadataDescriptor descriptor = new MetadataDescriptor("demo", parameters);
        final List<String> fields = descriptor.getFields();
        assertEquals(parameters, fields.toArray());
    }

    @Test
    public void testWithDb() {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        final String[] parameters = {"one", "two", "three"};
        final MetadataDescriptor descriptor = new MetadataDescriptor("another", parameters);
        entityManager.persist(descriptor);
        transaction.commit();

        transaction.begin();
        final Query query = entityManager.createQuery("SELECT d from MetadataDescriptor d where :id = d.id", MetadataDescriptor.class);
        query.setParameter("id", descriptor.getId());
        final List resultList = query.getResultList();
        assertTrue(resultList.size() == 1);
        assertTrue(resultList.get(0).equals(descriptor));
        transaction.commit();
    }

    @AfterClass
    public void teardown() {
        entityManager.close();
        entityManagerFactory.close();
    }
}
