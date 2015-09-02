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

import de.root1.slicknx.karduino.KarduinoManagement;
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
import tuwien.auto.calimero.datapoint.StateDP;
import tuwien.auto.calimero.dptxlator.DPTXlator;
import tuwien.auto.calimero.dptxlator.TranslatorTypes;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.exception.KNXTimeoutException;
import tuwien.auto.calimero.knxnetip.KNXnetIPTunnel;
import tuwien.auto.calimero.link.KNXLinkClosedException;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicationBase;

/**
 *
 * @author achristian
 */
public final class Knx {
    
    private static final Logger log = LoggerFactory.getLogger(Knx.class);
    
    private KNXNetworkLinkIP netlink;
    private int port = 3671;
    private InetAddress hostadr;
    
    private final Map<String, List<GroupAddressListener>> listeners = new HashMap<>();
    
    private final GeneralGroupAddressListener ggal = new GeneralGroupAddressListener(null, listeners);

    /**
     * Used to write data to KNX and listen to GAs
     */
    private SlicKnxProcessCommunicatorImpl pc;
    private String individualAddress = null;
    
    static {

        // add own DPT types, if necessary
        Map allTypes = TranslatorTypes.getAllMainTypes();
        if (!allTypes.containsKey(TranslatorTypes.TYPE_8BIT_SIGNED)) {
            
            String desc = "8 Bit signed value (main type 6)";
            
            allTypes.put(TranslatorTypes.TYPE_8BIT_SIGNED, new TranslatorTypes.MainType(TranslatorTypes.TYPE_8BIT_SIGNED,
                    DPTXlator8BitSigned.class, desc));
            
        }
    }

    /**
     * Start KNX communication with with ROUTING mode (224.0.23.12:3671)
     *
     * @param individualAddress
     * @throws KnxException if connection to knx(router, ...) fails
     */
    public Knx(String individualAddress) throws KnxException {
        this();
        setIndividualAddress(individualAddress);
    }
    
    /**
     * UNTESTED!!!! Start KNX communication with with TUNNELING mode
     * @param individualAddress local individual address to use
     * @param host
     * @throws KnxException 
     */
    public Knx(String individualAddress, InetAddress host) throws KnxException {
        this(host);
        setIndividualAddress(individualAddress);
    }
    
    /**
     * UNTESTED!!!! Start KNX communication with with TUNNELING mode 
     * @param host
     * @throws KnxException
     */
    public Knx(InetAddress host) throws KnxException {
        try {
            
            // setup knx connection
//            netlink = new SlicKNXNetworkLinkIP(KNXNetworkLinkIP.TUNNELING, null, new InetSocketAddress(host, port), false, new TPSettings(false));
            netlink = new KNXNetworkLinkIP(host.getHostName(), new TPSettings(false));
            
            pc = new SlicKnxProcessCommunicatorImpl(netlink);
            log.debug("Connected to knx via {}:{} and individualaddress {}", hostadr, port, individualAddress);
            pc.addProcessListener(ggal);
        } catch (KNXException | InterruptedException ex) {
            throw new KnxException("Error connecting to KNX: "+ex.getMessage(), ex);
        }
    }
    
    /**
     * 
     * @throws KnxException if connection to knx(router, ...) fails
     */
    public Knx() throws KnxException {
        try {
            this.hostadr = InetAddress.getByName("224.0.23.12");

            // setup knx connection
            netlink = new SlicKNXNetworkLinkIP(KNXNetworkLinkIP.ROUTING, null, new InetSocketAddress(hostadr, port), false, new TPSettings(false));
            
            pc = new SlicKnxProcessCommunicatorImpl(netlink);
            log.debug("Connected to knx via {}:{} and individualaddress {}", hostadr, port, individualAddress);
            pc.addProcessListener(ggal);
        } catch (KNXException | InterruptedException | UnknownHostException ex) {
            throw new KnxException("Error connecting to KNX: "+ex.getMessage(), ex);
        }
    }

