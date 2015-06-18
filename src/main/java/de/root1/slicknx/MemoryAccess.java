/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.root1.slicknx;

import tuwien.auto.calimero.IndividualAddress;
import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.mgmt.Destination;
import tuwien.auto.calimero.mgmt.ManagementClientImpl;

/**
 *
 * @author achristian
 */
public class MemoryAccess {

    private final ManagementClientImpl mc;
    private Destination dest;

    public MemoryAccess(String pa, ManagementClientImpl mc) throws KNXFormatException {
        this.mc = mc;
        dest = mc.createDestination(new IndividualAddress(pa), true);
    }

    public synchronized void writeMem(int startAddr, byte[] data) throws InterruptedException, KnxException {
        if (dest == null) {
            throw new KnxException("Memory Access already closed");
        }
        try {
            mc.writeMemory(dest, startAddr, data);
        } catch (tuwien.auto.calimero.exception.KNXException ex) {
            throw new KnxException("Error writing memory", ex);
        }
    }

    public synchronized void close() {
        dest.destroy();
        dest = null;
    }

}
