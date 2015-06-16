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
import tuwien.auto.calimero.IndividualAddress;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.mgmt.Destination;
import tuwien.auto.calimero.mgmt.ManagementClientImpl;
import tuwien.auto.calimero.mgmt.ManagementProceduresImpl;
import tuwien.auto.calimero.mgmt.TransportLayerImpl;

/**
 *
 * @author achristian
 */
public class TestProgram {
    
    public static void main(String[] args) throws KNXException, InterruptedException, UnknownHostException {
        InetAddress hostadr = InetAddress.getByName("224.0.23.12");
//        InetAddress hostadr = InetAddress.getByName("abb-ipr");
        int port = 3671;

        // setup knx connection
        SlicKNXNetworkLinkIP netlink = new SlicKNXNetworkLinkIP(KNXNetworkLinkIP.ROUTING, null, new InetSocketAddress(hostadr, port), false, new TPSettings(false));
//        KNXNetworkLinkIP netlink = new KNXNetworkLinkIP(KNXNetworkLinkIP.TUNNELING, new InetSocketAddress("localhost", 0), new InetSocketAddress(hostadr, port), false, TPSettings.TP1);
        netlink.getKNXMedium().setDeviceAddress(new IndividualAddress("1.1.250"));
        
        ManagementProceduresImpl mp = new ManagementProceduresImpl(netlink);
        ManagementClientImpl mci = new ManagementClientImpl(netlink);
//        System.out.println("Write address");
//        mci.writeAddress(new IndividualAddress("15.15.21"));
//        System.out.println("done");
        
        IndividualAddress device = new IndividualAddress("1.1.14");
//        IndividualAddress device = new IndividualAddress("1.1.251");
        
//        final byte[] data = new byte[]{0x01, 0x02, 0x03, 0x04};
//        mp.writeMemory(device, 0x00, data, false, false);
        
        byte[] readProperty = mci.readProperty(mci.createDestination(device, true), 0 /* obj-index */, 56 /* prop-id */, 1 /* start */, 1 /* elements*/);
        
        System.out.println("prop: "+Integer.toHexString(readProperty[0]));
        
    }
    
}
