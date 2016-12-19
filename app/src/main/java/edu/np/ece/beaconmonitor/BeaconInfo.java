package edu.np.ece.beaconmonitor;

import java.io.Serializable;

/**
 * Created by zqi2 on 18/12/16.
 */

public class BeaconInfo implements Serializable{

    String name;
    String uuid;
    String major;
    String minor;

    public BeaconInfo(String name, String uuid, String major, String minor) {
        this.name = name;
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "BeaconInfo{" +
                "name='" + name + '\'' +
                ", uuid='" + uuid + '\'' +
                ", major='" + major + '\'' +
                ", minor='" + minor + '\'' +
                '}';
    }
}