    /**
     * sets the own physical, individual address. f.i. "1.1.123"
     *
     * @param individualAddress individual address with dot-notation. f.i.
     * "1.1.123"
     * @throws KnxException
     */
    public void setIndividualAddress(String individualAddress) throws KnxException {
        try {
            netlink.getKNXMedium().setDeviceAddress(new IndividualAddress(individualAddress));
            this.individualAddress = individualAddress;
        } catch (KNXFormatException ex) {
            throw new KnxException("Error setting indiviaual address to " + individualAddress, ex);
        }
    }

    /**
     * Return the individual address or <code>null</code> in case of no address
     * has been set.
     *
     * @return individual address
     */
    public String getIndividualAddress() {
        return individualAddress;
    }

    /**
     * Returns whether an individual address has been set
     *
     * @return true if individual address has been set, false if not
     */
    public boolean hasIndividualAddress() {
        return individualAddress != null;
    }
    
    public KarduinoManagement createKarduinoManagement() throws KnxException {
        try {
            return new KarduinoManagement(netlink);
        } catch (KNXLinkClosedException ex) {
            throw new KnxException("Link closed", ex);
        }
    }
    
    /**
     * DPT 16.001 14 byte ISO8859-1 String
     *
     * @param isResponse
     * @param ga
     * @param string
     * @throws de.root1.slicknx.KnxException
     */
    public void writeString(boolean isResponse, String ga, String string) throws KnxException {
        checkGa(ga);
        try {
            if (isResponse) {
                pc.writeResponse(new GroupAddress(ga), string);
            } else {
                pc.write(new GroupAddress(ga), string);
            }
        } catch (KNXTimeoutException | KNXLinkClosedException | KNXFormatException ex) {
            throw new KnxException("Error writing string", ex);
        }
    }

    /**
     * DPT 1
     *
     * @param isResponse
     * @param ga
     * @param bool
     * @throws de.root1.slicknx.KnxException
     */
    public void writeBoolean(boolean isResponse, String ga, boolean bool) throws KnxException {
        checkGa(ga);
        try {
            if (isResponse) {
                pc.writeResponse(new GroupAddress(ga), bool);
            } else {
                pc.write(new GroupAddress(ga), bool);
            }
        } catch (KNXTimeoutException | KNXLinkClosedException | KNXFormatException ex) {
            throw new KnxException("Error writing boolean", ex);
        }
    }

    /**
     * DPT 9
     *
     * @param isResponse
     * @param ga
     * @param f float value [-671088,64-670760,96]
     * @throws de.root1.slicknx.KnxException
     */
    public void write2ByteFloat(boolean isResponse, String ga, float f) throws KnxException {
        checkGa(ga);
        try {
            if (isResponse) {
                pc.writeResponse(new GroupAddress(ga), f, false);
            } else {
                pc.write(new GroupAddress(ga), f, false);
            }
        } catch (KNXTimeoutException | KNXLinkClosedException | KNXFormatException ex) {
            throw new KnxException("Error writing 2byte float", ex);
        }
    }

    /**
     * DPT 14
     *
     * @param isResponse
     * @param ga
     * @param f 4 byte float value
     * @throws de.root1.slicknx.KnxException
     */
    public void write4ByteFloat(boolean isResponse, String ga, float f) throws KnxException {
        checkGa(ga);
        try {
            if (isResponse) {
                pc.writeResponse(new GroupAddress(ga), f, true);
            } else {
                pc.write(new GroupAddress(ga), f, true);
            }
        } catch (KNXTimeoutException | KNXLinkClosedException | KNXFormatException ex) {
            throw new KnxException("Error writing 4byte float", ex);
        }
    }

