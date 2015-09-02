/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.root1.slicknx;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.knxnetip.KNXnetIPConnection;
import tuwien.auto.calimero.knxnetip.KNXnetIPRouting;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.medium.KNXMediumSettings;

/**
 * Same as {@link KNXNetworkLinkIP}, but with per default disabled multicast-loopback mode
 * @author achristian
 */
public class SlicKNXNetworkLinkIP extends KNXNetworkLinkIP {

    public SlicKNXNetworkLinkIP(int serviceMode, InetSocketAddress localEP, InetSocketAddress remoteEP, boolean useNAT, KNXMediumSettings settings) throws KNXException, InterruptedException {
        super(serviceMode, localEP, remoteEP, useNAT, settings);
        setLoopbackMode(false);
    }

    public SlicKNXNetworkLinkIP(String remoteHost, KNXMediumSettings settings) throws KNXException, InterruptedException {
        super(remoteHost, settings);
        setLoopbackMode(false);
    }

    public SlicKNXNetworkLinkIP(NetworkInterface netIf, InetAddress mcGroup, KNXMediumSettings settings) throws KNXException {
        super(netIf, mcGroup, settings);
        setLoopbackMode(false);
    }
    
    /**
     * 
     * @param flag true to enable loopback, fals to disable loopback
     */
    public void setLoopbackMode(boolean flag) {
        try {
            Field connField = KNXNetworkLinkIP.class.getDeclaredField("conn");
            connField.setAccessible(true);
            
            KNXnetIPConnection conn = (KNXnetIPConnection) connField.get(this);
            
            if (conn instanceof KNXnetIPRouting) {
                KNXnetIPRouting knxnetiprouting = (KNXnetIPRouting) conn;
                Field socketField = KNXnetIPRouting.class.getSuperclass().getDeclaredField("socket");
                socketField.setAccessible(true);
                MulticastSocket socket = (MulticastSocket) socketField.get(knxnetiprouting);
                socket.setLoopbackMode(!flag); // weird inverse logic, see javadoc of MulticastSocket#setLoopbackMode
            }
                    
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | SocketException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * 
     * @return true, if loopbackmode, false if no loopback-mode or no information about loopback available/accessible
     */
    public boolean isLoopbackMode() {
        try {
            Field connField = KNXNetworkLinkIP.class.getDeclaredField("conn");
            connField.setAccessible(true);
            
            KNXnetIPConnection conn = (KNXnetIPConnection) connField.get(this);
            
            if (conn instanceof KNXnetIPRouting) {
                KNXnetIPRouting knxnetiprouting = (KNXnetIPRouting) conn;
                
                
                return knxnetiprouting.usesMulticastLoopback();
            }
                    
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    
    
}
