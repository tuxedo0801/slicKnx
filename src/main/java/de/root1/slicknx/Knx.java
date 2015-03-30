/*
 * Copyright (C) 2015 Alexander Christian <alex(at)root1.de>. All rights reserved.
 * 
 * This file is part of slicKnx.
 *
 *   slicKnx is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   slicKnx is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with slicKnx.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.root1.slicknx;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.IndividualAddress;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.exception.KNXTimeoutException;
import tuwien.auto.calimero.link.KNXLinkClosedException;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicationBase;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;

/**
 *
 * @author achristian
 */
public class Knx {

    private static final Logger log = LoggerFactory.getLogger(Knx.class);

    private KNXNetworkLinkIP netlink;
    private int port = 3671;
    private InetAddress hostadr;

    private final Map<String, List<GroupAddressListener>> listeners = new HashMap<>();

    private final GeneralGroupAddressListener ggal = new GeneralGroupAddressListener(listeners);

    /**
     * Used to write data to KNX and listen to GAs
     */
    private ProcessCommunicatorImpl pc;

    /**
     * Start KNX communication with with ROUTING mode (224.0.23.12:3671)
     *
     * @param individualAddress
     */
    public Knx(String individualAddress) {
        try {
            this.hostadr = InetAddress.getByName("224.0.23.12");

            // setup knx connection
            netlink = new KNXNetworkLinkIP(KNXNetworkLinkIP.ROUTING, null, new InetSocketAddress(hostadr, port), false, new TPSettings(false));
            netlink.getKNXMedium().setDeviceAddress(new IndividualAddress(individualAddress));
            pc = new ProcessCommunicatorImpl(netlink);
            log.debug("Connected to knx via {}:{} and individualaddress {}", hostadr, port, individualAddress);
            pc.addProcessListener(ggal);
        } catch (KNXException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (UnknownHostException ex) {
            // should not happen as address is pre-defined
            ex.printStackTrace();
        }
    }

    /**
     * 14 byte ISO8859-1 String
     * @param ga
     * @param string 
     * @throws de.root1.slicknx.KnxException 
     */
    public void writeString(String ga, String string) throws KnxException {
        checkGa(ga);
        try {
            pc.write(new GroupAddress(ga), string);
        } catch (KNXTimeoutException | KNXLinkClosedException | KNXFormatException ex) {
            throw new KnxException("Error writing string", ex);
        }
    }

    /**
     * DPT ?
     * @param ga
     * @param bool 
     * @throws de.root1.slicknx.KnxException 
     */
    public void writeBoolean(String ga, boolean bool) throws KnxException {
        checkGa(ga);
        try {
            pc.write(new GroupAddress(ga), bool);
        } catch (KNXTimeoutException | KNXLinkClosedException | KNXFormatException ex) {
            throw new KnxException("Error writing boolean", ex);
        }
    }

    /**
     * DPT 9
     * @param ga
     * @param f 
     * @throws de.root1.slicknx.KnxException 
     */
    public void write2ByteFloat(String ga, float f) throws KnxException {
        checkGa(ga);
        try {
            pc.write(new GroupAddress(ga), f, false);
        } catch (KNXTimeoutException | KNXLinkClosedException | KNXFormatException ex) {
            throw new KnxException("Error writing 2byte float", ex);
        }
    }

    /**
     * DPT 14
     * @param ga
     * @param f 4 byte float value
     * @throws de.root1.slicknx.KnxException
     */
    public void write4ByteFloat(String ga, float f) throws KnxException {
        checkGa(ga);
        try {
            pc.write(new GroupAddress(ga), f, true);
        } catch (KNXTimeoutException | KNXLinkClosedException | KNXFormatException ex) {
            throw new KnxException("Error writing 4byte float", ex);
        }
    }

    /**
     * Step
     * @param ga
     * @param control start=true, stop=false, increase=true, decrease=false, down=true, up=false
     * @param stepcode value [0..7]
     * @throws de.root1.slicknx.KnxException
     */
    public void writeControl(String ga, boolean control, int stepcode) throws KnxException {
        checkGa(ga);
        try {
            pc.write(new GroupAddress(ga), control, stepcode);
        } catch (KNXTimeoutException | KNXLinkClosedException | KNXFormatException ex) {
            throw new KnxException("Error writing control", ex);
        }
    }

    /**
     * DPT 5.001
     * @param ga
     * @param scale value [0..100], i.e. %
     * @throws de.root1.slicknx.KnxException
     */
    public void writeScale(String ga, int scale) throws KnxException {
        checkGa(ga);
        try {
            pc.write(new GroupAddress(ga), scale, ProcessCommunicationBase.SCALING);
        } catch (KNXTimeoutException | KNXLinkClosedException | KNXFormatException ex) {
            throw new KnxException("Error writing scale", ex);
        }
    }   
    
    /**
     * DPT 5.003
     * @param ga
     * @param angle value [0..360], i.e. °C
     * @throws de.root1.slicknx.KnxException
     */
    public void writeAngle(String ga, int angle) throws KnxException {
        checkGa(ga);
        try {
            pc.write(new GroupAddress(ga), angle, ProcessCommunicationBase.ANGLE);
        } catch (KNXTimeoutException | KNXLinkClosedException | KNXFormatException ex) {
            throw new KnxException("Error writing angle", ex);
        }
    }  
    
    /**
     * DPT 5.010
     * DPT 5.005
     * 
     * @param ga
     * @param count value [0..255], i.e. absolute 8-bit dimm value
     * @throws de.root1.slicknx.KnxException
     */
    public void writeCount(String ga, int count) throws KnxException {
        checkGa(ga);
        try {
            pc.write(new GroupAddress(ga), count, ProcessCommunicationBase.UNSCALED);
        } catch (KNXTimeoutException | KNXLinkClosedException | KNXFormatException ex) {
            throw new KnxException("Error writing count", ex);
        }
    }    

    /**
     * Add a listener for the specified group address.
     * @param groupAddress group address, format "x/y/z"
     * @param listener 
     */
    public void addGroupAddressListener(String groupAddress, GroupAddressListener listener) {
        checkGa(groupAddress);
        synchronized (listeners) {
            List<GroupAddressListener> listenerslist = listeners.get(groupAddress);
            if (listenerslist == null) {
                listenerslist = new ArrayList<>();
            }
            listenerslist.add(listener);
        }
    }

    /**
     * Remove a listener from listening to specified group address
     * 
     * @param groupAddress group address, format "x/y/z"
     * @param listener 
     */
    public void removeGroupAddressListener(String groupAddress, GroupAddressListener listener) {
        checkGa(groupAddress);
        synchronized (listeners) {
            List<GroupAddressListener> listenerslist = listeners.get(groupAddress);
            if (listenerslist != null) {
                listenerslist.remove(listener);
                if (listenerslist.isEmpty()) {
                    listeners.remove(groupAddress, listenerslist);
                }
            }
        }
    }

    private void checkGa(String ga) throws RuntimeException {

    }

    public static void main(String[] args) throws UnknownHostException, KnxException {
        
        Knx knx = new Knx("1.1.254");
        
        knx.addGroupAddressListener("1/1/15", new GroupAddressListener() {

            @Override
            public void readRequest(GroupAddressEvent event) {
            }

            @Override
            public void readResponse(GroupAddressEvent event) {
            }

            @Override
            public void write(GroupAddressEvent event) {
                try {
                    System.out.println("Received update for 1/1/15: "+event.asBool());
                } catch (KnxFormatException ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        knx.writeBoolean("1/1/15", true);


    }

}
