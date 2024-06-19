package com.kandclay.gwt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.kandclay.Main;
import com.kandclay.utils.Constants;

/**
 * Launches the GWT application.
 */
public class GwtLauncher extends GwtApplication {
    @Override
    public GwtApplicationConfiguration getConfig() {
        // Resizable application, uses available space in browser with no padding:
        GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(true);
        cfg.padVertical = 0;
        cfg.padHorizontal = 0;
        return cfg;
        // If you want a fixed size application, comment out the above resizable section,
        // and uncomment below:
//        return new GwtApplicationConfiguration(Constants.General.WIDTH, Constants.General.HEIGHT);
    }

//    @Override
//    protected void adjustMeterPanel(Panel meterPanel, Style meterStyle) {
//        meterPanel.setStyleName("gdx-meter");
//        meterPanel.addStyleName("nostripes");
//        meterStyle.setProperty("backgroundColor", "#ff0000"); // 9bbc0f
//        meterStyle.setProperty("backgroundImage", "none");
//        Style meterPanelStyle = meterPanel.getElement().getStyle();
//        meterPanelStyle.setProperty("backgroundColor", "#ff0000");
//    }

    @Override
    public ApplicationListener createApplicationListener() {
        return new Main();
    }
}
