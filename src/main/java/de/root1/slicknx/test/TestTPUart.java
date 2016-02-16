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
package de.root1.slicknx.test;

import de.root1.slicknx.GroupAddressEvent;
import de.root1.slicknx.GroupAddressListener;
import de.root1.slicknx.Knx;
import de.root1.slicknx.KnxException;

/**
 *
 * @author achristian
 */
public class TestTPUart {
    
    public static void main(String[] args) throws KnxException {
        Knx knx = new Knx(Knx.SerialType.TPUART, "/dev/ttyS0");
        
        knx.setGlobalGroupAddressListener(new GroupAddressListener() {

            @Override
            public void readRequest(GroupAddressEvent event) {
                System.out.println("read: "+event);
            }

            @Override
            public void readResponse(GroupAddressEvent event) {
                System.out.println("response: "+event);
            }

            @Override
            public void write(GroupAddressEvent event) {
                System.out.println("write: "+event);
            }
        });
    }
    
}