    /**
     * DPT 3 Dimming (POsition, Control, Value)
     *
     * @param isResponse
     * @param ga
     * @param control start=true, stop=false, increase=true, decrease=false,
     * down=true, up=false
     * @param stepcode value [0..7]
     * @throws de.root1.slicknx.KnxException
     */
    public void writeControl(boolean isResponse, String ga, boolean control, int stepcode) throws KnxException {
        checkGa(ga);
        try {
            if (isResponse) {
                pc.writeResponse(new GroupAddress(ga), control, stepcode);
            } else {
                pc.write(new GroupAddress(ga), control, stepcode);
            }
        } catch (KNXTimeoutException | KNXLinkClosedException | KNXFormatException ex) {
            throw new KnxException("Error writing control", ex);
        }
    }

    /**
     * DPT 5.001
     *
     * @param isResponse
     * @param ga
     * @param scale value [0..100], i.e. %
     * @throws de.root1.slicknx.KnxException
     */
    public void writeScaled(boolean isResponse, String ga, int scale) throws KnxException {
        checkGa(ga);
        try {
            if (isResponse) {
                pc.writeResponse(new GroupAddress(ga), scale, ProcessCommunicationBase.SCALING);
            } else {
                pc.write(new GroupAddress(ga), scale, ProcessCommunicationBase.SCALING);
            }
        } catch (KNXTimeoutException | KNXLinkClosedException | KNXFormatException ex) {
            throw new KnxException("Error writing scale", ex);
        }
    }

    /**
     * DPT 5.003
     *
     * @param isResponse
     * @param ga
     * @param angle value [0..360], i.e. °C
     * @throws de.root1.slicknx.KnxException
     * @throws IllegalArgumentException in case of wrong value
     */
    public void writeAngle(boolean isResponse, String ga, int angle) throws KnxException {
        if (angle < 0 || angle > 360) {
            throw new IllegalArgumentException("Angle must be between 0..360, but was: " + angle);
        }
        checkGa(ga);
        try {
            if (isResponse) {
                pc.writeResponse(new GroupAddress(ga), angle, ProcessCommunicationBase.ANGLE);
            } else {
                pc.write(new GroupAddress(ga), angle, ProcessCommunicationBase.ANGLE);
            }
        } catch (KNXTimeoutException | KNXLinkClosedException | KNXFormatException ex) {
            throw new KnxException("Error writing angle", ex);
        }
    }

