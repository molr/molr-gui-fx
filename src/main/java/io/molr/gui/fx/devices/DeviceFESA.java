package io.molr.gui.fx.devices;

// for library loggers
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

// for application loggers
//import de.gsi.cs.co.ap.common.gui.elements.logger.AppLogger;

/**
 *
 * @author krepp
 */
public class DeviceFESA {

    private long dbId;

    private String deviceName;

    private String serverName;

    private String accelerator;

    private String fecName;

    private String className;

    private int classVersionMinor;

    private int classVersionMajor;

    private int classVersionTiny;

    private String acceleratorZone;

    private String timingDomain;

    public long getDbId() {
        return dbId;
    }

    public DeviceFESA setDbId(final long deviceId) {
        dbId = deviceId;
        return this;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public DeviceFESA setDeviceName(final String deviceName) {
        this.deviceName = deviceName;
        return this;
    }

    public String getServerName() {
        return serverName;
    }

    public DeviceFESA setServerName(final String serverName) {
        this.serverName = serverName;
        return this;
    }

    public String getAccelerator() {
        return accelerator;
    }

    public DeviceFESA setAccelerator(final String accelerator) {
        this.accelerator = accelerator;
        return this;
    }

    public String getFecName() {
        return fecName;
    }

    public DeviceFESA setFecName(final String fecName) {
        this.fecName = fecName;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public DeviceFESA setClassName(final String className) {
        this.className = className;
        return this;
    }

    public String getAcceleratorZone() {
        return acceleratorZone;
    }

    public DeviceFESA setAcceleratorZone(final String acceleratorZone) {
        this.acceleratorZone = acceleratorZone;
        return this;
    }

    public String getTimingDomain() {
        return timingDomain;
    }

    public DeviceFESA setTimingDomain(final String timingDomain) {
        this.timingDomain = timingDomain;
        return this;
    }

    public int getClassVersionMinor() {
        return classVersionMinor;
    }

    public DeviceFESA setClassVersionMinor(final int classVersionMinor) {
        this.classVersionMinor = classVersionMinor;
        return this;
    }

    public int getClassVersionMajor() {
        return classVersionMajor;
    }

    public DeviceFESA setClassVersionMajor(final int classVersionMajor) {
        this.classVersionMajor = classVersionMajor;
        return this;
    }

    public int getClassVersionTiny() {
        return classVersionTiny;
    }

    public DeviceFESA setClassVersionTiny(final int classVersionTiny) {
        this.classVersionTiny = classVersionTiny;
        return this;
    }

    
    
}

