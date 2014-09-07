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

import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class BerkeleyImplTest {

    private Injector injector;

    @BeforeClass
    public void setUp() throws Exception {
        injector = Guice.createInjector(new TestModule());
        BerkeleyImpl instance = (BerkeleyImpl) injector.getInstance(Persistence.class);
        instance.startUp();
        assertNotNull(instance.primaryData);
        assertNotNull(instance.metaStore);

    }

    @Test
    public void testInitalize() throws Exception {
    }

    @AfterClass
    public void shutDown() throws Exception {
        BerkeleyImpl instance = (BerkeleyImpl) injector.getInstance(Persistence.class);

        instance.shutDown();
        assertNull(instance.environment);
        assertNull(instance.primaryData);
        assertNull(instance.metaStore);
    }

    private class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Persistence.class).to(BerkeleyImpl.class);
//            File tempDir = Files.createTempDir();
//            bindConstant().annotatedWith(Names.named("berkeleyDbEnvHome"));

        }
    }
}
