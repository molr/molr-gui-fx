package io.molr.gui.fx.devices;

import java.util.ArrayList;
import java.util.List;

// for library loggers
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

// for application loggers
//import de.gsi.cs.co.ap.common.gui.elements.logger.AppLogger;

/**
 *
 * @author krepp
 */
public class DeviceList {

    private List<DeviceFESA> devices;
    
    public DeviceList() {
        this.devices = new ArrayList<>();
    }

    /**
     * @return the devices
     */
    public List<DeviceFESA> getDevices() {
        return this.devices;
    }

    /**
     * @param devices the devices to set
     */
    public void setDevices(List<DeviceFESA> devices) {
        this.devices = devices;
    }
    
    
    
        
    
    
}

