/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.root1.slicknx;

import java.util.logging.Level;
import java.util.logging.Logger;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.IndividualAddress;
import tuwien.auto.calimero.KNXFormatException;

/**
 *
 * @author achristian
 */
public class Utils {
    
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    public static IndividualAddress getIndividualAddress(String address) throws KnxException {
        try {
            IndividualAddress ia = new IndividualAddress(address);
            return ia;
        } catch (KNXFormatException ex) {
            throw new KnxException("Individual address format not correct.", ex);
        }
    }
    
    public static IndividualAddress getIndividualAddress(byte hi, byte lo) throws KnxException {
        try {
            IndividualAddress ia = new IndividualAddress(new byte[]{hi,lo});
            return ia;
        } catch (Exception ex) {
            throw new KnxException("Individual address error.", ex);
        }
    }
    
    public static GroupAddress getGroupAddress(String address) throws KnxException {
        try {
            GroupAddress ga = new GroupAddress(address);
            return ga;
        } catch (KNXFormatException ex) {
            throw new KnxException("Group address format not correct.", ex);
        } 
    }
    
    public static GroupAddress getGroupAddress(byte hi, byte lo) throws KnxException {
        try {
            GroupAddress ga = new GroupAddress(new byte[]{hi,lo});
            return ga;
        } catch (Exception ex) {
            throw new KnxException("Group address error.", ex);
        }
    }
    
}
