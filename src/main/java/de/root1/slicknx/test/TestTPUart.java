/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
        Knx knx = new Knx(Knx.SerialType.TPUART, "/dev/ttyUSB0");
        
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
