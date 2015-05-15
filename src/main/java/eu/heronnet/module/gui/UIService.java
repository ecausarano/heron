package eu.heronnet.module.gui;

import javafx.application.Application;

import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.AbstractIdleService;
import eu.heronnet.module.gui.fx.HeronApplication;

/**
 * @author edoardocausarano
 */
@Component
public class UIService extends AbstractIdleService {

    @Override
    protected void startUp() throws Exception {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Application.launch(HeronApplication.class, "");
            }
        });
        thread.start();
    }

    @Override
    protected void shutDown() throws Exception {

    }
}
