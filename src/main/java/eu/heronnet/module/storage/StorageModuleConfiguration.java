package eu.heronnet.module.storage;

import java.io.File;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.SecondaryConfig;

import eu.heronnet.module.storage.binding.StringTripleBinding;
import eu.heronnet.module.storage.keycreators.StringNGramKeyCreator;
import eu.heronnet.module.storage.keycreators.TripleSubjectIdKeyCreator;

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

    @Bean(name = "tripleStoreIndexConfig" )
    SecondaryConfig tripleStoreIndexConfig() {
        SecondaryConfig secondaryConfig = new SecondaryConfig();
        secondaryConfig.setAllowCreate(true);
        secondaryConfig.setTransactional(true);
        secondaryConfig.setSortedDuplicates(true);
        secondaryConfig.setAllowPopulate(true);
        secondaryConfig.setKeyCreator(tripleSubjectIdKeyCreator());
        return secondaryConfig;
    }

    @Bean (name = "tripleStoreNGramConfig")
    SecondaryConfig tripleStoreNGramConfig() {
        SecondaryConfig secondaryConfig = new SecondaryConfig();
        secondaryConfig.setAllowCreate(true);
        secondaryConfig.setTransactional(true);
        secondaryConfig.setSortedDuplicates(true);
        secondaryConfig.setAllowPopulate(true);
        secondaryConfig.setMultiKeyCreator(stringNGramKeyCreator());
        return secondaryConfig;
    }

    @Bean
    StringTripleBinding stringTripleBinding() {
        return new StringTripleBinding();
    }

    @Bean
    TripleSubjectIdKeyCreator tripleSubjectIdKeyCreator() {
        return new TripleSubjectIdKeyCreator();
    }

    @Bean
    StringNGramKeyCreator stringNGramKeyCreator() {
        return new StringNGramKeyCreator();
    }
}
