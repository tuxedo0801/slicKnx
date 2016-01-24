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
public class TestListenerOwnTelegrams {
    
    public static void main(String[] args) throws KnxException, InterruptedException {
        Knx knx = new Knx("1.0.234");
        knx.setLoopbackMode(true);
        
        
        knx.addGroupAddressListener("15/7/255", new GroupAddressListener() {
            @Override
            public void readRequest(GroupAddressEvent event) {
            }

            @Override
            public void readResponse(GroupAddressEvent event) {
            }

            @Override
            public void write(GroupAddressEvent event) {
                System.out.println("Write from "+event.getSource());
            }
        });
        
        knx.writeBoolean(false, "15/7/255", true);
        
//        long start = System.currentTimeMillis();
//        while((System.currentTimeMillis()-start)<5000) {
//            Thread.currentThread().sleep(500);
//            knx.writeBoolean(false, "15/7/255", true);
//        }
        while(true) {
            Thread.currentThread().sleep(1000);
            knx.writeBoolean(false, "15/7/255", true);
        }
//        System.out.println("Thread terminated");
    }
    
}
