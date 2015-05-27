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
        
        args = new String[]{"1.1.128", "3/6/100", "02", "58"};
        
        if (args.length<3) {
            printHelp();
        }
        
        String source = args[0];
        String destination = args[1];
        
        byte[] data = new byte[args.length-2];
        
        for(int i=2;i<args.length;i++) {
            data[i-2] = Byte.parseByte(args[i], 16);
        }
        
        Knx knx = new Knx(source);
        knx.writeRaw(false, destination, data);
    }

    private static void printHelp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
