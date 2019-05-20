/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.root1.slicknx;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tuwien.auto.calimero.IndividualAddress;
import tuwien.auto.calimero.knxnetip.servicetype.SearchResponse;
import tuwien.auto.calimero.knxnetip.util.DeviceDIB;

/**
 *
 * @author achristian
 */
public class KnxRoutingDevice extends KnxInterfaceDevice {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private InetAddress mcast;

    KnxRoutingDevice(NetworkInterface ni, SearchResponse sr) {
        super(KnxInterfaceDeviceType.ROUTING, ni, sr);

        try {
            mcast = InetAddress.getByAddress(sr.getDevice().getMulticastAddress());
        } catch (final UnknownHostException ignore) {
        }
    }

    public InetAddress getMulticastAddress() {
        return mcast;
    }
    
    @Override
    public String toString() {
        return "Routing Device " + address + " \"" + name + "\", KNX medium " + knxMediumString
                + ", routing multicast address " + mcast + ", MAC address " + mac
                + ", S/N 0x" + sn;
    }

}
