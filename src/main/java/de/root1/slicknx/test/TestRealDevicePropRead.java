/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.root1.slicknx.test;

import de.root1.slicknx.SlicKNXNetworkLinkIP;
import de.root1.slicknx.Utils;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import tuwien.auto.calimero.IndividualAddress;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.mgmt.Destination;
import tuwien.auto.calimero.mgmt.ManagementClientImpl;

/**
 *
 * @author achristian
 */
public class TestRealDevicePropRead {
    
    static ManagementClientImpl mc;
    static IndividualAddress remote;
    static Destination destination;

    public static void connect(){
        destination = mc.createDestination(remote, true);
    }
    
    public static void disconnect(){
        destination.destroy();
    }
    
    public static void main(String[] args) throws KNXException, InterruptedException, UnknownHostException {
        InetAddress hostadr = InetAddress.getByName("224.0.23.12");
        int port = 3671;

        // setup knx connection
        SlicKNXNetworkLinkIP netlink = new SlicKNXNetworkLinkIP(KNXNetworkLinkIP.ROUTING, null, new InetSocketAddress(hostadr, port), false, new TPSettings(false));
        netlink.getKNXMedium().setDeviceAddress(new IndividualAddress("1.1.250"));
        mc = new ManagementClientImpl(netlink);
        remote = new IndividualAddress("1.1.11");
        
        connect();
        int authorize = mc.authorize(destination, new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff});
        System.out.println("authorize level: "+authorize);
        
        byte[] readProperty = mc.readProperty(destination, 0, 55, 1, 1);
        System.out.println(Utils.bytesToHex(readProperty));
       
        disconnect();
        
        // shutdown
        mc.detach();
        netlink.close();
        
    }

}
