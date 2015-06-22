/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.root1.slicknx.karduino;

import de.root1.slicknx.Knx;
import de.root1.slicknx.KnxException;
import de.root1.slicknx.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tuwien.auto.calimero.IndividualAddress;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.exception.KNXInvalidResponseException;
import tuwien.auto.calimero.exception.KNXRemoteException;
import tuwien.auto.calimero.exception.KNXTimeoutException;
import tuwien.auto.calimero.link.KNXLinkClosedException;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.mgmt.Destination;
import tuwien.auto.calimero.mgmt.KNXDisconnectException;
import tuwien.auto.calimero.mgmt.ManagementClientImpl;

/**
 * Class to manage an "KNX-on-Arduino" (Karduino) device.
 * 
 * @author achristian
 */
public class KarduinoManagement {

    private static final Logger log = LoggerFactory.getLogger(KarduinoManagement.class);

    private final ManagementClientImpl mc;
    private Destination dest;

    /**
     * Dont' use this constructor directly. Use {@link Knx#createKarduinoManagement() } instead.
     * @param netlink
     * @throws KNXLinkClosedException 
     */
    public KarduinoManagement(KNXNetworkLinkIP netlink) throws KNXLinkClosedException {
        this.mc = new ManagementClientImpl(netlink);
    }

    /**
     * Restart a connected device. You have to be connected first!
     * @throws KnxException 
     */
    public synchronized void restart() throws KnxException {
        checkConnected();
        try {
            mc.restart(dest);
        } catch (KNXTimeoutException | KNXLinkClosedException ex) {
            throw new KnxException("Error restarting device '" + dest.getAddress().toString() + "': " + ex.getMessage(), ex);
        }
    }

    /**
     * Connect to device
     * @param pa physical address to connect to
     * @throws KnxException 
     */
    public synchronized void connect(String pa) throws KnxException {
        try {
            dest = mc.createDestination(new IndividualAddress(pa), true);
        } catch (KNXFormatException ex) {
            throw new KnxException("Malformed address '" + pa + "': " + ex.getMessage(), ex);
        }
    }

    /**
     * check if connection is valid
     * @throws KnxException 
     */
    private synchronized void checkConnected() throws KnxException {
        if (mc==null) {
            throw new KnxException("KarduinoManagement already closed. Pls. create a new instance.");
        }
        if (dest == null) {
            throw new KnxException("target device not connected or connection already closed");
        }
    }

    /**
     * Authrorize. Key is currently hard-coded: 0x00, 0x0E, 0x01, 0x0B
     * You have to be connected first!
     * 
     * @return
     * @throws KnxException 
     */
    public int authorize() throws KnxException {
        checkConnected();
        try {
            byte[] key = new byte[]{
                (byte) 0x00,
                (byte) 0x0E, 
                (byte) 0x01, 
                (byte) 0x0B };
            
            return mc.authorize(dest, key);
        } catch (KNXDisconnectException | KNXTimeoutException | KNXRemoteException | KNXLinkClosedException | InterruptedException ex) {
            throw new KnxException("Error while authenticating: "+ex.getMessage(), ex);
        }
    }
    
