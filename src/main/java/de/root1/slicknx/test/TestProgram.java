/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.root1.slicknx.test;

import de.root1.slicknx.KarduinoManagement;
import de.root1.slicknx.Knx;
import de.root1.slicknx.KnxException;
import java.net.UnknownHostException;
import tuwien.auto.calimero.exception.KNXException;

/**
 *
 * @author achristian
 */
public class TestProgram {
    
    public static void main(String[] args) throws KNXException, InterruptedException, UnknownHostException, KnxException {
        Knx knx = new Knx("1.1.250");
        KarduinoManagement deviceManagement = knx.getKarduinoManagement();
        
        System.out.println("Press program button ...");
        boolean writeAddress = deviceManagement.writeAddress("1.1.252");
        System.out.println("done: "+writeAddress);
        
    }
    
}
