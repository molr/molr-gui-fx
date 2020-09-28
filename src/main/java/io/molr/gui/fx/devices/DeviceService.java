package io.molr.gui.fx.devices;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.util.Lists;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

// for library loggers
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

// for application loggers
//import de.gsi.cs.co.ap.common.gui.elements.logger.AppLogger;

/**
 *
 * @author krepp
 */
@Component
public class DeviceService {
    
    public void getDevices() {
        
        RestTemplateBuilder builder = new RestTemplateBuilder();
        
        RestTemplate template = builder.build();
        ResponseEntity<DeviceFESA[]> response = template.getForEntity("https://restpro00a.acc.gsi.de/fesa/client/v1/device", DeviceFESA[].class);
        System.out.println("response");
        List<DeviceFESA> devices = Arrays.asList(response.getBody());
        devices.forEach(device -> {
////           if(device.getDeviceName().contains("hest")) {
//               System.out.println("HELLO");
//           }
            System.out.println(device.getAccelerator()+" "+device.getAcceleratorZone()+" "+device.getClassName() );
        });
        System.out.println(response.getBody().length);
    }

    // You can choose a logger (needed imports are given in the import section as comments):
    // for libraries:
    // private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);
    // for applications:
    // private static final AppLogger LOGGER = AppLogger.getLogger();
}

