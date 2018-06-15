/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.root1.slicknx;

import java.net.NetworkInterface;
import java.util.Objects;
import tuwien.auto.calimero.IndividualAddress;
import tuwien.auto.calimero.knxnetip.servicetype.SearchResponse;
import tuwien.auto.calimero.knxnetip.util.DeviceDIB;

/**
 *
 * @author achristian
 */
public abstract class KnxInterfaceDevice {

    private KnxInterfaceDeviceType type;

    final IndividualAddress address;
    final String knxMediumString;
    final String mac;
    final String name;
    final String sn;
    private final NetworkInterface ni;

    KnxInterfaceDevice(KnxInterfaceDeviceType type, NetworkInterface ni, SearchResponse sr) {
        this.type = type;
        this.ni = ni;
        DeviceDIB d = sr.getDevice();
        address = d.getAddress();
        name = d.getName();
        knxMediumString = d.getKNXMediumString();
        mac = d.getMACAddressString();
        sn = d.getSerialNumberString();

    }

    public IndividualAddress getAddress() {
        return address;
    }

    public String getKnxMediumString() {
        return knxMediumString;
    }

    public String getMac() {
        return mac;
    }

    public String getName() {
        return name;
    }

    public String getSn() {
        return sn;
    }

    public KnxInterfaceDeviceType getType() {
        return type;
    }

    public NetworkInterface getNetworkInterface() {
        return ni;
    }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 89 * hash + Objects.hashCode(this.type);
    hash = 89 * hash + Objects.hashCode(this.address);
    hash = 89 * hash + Objects.hashCode(this.knxMediumString);
    hash = 89 * hash + Objects.hashCode(this.mac);
    hash = 89 * hash + Objects.hashCode(this.name);
    hash = 89 * hash + Objects.hashCode(this.sn);
    hash = 89 * hash + Objects.hashCode(this.ni);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final KnxInterfaceDevice other = (KnxInterfaceDevice) obj;
    if (!Objects.equals(this.knxMediumString, other.knxMediumString)) {
      return false;
    }
    if (!Objects.equals(this.mac, other.mac)) {
      return false;
    }
    if (!Objects.equals(this.name, other.name)) {
      return false;
    }
    if (!Objects.equals(this.sn, other.sn)) {
      return false;
    }
    if (this.type != other.type) {
      return false;
    }
    if (!Objects.equals(this.address, other.address)) {
      return false;
    }
    if (!Objects.equals(this.ni, other.ni)) {
      return false;
    }
    return true;
  }
    
    
    

    @Override
    public String toString() {

        return "device " + address + " \"" + name + "\", KNX medium " + knxMediumString
                + ", MAC address " + mac
                + ", S/N 0x" + sn+ " iface "+ni.getDisplayName();

    }

}
