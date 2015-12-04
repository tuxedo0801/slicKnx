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
package de.root1.slicknx.karduino;

import de.root1.slicknx.Knx;
import de.root1.slicknx.KnxException;
import de.root1.slicknx.karduino.protocol0x00.ProgProtocol0x00;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tuwien.auto.calimero.link.KNXLinkClosedException;

/**
 * Class to manage an "KNX-on-Arduino" (Karduino) device.
 *
 * @author achristian
 */
public class KarduinoManagement {

    private static final Logger log = LoggerFactory.getLogger(KarduinoManagement.class);

    /**
     * factory
     *
     * @param knx
     * @return
     */
    public static KarduinoManagement createInstance(Knx knx) {
        return new KarduinoManagement(knx);
    }

    private final Knx knx;
    private ProgProtocol0x00 protocol;
    private boolean isProgramming = false;
    private String individualAddress;

    /**
     * Dont' use this constructor directly. Use {@link Knx#createKarduinoManagement()
     * } instead.
     *
     * @param netlink
     * @throws KNXLinkClosedException
     */
    KarduinoManagement(Knx knx) {
        this.knx = knx;
        protocol = ProgProtocol0x00.getInstance(knx);
    }
    
    public boolean writeIndividualAddress(String individualAddress) throws KnxException {
        return protocol.writeIndividualAddress(individualAddress);
    }
    
    public List<String> readIndividualAddress(boolean oneAddressOnly) throws KnxException {
        return protocol.readIndividualAddress(oneAddressOnly);
    }

    /**
     * Starts programming existing device with given address
     * @param individualAddress 
     * @param manufacturerId 
     * @param deviceId 
     * @param revisionId 
     * @throws de.root1.slicknx.KnxException 
     */
    public void startProgramming(String individualAddress, byte manufacturerId, byte deviceId, byte revisionId) throws KnxException {
        if (isProgramming) {
            throw new IllegalStateException("Already in programming mode. Please call stopProgramming() first.");
        }
        
        // set prog mode based on pa
        protocol.writeProgrammingMode(individualAddress, true);
        
        // check for single device in prog mode (uses ReadPro
        boolean cont = protocol.onlyOneDeviceInProgMode();
        
        if (!cont) {
            throw new KnxException("It seems that more than one device is in programming-mode.");
        }
        DeviceInfo di = protocol.readDeviceInfo(individualAddress);
        
        // check for correct device
        if (di.getManufacturerId()!=manufacturerId || di.getDeviceId()!=deviceId || di.getRevisionId()!=revisionId) {
            throw new KnxException("Device does not match.\n"
                + " KARDUINO reported: \n"
                + "  manufacturer: "+di.getManufacturerId()+"\n"
                + "  device: "+di.getDeviceId()+"\n"
                + "  revision: "+di.getRevisionId()+"\n"
                + " Configuration requires:\n"
                + "  manufacturer: "+manufacturerId+"\n"
                + "  device: "+deviceId+"\n"
                + "  revision: "+revisionId);
        }
        this.individualAddress = individualAddress;
        isProgramming = true;
    }
    
    public void stopProgramming() throws KnxException {
        if (!isProgramming) throw new IllegalStateException("Not in programming-state- Call startProgramming() first.");
        protocol.writeProgrammingMode(individualAddress, false);
        protocol.restart(individualAddress);
        isProgramming = false;
    }
    
    public void writeParameter(byte id, byte[] data) throws KnxException {
        if (!isProgramming) throw new IllegalStateException("Not in programming-state- Call startProgramming() first.");
        protocol.writeParameter(id, data);
    }
    
    public void writeComObject(List<ComObject> list) throws KnxException {
        if (!isProgramming) throw new IllegalStateException("Not in programming-state- Call startProgramming() first.");
        protocol.writeComObject(list);
    }
    
    public void restart(String address) throws KnxException {
        protocol.restart(address);
    }

}
