/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.root1.slicknx;

import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 *
 * @author achristian
 */
public interface AutoDiscoverProgressListener {
    
    public void onProgress(int i, int max, NetworkInterface iface, InetAddress address);
    public void done(NetworkInterface ni, String individualAddress, String name, String knxMediumString, InetAddress mcast, String macAddressString);
    public void noResult();
    
}
