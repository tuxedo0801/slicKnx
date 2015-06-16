/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.root1.slicknx.test;

import de.root1.slicknx.SlicKNXNetworkLinkIP;
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
public class TestMemReadWrite {

    public static void main(String[] args) throws KNXException, InterruptedException, UnknownHostException {
        InetAddress hostadr = InetAddress.getByName("224.0.23.12");
        int port = 3671;

        // setup knx connection
        SlicKNXNetworkLinkIP netlink = new SlicKNXNetworkLinkIP(KNXNetworkLinkIP.ROUTING, null, new InetSocketAddress(hostadr, port), false, new TPSettings(false));
        netlink.getKNXMedium().setDeviceAddress(new IndividualAddress("1.1.250"));
        IndividualAddress remote = new IndividualAddress("1.1.14");

        
        ManagementClientImpl mc = new ManagementClientImpl(netlink);
        Destination destination = mc.createDestination(remote, true);
        
//        int accesslevel = mc.authorize(destination, new byte[]{0x10, 0x10, 0x10});
        
        byte[] readMemory = mc.readMemory(destination, 0x0000, 10);
//        mc.writeMemory(destination, 0x0000, new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09});
        
        
        System.out.println(Arrays.toString(readMemory));
        
        // shutdown
        mc.detach();
        netlink.close();
        
    }

}
