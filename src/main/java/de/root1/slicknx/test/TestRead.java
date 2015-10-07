/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.root1.slicknx.test;

import de.root1.slicknx.Knx;
import de.root1.slicknx.KnxException;

/**
 *
 * @author achristian
 */
public class TestRead {
    
    public static void main(String[] args) throws KnxException {
        Knx knx = new Knx("1.1.203");
        
        System.out.println("Read: "+knx.readRawAsString("3/1/130"));
    }
    
}
