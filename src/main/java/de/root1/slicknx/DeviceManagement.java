/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.root1.slicknx;

import java.util.ArrayList;
import java.util.List;
import tuwien.auto.calimero.IndividualAddress;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.exception.KNXRemoteException;
import tuwien.auto.calimero.exception.KNXTimeoutException;
import tuwien.auto.calimero.link.KNXLinkClosedException;
import tuwien.auto.calimero.mgmt.Destination;
import tuwien.auto.calimero.mgmt.KNXDisconnectException;
import tuwien.auto.calimero.mgmt.ManagementClientImpl;

/**
 *
 * @author achristian
 */
public class DeviceManagement {

    private final ManagementClientImpl mc;
    private Destination dest;

    public DeviceManagement(ManagementClientImpl mc) {
        this.mc = mc;
    }
    
    public void connect(String pa) throws KnxException {
        try {
            dest = mc.createDestination(new IndividualAddress(pa), true);
        } catch (KNXFormatException ex) {
            throw new KnxException("Malformed address '"+pa+"': "+ex.getMessage(), ex);
        }
    }
    
    private void checkConnected() throws KnxException {
        if (dest == null) {
            throw new KnxException("target device not connected or connection already closed");
        }
    }
    
    public void writeAddress(String address) throws KnxException {
        checkConnected();
        try {
            mc.writeAddress(new IndividualAddress(address));
        } catch (KNXFormatException ex) {
            throw new KnxException("Malformed address '"+address+"': "+ex.getMessage(), ex);
        } catch (KNXTimeoutException | KNXLinkClosedException ex) {
            throw new KnxException("Error writing address '"+address+"': "+ex.getMessage(), ex);
        }
    }    
    
    public List<String> readAddress(boolean oneAddressOnly) throws KnxException {
        try {
            IndividualAddress[] addresses = mc.readAddress(oneAddressOnly);
            List<String> adr = new ArrayList<>();
            for (IndividualAddress address : addresses) {
                adr.add(address.toString());
            }
            return adr;
        } catch (KNXTimeoutException | KNXRemoteException | KNXLinkClosedException | InterruptedException ex) {
            throw new KnxException("Error reading address: "+ex.getMessage(), ex);
        }
    }

    public synchronized void writeMem(int startAddr, byte[] data) throws InterruptedException, KnxException {
        checkConnected();
        try {
            mc.writeMemory(dest, startAddr, data);
        } catch (KNXException ex) {
            throw new KnxException("Error writing memory", ex);
        }
    }
    
    public synchronized byte[] readMem(int startAddr, int bytes) throws KnxException {
        checkConnected();
        try {
            return mc.readMemory(dest, startAddr, bytes);
        } catch (KNXTimeoutException | KNXDisconnectException | KNXRemoteException | KNXLinkClosedException | InterruptedException ex) {
            throw new KnxException("Error reading memory: "+ex.getMessage(), ex);
        }
    }
    
    public synchronized byte[] readProperty(int objIndex, int propertyId, int start, int elements) throws KnxException {
        checkConnected();
        try {
            return mc.readProperty(dest, objIndex, propertyId, start, elements);
        } catch (KNXTimeoutException | KNXRemoteException | KNXDisconnectException | KNXLinkClosedException | InterruptedException ex) {
           throw new KnxException("Error reading property: "+ex.getMessage(), ex);
        }
    }

    public synchronized void disconnect() {
        dest.destroy();
        dest = null;
    }

}
