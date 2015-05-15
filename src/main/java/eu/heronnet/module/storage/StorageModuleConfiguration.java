package eu.heronnet.module.storage;

import java.io.File;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

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
}
