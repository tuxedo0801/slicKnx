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
        remote = new IndividualAddress("1.1.251");
        
        
        
        int sleep = 10;
        byte[] readMemory;
        
//        mc.writeMemory(destination, 0x00F0, new byte[]{0x00, 0x01, 0x02});
//        System.out.println("Write done");
//        Thread.sleep(sleep);
//        
//        mc.writeMemory(destination, 0x00F4, new byte[]{0x00, 0x01, 0x02});
//        System.out.println("Write done");
//        Thread.sleep(sleep);
//        
//        mc.writeMemory(destination, 0x00F4, new byte[]{0x00, 0x01, 0x02});
//        System.out.println("Write done");
//        Thread.sleep(sleep);

        int readCount = 100;
        
        
        for (int i=0;i<readCount; i++) {
            System.out.println("Count: "+i);
            connect();
            readMemory = mc.readMemory(destination, 0xF0, 1);
            System.out.println("Read done: "+Arrays.toString(readMemory));
            disconnect();
//            Thread.sleep(1000);
            
        }
        
//        destination = mc.createDestination(remote, true);
//        destination.destroy();
        
        
        // shutdown
        mc.detach();
        netlink.close();
        
    }

}
