/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.root1.slicknx;

import java.net.InetAddress;
import java.net.NetworkInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tuwien.auto.calimero.knxnetip.servicetype.SearchResponse;

/**
 *
 * @author achristian
 */
class KnxTunnelingDevice extends KnxInterfaceDevice {
    
    private final Logger log = LoggerFactory.getLogger(getClass());
    private InetAddress ip;
    private final int port;

    public KnxTunnelingDevice(NetworkInterface ni, SearchResponse sr) {
        super(KnxInterfaceDeviceType.TUNNELING, ni, sr);
        
        ip = sr.getControlEndpoint().getAddress();
        port = sr.getControlEndpoint().getPort();
    }


    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    
    @Override
    public String toString() {
        return "Tunneling Device " + address + " \"" + name + "\", KNX medium " + knxMediumString
                + ", IP:Port " + ip+":" +port+ ", MAC address " + mac
                + ", S/N 0x" + sn;
    }
    
    
    
}
