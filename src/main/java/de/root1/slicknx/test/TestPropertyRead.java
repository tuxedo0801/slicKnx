/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.root1.slicknx.test;

import de.root1.slicknx.SlicKNXNetworkLinkIP;
import java.lang.reflect.Field;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import tuwien.auto.calimero.IndividualAddress;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.knxnetip.KNXnetIPConnection;
import tuwien.auto.calimero.knxnetip.KNXnetIPRouting;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.mgmt.PropertyClient;
import tuwien.auto.calimero.mgmt.RemotePropertyServiceAdapter;

/**
 *
 * @author achristian
 */
public class TestPropertyRead {
    
    public static void main(String[] args) throws UnknownHostException, KNXException, InterruptedException {
        
        InetAddress hostadr = InetAddress.getByName("224.0.23.12");
        int port = 3671;

        // setup knx connection
        SlicKNXNetworkLinkIP netlink = new SlicKNXNetworkLinkIP(KNXNetworkLinkIP.ROUTING, null, new InetSocketAddress(hostadr, port), false, new TPSettings(false));
        System.out.println("is loopback mode: "+netlink.isLoopbackMode());
        
//        try {
//            Field connField = KNXNetworkLinkIP.class.getDeclaredField("conn");
//            connField.setAccessible(true);
//            
//            KNXnetIPConnection conn = (KNXnetIPConnection) connField.get(netlink);
//            
//            if (conn instanceof KNXnetIPRouting) {
//                KNXnetIPRouting knxnetiprouting = (KNXnetIPRouting) conn;
//                Field socketField = KNXnetIPRouting.class.getSuperclass().getDeclaredField("socket");
//                socketField.setAccessible(true);
//                MulticastSocket socket = (MulticastSocket) socketField.get(knxnetiprouting);
//                socket.setLoopbackMode(true);
//                
//                System.out.println("loopback enabled: "+knxnetiprouting.usesMulticastLoopback());
//            }
//                    
//        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | SocketException ex) {
//            ex.printStackTrace();
//        }
        netlink.getKNXMedium().setDeviceAddress(new IndividualAddress("1.1.250"));
        IndividualAddress remote = new IndividualAddress("1.1.14");
        
        RemotePropertyServiceAdapter rpsa = new RemotePropertyServiceAdapter(netlink, remote, null, true);
        PropertyClient pc = new PropertyClient(rpsa);
        
        byte[] property = pc.getProperty(0 /* obj-index */, 56 /* prop-id */, 1 /* start */, 1 /* elements*/);
        
        System.out.println(Arrays.toString(property));
    }
    
}