    /**
     * Writes address to device which is in programming mode
     * @param address address to write to device
     * @return success-flag
     * @throws KnxException if f.i. a timeout occurs or more than one device is in programming mode 
     */
    public synchronized boolean writeAddress(String address) throws KnxException {
        try {

            if (dest != null) {
                throw new KnxException("can not write address when beeing connected. disconnect first.");
            }

            connect(address);
            boolean exists = false;
            try {
                mc.readDeviceDesc(dest, 0);
                exists = true;
            } catch (final KNXDisconnectException e) {
                // remote endpoint exists, but might not support CO mode, we proceed
            } catch (final KNXTimeoutException e) {
                // no remote endpoint answered, we proceed
            } catch (KNXInvalidResponseException ex) {
                log.warn("invalid response", ex);
            } catch (InterruptedException ex) {
                log.warn("interrupted while checking for device", ex);
            } finally {
                disconnect();
            }

            if (exists) {
                log.info("Device exists ...");
            }

            boolean setAddr = false;
            synchronized (mc) {
                final int oldTimeout = mc.getResponseTimeout();
                connect(address);
                try {
                    mc.setResponseTimeout(1);
                    // ??? this does not conform to spec, where no max. attempts are given
                    // the problem is that we potentially loop forever (which would be correct)
                    int attempts = 20;
                    int count = 0;
                    while (count != 1 && attempts-- > 0) {
                        try {
                            final IndividualAddress[] list = mc.readAddress(false);
                            for (IndividualAddress ia : list) {
                                System.out.println("ia: " + ia.toString());
                            }
                            count = list.length;
                            if (count == 1 && !list[0].equals(new IndividualAddress(address))) {
                                setAddr = true;
                            } else if (count == 1 && list[0].equals(new IndividualAddress(address))) {
                                log.info("One device responded, but already has " + address + ".");
                            }
                        } catch (final KNXException e) {
                            // a device with newAddress exists but is not in programming mode,
                            // bail out
                            if (exists) {
                                log.warn("device exists but is not in programming mode, "
                                        + "cancel writing address");
                                return false;
                            }
                        }
                        log.info("KNX devices in programming mode: " + count);
                    }
                    if (!setAddr) {
                        log.warn("Will not set address. too much devices in prog-mode or wrong device in prog mode");
                        return false;
                    }
                    log.info("Writing address ...");
                    mc.writeAddress(new IndividualAddress(address));
                    // if this throws, either programming failed, or its
                    // probably some network configuration issue
                    mc.readDeviceDesc(dest, 0);
                    restart();
                    log.info("Writing address ... *done*");
                } catch (KNXInvalidResponseException | KNXDisconnectException | InterruptedException ex) {
                    throw new KnxException("Error writing address: " + ex.getMessage(), ex);
                } finally {
                    disconnect();
                    mc.setResponseTimeout(oldTimeout);
                }
            }
            return true;

            // ----------------------------
        } catch (KNXFormatException ex) {
            throw new KnxException("Malformed address '" + address + "': " + ex.getMessage(), ex);
        } catch (KNXTimeoutException | KNXLinkClosedException ex) {
            throw new KnxException("Error writing address '" + address + "': " + ex.getMessage(), ex);
        }
    }

    /**
     * read address of devices in programming mode
     * @param oneAddressOnly if true, returns after first received addres. if false, the whole response timeout is waited for read responses
     * @return list of addresses of devices which are in programming mode
     * @throws KnxException 
     */
    public List<String> readAddress(boolean oneAddressOnly) throws KnxException {
        try {
            IndividualAddress[] addresses = mc.readAddress(oneAddressOnly);
            List<String> adr = new ArrayList<>();
            for (IndividualAddress address : addresses) {
                adr.add(address.toString());
            }
            return adr;
        } catch (KNXTimeoutException | KNXRemoteException | KNXLinkClosedException | InterruptedException ex) {
            throw new KnxException("Error reading address: " + ex.getMessage(), ex);
        }
    }

    /**
     * Writes data to karduino memory. Be warned: You have to know which address-area is safe to write to (f.i. don't overwrite the eeprom area where the physical address is stored!).
     * You have to be connected first!
     * @param startAddr the start-address
     * @param data the data to write, beginnning at startAddr
     * @throws KnxException 
     */
    public synchronized void writeMem(int startAddr, byte[] data) throws KnxException {
        checkConnected();
        try {
            mc.writeMemory(dest, startAddr, data);
        } catch (KNXException | InterruptedException ex) {
            throw new KnxException("Error writing memory: " + ex.getMessage(), ex);
        }
    }

    /**
     * Reading data from karduino memory. You have to be connected first!
     * 
     * @param startAddr address to start reading from
     * @param bytes number of bytes to read
     * @return bytes read
     * @throws KnxException 
     */
    public synchronized byte[] readMem(int startAddr, int bytes) throws KnxException {
        checkConnected();
        try {
            return mc.readMemory(dest, startAddr, bytes);
        } catch (KNXTimeoutException | KNXDisconnectException | KNXRemoteException | KNXLinkClosedException | InterruptedException ex) {
            throw new KnxException("Error reading memory: " + ex.getMessage(), ex);
        }
    }

//    public synchronized byte[] readProperty(int objIndex, int propertyId, int start, int elements) throws KnxException {
//        checkConnected();
//        try {
//            return mc.readProperty(dest, objIndex, propertyId, start, elements);
//        } catch (KNXTimeoutException | KNXRemoteException | KNXDisconnectException | KNXLinkClosedException | InterruptedException ex) {
//            throw new KnxException("Error reading property: " + ex.getMessage(), ex);
//        }
//    }

    /**
     * Disconnect from karduino device. Reconnect to further manage a/another device
     */
    public synchronized void disconnect() {
        dest.destroy();
        dest = null;
    }
    
    /**
     * close management interface
     */
    public void close() {
        mc.detach();
    }

}
