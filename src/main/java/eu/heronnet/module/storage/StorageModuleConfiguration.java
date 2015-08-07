package eu.heronnet.module.storage;

import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.SecondaryConfig;
import eu.heronnet.module.storage.binding.BundleBinding;
import eu.heronnet.module.storage.keycreators.NodeIdIndexKeyCreator;
import eu.heronnet.module.storage.keycreators.StringObjectNgramIndexKeyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * @author edoardocausarano
 */
@Configuration
public class StorageModuleConfiguration {

    @Bean
    Environment environment() {
        String dbEnvHome = "herondb";
        final EnvironmentConfig config = new EnvironmentConfig();
        config.setAllowCreate(true);
        config.setTransactional(true);

        final File envHome = new File(dbEnvHome);
        if (!envHome.exists()) {
            envHome.mkdir();
        }
        return new Environment(envHome, config);
    }

    @Bean
    DatabaseConfig databaseConfig() {
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setAllowCreate(true);
        databaseConfig.setTransactional(true);
        databaseConfig.setSortedDuplicates(false);
        return databaseConfig;
    }

    @Bean
    SecondaryConfig nodeIdIndexConfig() {
        SecondaryConfig secondaryConfig = new SecondaryConfig();
        secondaryConfig.setAllowCreate(true);
        secondaryConfig.setTransactional(true);
        secondaryConfig.setSortedDuplicates(true);
        secondaryConfig.setAllowPopulate(true);
        secondaryConfig.setMultiKeyCreator(nodeIdIndexKeyCreator());
        return secondaryConfig;
    }

    @Bean
    SecondaryConfig stringObjectNgramIndexConfig() {
        SecondaryConfig secondaryConfig = new SecondaryConfig();
        secondaryConfig.setAllowCreate(true);
        secondaryConfig.setTransactional(true);
        secondaryConfig.setSortedDuplicates(true);
        secondaryConfig.setAllowPopulate(true);
        secondaryConfig.setMultiKeyCreator(stringObjectNgramIndexKeyCreator());
        return secondaryConfig;
    }

    @Bean
    NodeIdIndexKeyCreator nodeIdIndexKeyCreator() {
        return new NodeIdIndexKeyCreator();
    }

    @Bean
    StringObjectNgramIndexKeyCreator stringObjectNgramIndexKeyCreator() {
        return new StringObjectNgramIndexKeyCreator();
    }

    @Bean
    BundleBinding bundleBinding() {
        return new BundleBinding();
    }
}
