/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.root1.slicknx.test;

import de.root1.slicknx.Knx;
import de.root1.slicknx.KnxException;
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
    
    public static void main(String[] args) throws KNXException, InterruptedException, UnknownHostException, KnxException {
        Knx knx = new Knx("1.1.250");
        knx.restartDevice("1.1.251");
    }
    
}
