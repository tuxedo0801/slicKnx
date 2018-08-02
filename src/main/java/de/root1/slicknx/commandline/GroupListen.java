/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.root1.slicknx.commandline;

import de.root1.slicknx.GroupAddressEvent;
import de.root1.slicknx.GroupAddressListener;
import de.root1.slicknx.Knx;
import de.root1.slicknx.KnxException;
import de.root1.slicknx.Utils;
import java.util.concurrent.Semaphore;

/**
 *
 * @author achristian
 */
public class GroupListen {
    
    public static void main(String[] args) throws KnxException, InterruptedException {
        
//        args = new String[]{"5/0/99"};
        
        if (args.length<1) {
            printHelp();
            System.exit(1);
        }
        
        final String address = args[0];
        
        final Knx knx = new Knx();
        
        
        knx.addGroupAddressListener(address, new GroupAddressListener() {
            @Override
            public void readRequest(GroupAddressEvent event) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void readResponse(GroupAddressEvent event) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void write(GroupAddressEvent event) {
                byte[] data = event.getData();
                System.out.println(address +": "+Utils.bytesToHex(data).toUpperCase());
                knx.removeGroupAddressListener(address, this);
                knx.close();
                System.exit(0);
            }
        });
        while(true) {
            Thread.sleep(1000);
        }
        
    }

    private static void printHelp() {
        System.out.println("");
        System.out.println(" slicKnx: GroupListen, using Routing-Mode");
        System.out.println("");
        System.out.println(" Arguments:");
        System.out.println("   <source> <destination> <data[]>");
        System.out.println("");
        System.out.println("");
        System.out.println(" Example:");
        System.out.println("   3/6/100");
        System.out.println("");
        System.out.println("   --> Blocks until telegram is received, prints something like (address, followed by raw hex value):     3/6/100: FE10");
        System.out.println("");
    }
    
}
