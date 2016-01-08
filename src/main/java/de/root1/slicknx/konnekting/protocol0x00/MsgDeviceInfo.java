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
package de.root1.slicknx.konnekting.protocol0x00;

import de.root1.slicknx.KnxException;
import de.root1.slicknx.Utils;

/**
 *
 * @author achristian
 */
class MsgDeviceInfo extends ProgMessage {

    private final byte[] message;
    
    public MsgDeviceInfo(byte[] message) {
        super(message);
        this.message = message;
    }

    /**
     * Manufacturer-ID, 2 bytes value, unsigned
     * @return 
     */
    public int getManufacturerId() {
        byte hi = message[2];
        byte lo = message[3];
        
        return ((hi << 8)&0xffff) + ((lo << 0)&0xff);
    }

    /**
     * Device ID, 1 byte value
     * @return 
     */
    public short getDeviceId() {
        return (short) (message[4]&0xff);
    }

    /**
     * Device revision, 1 byte value
     * @return 
     */
    public short getRevisionId() {
        return (short) (message[5]&0xff);
    }

    public byte getDeviceFlags() {
        return message[6];
    }

    public String getIndividualAddress() throws KnxException {
        return Utils.getIndividualAddress(message[7], message[8]).toString();
    }
    
    
    
    
}