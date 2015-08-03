/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.root1.slicknx.test;

import de.root1.slicknx.karduino.KarduinoManagement;
import de.root1.slicknx.Knx;
import de.root1.slicknx.KnxException;
import de.root1.slicknx.Utils;
import java.net.InetAddress;
import java.net.UnknownHostException;
import tuwien.auto.calimero.exception.KNXException;

/**
 *
 * @author achristian
 */
public class TestProgram {
    
    public static void main(String[] args) throws KNXException, InterruptedException, UnknownHostException, KnxException {
        
        
        long start = System.currentTimeMillis();
        
        Knx knx = new Knx("1.1.250");
//        Knx knx = new Knx("1.1.250", InetAddress.getByName("192.168.200.56"));
        
        KarduinoManagement karduino = knx.createKarduinoManagement();
        karduino.setTargetDevice("1.1.251");
        
//        System.out.println("Reading property ...");
//        boolean writeAddress = deviceManagement.writeAddress("1.1.252");
        
//        karduino.setTargetDevice("1.1.251");
//        karduino.authorize();
//        karduino.releaseTargetDevice();
        
//        byte[] readProperty = karduino.readProperty(0, 56, 1,1);
//        byte[] readProperty = karduino.readProperty(0, 11, 1,1);
//        System.out.println(Utils.bytesToHex(readProperty));
        
//        karduino.readMem2("1.1.251", 0, 1);
//        karduino.releaseTargetDevice();
//        System.out.println("Reading property *DONE* ");
        
//        karduino.setTargetDevice("1.1.251");
        
//        System.out.println("Writing mem");
//        karduino.writeMem(0xC0, new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09});
//        System.out.println("Writing mem *done*");
//        
//        
//        System.out.println("Writing mem");
//        karduino.writeMem(0xD0, new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09});
//        System.out.println("Writing mem *done*");
        
        
        byte[] readMem;
        
        int count = 100;
        
        for(int i=0;i<count;i++) {
            System.out.println("\n\nReading mem: "+i);
            readMem = karduino.readMem(0x0190+i, 10);
            System.out.println(Utils.bytesToHex(readMem));
            System.out.println("Reading mem *done*");
            
//            System.out.println("Writing mem: "+i);
//        karduino.writeMem(0x0190+i, new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09});
//        System.out.println("Writing mem *done*");
            
//            Thread.sleep(300);
        }
        
        
//        System.out.println("Reading mem");
//        readMem = karduino.readMem(0xF0, 10);
//        System.out.println(Utils.bytesToHex(readMem));
//        System.out.println("Reading mem *done*");
        
        
        karduino.releaseTargetDevice();
        
        long end = System.currentTimeMillis();
        
        System.out.println("Time: "+(end-start)+"ms");
        
    }
    
}
