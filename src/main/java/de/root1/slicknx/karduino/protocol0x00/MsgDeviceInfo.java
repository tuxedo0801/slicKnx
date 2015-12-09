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
package de.root1.slicknx.karduino.protocol0x00;

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
     * Manufacturer-ID, 2 bytes value
     * @return 
     */
    public short getManufacturerId() {
        int hi = message[2];
        int lo = message[3];
        return (short) ((hi<<8) + (lo<<0));
    }

    /**
     * Device ID, 1 byte value
     * @return 
     */
    public byte getDeviceId() {
        return message[3];
    }

    /**
     * Device revision, 1 byte value
     * @return 
     */
    public byte getRevisionId() {
        return message[4];
    }

    public byte getDeviceFlags() {
        return message[5];
    }

    public String getIndividualAddress() throws KnxException {
        return Utils.getIndividualAddress(message[6], message[7]).toString();
    }
    
    
    
}
