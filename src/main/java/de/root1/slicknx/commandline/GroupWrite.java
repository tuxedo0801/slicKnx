/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.root1.slicknx.commandline;

import de.root1.slicknx.Knx;
import de.root1.slicknx.KnxException;

/**
 *
 * @author achristian
 */
public class GroupWrite {
    
    public static void main(String[] args) throws KnxException, InterruptedException {
        
        args = new String[]{"1.1.128", "5/0/99", "00", "00", "00", "00"};
        
        if (args.length<3) {
            printHelp();
            System.exit(1);
        }
        
        String source = args[0];
        String destination = args[1];
        
        byte[] data = new byte[args.length-2];
        
        for(int i=2;i<args.length;i++) {
            data[i-2] = (byte) (Integer.parseInt(args[i], 16)&0xff);
        }
        
        Knx knx = new Knx(source);
        knx.writeRaw(false, destination, data);
        System.out.println("Done!\n");
        
    }

    private static void printHelp() {
        System.out.println("");
        System.out.println(" slicKnx: GroupWrite, using Routing-Mode");
        System.out.println("");
        System.out.println(" Arguments:");
        System.out.println("   <source> <destination> <data[]>");
        System.out.println("");
        System.out.println("");
        System.out.println(" Example:");
        System.out.println("   1.1.128 3/6/100 02 58");
        System.out.println("");
        System.out.println("   --> Sending from 1.1.128 to 3/6/100 two bytes (unsigned hex value only!): 02 58");
        System.out.println("");
    }
    
}
