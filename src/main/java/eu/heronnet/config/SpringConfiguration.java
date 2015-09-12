package eu.heronnet.config;

import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author edoardocausarano
 */
@Configuration
@ComponentScan(
        basePackages = "eu.heronnet",
        includeFilters = @ComponentScan.Filter(Configuration.class))
public class SpringConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SpringConfiguration.class);

    @Inject
    ApplicationContext applicationContext;

    @Bean
    String heronDataRoot() throws IOException {
        String dataFolder = null;

        switch (System.getProperty("os.name")) {
            case "Mac OS X":
                dataFolder = System.getProperty("user.home") + "/Library/eu.heronnet.Heron";
                break;
            default:
                dataFolder = System.getProperty("user.home") + "/heron";
        }
        Files.createDirectories(
                Paths.get(dataFolder),
                PosixFilePermissions.asFileAttribute(
                        EnumSet.of(
                                PosixFilePermission.OWNER_READ,
                                PosixFilePermission.OWNER_WRITE,
                                PosixFilePermission.OWNER_EXECUTE)));
        return dataFolder;
    }

    @Bean
    ExecutorService executor() {
        return Executors.newWorkStealingPool();
    }

    @Bean
    ServiceManager serviceManager() {
        Map<String, Service> services = applicationContext.getBeansOfType(Service.class);
        ServiceManager manager = new ServiceManager(services.values());

        manager.addListener(new ServiceManager.Listener() {
            public void stopped() {}
            public void healthy() {
                logger.debug("Done initializing services, Heron application up");
            }
            public void failure(Service service) {
                logger.error("Failure in service={}", service.getClass());
            }
        }, executor());

        return manager;
    }
}
