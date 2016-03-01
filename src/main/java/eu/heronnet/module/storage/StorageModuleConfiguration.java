package eu.heronnet.module.storage;

import javax.inject.Inject;
import java.io.File;

import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.SecondaryConfig;
import eu.heronnet.module.storage.binding.BundleBinding;
import eu.heronnet.module.storage.binding.DateNodeBinding;
import eu.heronnet.module.storage.binding.NodeBinding;
import eu.heronnet.module.storage.binding.SubjectNodeBinding;
import eu.heronnet.module.storage.keycreators.PredicateIdIndexKeyCreator;
import eu.heronnet.module.storage.keycreators.StringObjectNgramIndexKeyCreator;
import eu.heronnet.module.storage.keycreators.SubjectIdIndexKeyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author edoardocausarano
 */
@Configuration
public class StorageModuleConfiguration {
    @Inject
    String heronDataRoot;

    @Inject
    BundleBinding bundleBinding;
    @Inject
    DateNodeBinding dateNodeBinding;
    @Inject
    NodeBinding nodeBinding;
    @Inject
    SubjectNodeBinding subjectNodeBinding;

    @Inject
    SubjectIdIndexKeyCreator subjectIdIndexKeyCreator;
    @Inject
    PredicateIdIndexKeyCreator predicateIdIndexKeyCreator;
    @Inject
    StringObjectNgramIndexKeyCreator stringObjectNgramIndexKeyCreator;

    @Bean
    Environment environment() {
        String dbEnvHome = heronDataRoot + "/db";
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
    SecondaryConfig subjectIdIndexConfig() {
        SecondaryConfig secondaryConfig = new SecondaryConfig();
        secondaryConfig.setAllowCreate(true);
        secondaryConfig.setTransactional(true);
        secondaryConfig.setSortedDuplicates(true);
        secondaryConfig.setAllowPopulate(true);
        secondaryConfig.setKeyCreator(subjectIdIndexKeyCreator);
        return secondaryConfig;
    }

    @Bean
    SecondaryConfig predicateIdIndexConfig() {
        SecondaryConfig secondaryConfig = new SecondaryConfig();
        secondaryConfig.setAllowCreate(true);
        secondaryConfig.setTransactional(true);
        secondaryConfig.setSortedDuplicates(true);
        secondaryConfig.setAllowPopulate(true);
        secondaryConfig.setMultiKeyCreator(predicateIdIndexKeyCreator);
        return secondaryConfig;
    }


    @Bean
    SecondaryConfig stringObjectNgramIndexConfig() {
        SecondaryConfig secondaryConfig = new SecondaryConfig();
        secondaryConfig.setAllowCreate(true);
        secondaryConfig.setTransactional(true);
        secondaryConfig.setSortedDuplicates(true);
        secondaryConfig.setAllowPopulate(true);
        secondaryConfig.setMultiKeyCreator(stringObjectNgramIndexKeyCreator);
        return secondaryConfig;
    }
}