    /**
     * DPT 5.005 DPT 5.010
     *
     * @param isResponse
     * @param ga
     * @param value value [0..255]
     * @throws de.root1.slicknx.KnxException
     * @throws IllegalArgumentException in case of wrong value
     */
    public void writeUnscaled(boolean isResponse, String ga, int value) throws KnxException {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException("value must be between 0..255, but was: " + value);
        }
        checkGa(ga);
        try {
            if (isResponse) {
                pc.writeResponse(new GroupAddress(ga), value, ProcessCommunicationBase.UNSCALED);
            } else {
                pc.write(new GroupAddress(ga), value, ProcessCommunicationBase.UNSCALED);
            }
        } catch (KNXTimeoutException | KNXLinkClosedException | KNXFormatException ex) {
            throw new KnxException("Error writing unscaled", ex);
        }
    }

    /**
     * DPT 6 8-bit signed value
     *
     * @param isResponse
     * @param ga
     * @param value value [-128..127] ^= 8bit signed
     * @throws de.root1.slicknx.KnxException
     */
    public void writeDpt6(boolean isResponse, String ga, int value) throws KnxException {
        if (value < -128 || value > 127) {
            throw new IllegalArgumentException("value must be between -128..127, but was: " + value);
        }
        checkGa(ga);
        try {
            StateDP dp = new StateDP(new GroupAddress(ga), "6.001", 6, "6.001");
            if (isResponse) {
                pc.writeResponse(dp, Integer.toString(value));
            } else {
                pc.write(dp, Integer.toString(value));
            }
            
        } catch (KNXException ex) {
            throw new KnxException("Error writing dpt7", ex);
        }
    }

    /**
     * DPT 7 16-bit unsigned value
     *
     * @param isResponse
     * @param ga
     * @param value value [0..65535] ^= 16bit unsigned
     * @throws de.root1.slicknx.KnxException
     * @throws IllegalArgumentException in case of wrong value
     */
    public void writeDpt7(boolean isResponse, String ga, int value) throws KnxException {
        if (value < 0 || value > 65535) {
            throw new IllegalArgumentException("value must be between 0..65535, but was: " + value);
        }
        checkGa(ga);
        try {
            
            StateDP dp = new StateDP(new GroupAddress(ga), "7.001", 7, "7.001");
            if (isResponse) {
                pc.writeResponse(dp, Integer.toString(value));
            } else {
                pc.write(dp, Integer.toString(value));
            }
            
        } catch (KNXException ex) {
            throw new KnxException("Error writing dpt7", ex);
        }
    }

    /**
     * DPT 7 16-bit unsigned value
     *
     * @param isResponse
     * @param ga
     * @param value value [-32768..32767] ^= 16bit signed
     * @throws de.root1.slicknx.KnxException
     */
    public void writeDpt8(boolean isResponse, String ga, int value) throws KnxException {
        if (value < -32768 || value > 32767) {
            throw new IllegalArgumentException("value must be between -32768..32767, but was: " + value);
        }
        checkGa(ga);
        try {
            
            StateDP dp = new StateDP(new GroupAddress(ga), "8.001", 7, "8.001");
            if (isResponse) {
                pc.writeResponse(dp, Integer.toString(value));
            } else {
                pc.write(dp, Integer.toString(value));
            }
            
        } catch (KNXException ex) {
            throw new KnxException("Error writing dpt8", ex);
        }
    }
    
    public void writeRaw(boolean isResponse, String ga, byte[] data) throws KnxException {
        checkGa(ga);
        try {
            
            DPTXlator t = new DPTXlator(data.length) {
                
                @Override
                public String[] getAllValues() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
                
                @Override
                public Map getSubTypes() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
                
                @Override
                protected void toDPT(String value, short[] dst, int index) throws KNXFormatException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
                
            };
            
            t.setData(data, 0);
            
            pc.write(new GroupAddress(ga), t);
            
        } catch (KNXException ex) {
            throw new KnxException("Error writing dpt8", ex);
        }
    }

    /**
     * Add a listener for the specified group address.
     *
     * @param groupAddress group address, format "x/y/z"
     * @param listener
     */
    public void addGroupAddressListener(String groupAddress, GroupAddressListener listener) {
        checkGa(groupAddress);
        synchronized (listeners) {
            List<GroupAddressListener> listenerslist = listeners.get(groupAddress);
            if (listenerslist == null) {
                listenerslist = new ArrayList<>();
                listeners.put(groupAddress, listenerslist);
            }
            listenerslist.add(listener);
        }
    }

    /**
     * Global listener for all group addresses
     *
     * @param listener
     */
    public void setGlobalGroupAddressListener(GroupAddressListener listener) {
        this.ggal.setMaster(listener);
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
                    listeners.remove(groupAddress);
                }
            }
        }
    }
    
    private void checkGa(String ga) throws RuntimeException {
        
    }
    
    public static void main(String[] args) throws UnknownHostException, KnxException {
        
        final Knx knx = new Knx("1.1.254");
        
        knx.addGroupAddressListener("1/1/200", new GroupAddressListener() {
            
            @Override
            public void readRequest(GroupAddressEvent event) {
                try {
                    System.out.println("Answering read request");
                    knx.writeBoolean(true, "1/1/200", true);
                } catch (KnxException ex) {
                    ex.printStackTrace();
                }
                
            }
            
            @Override
            public void readResponse(GroupAddressEvent event) {
            }
            
            @Override
            public void write(GroupAddressEvent event) {
            }
        });
        
        while (1 != 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(Knx.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
}
