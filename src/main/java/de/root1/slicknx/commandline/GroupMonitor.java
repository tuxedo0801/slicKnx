/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.root1.slicknx.commandline;

import de.root1.slicknx.GroupAddressEvent;
import de.root1.slicknx.GroupAddressListener;
import de.root1.slicknx.Knx;

/**
 *
 * @author achristian
 */
public class GroupMonitor {
    
    public static void main(String[] args) throws InterruptedException {
        Knx knx = new Knx();
        
        knx.setGlobalGroupAddressListener(new GroupAddressListener(){
            
            private String createDataString(byte[] data) {
                String result = "";
                for(int i=0;i<data.length;i++) {
                    int x = data[i] & 0xff;
                    result += String.format("%02x",x);
                    if (i<data.length) {
                        result += " ";
                    }
                }
                return result.toUpperCase();
            }

            @Override
            public void readRequest(GroupAddressEvent event) {
            }

            @Override
            public void readResponse(GroupAddressEvent event) {
            }

            @Override
            public void write(GroupAddressEvent event) {
                System.out.println("Write from "+event.getSource()+" to "+event.getDestination()+": "+createDataString(event.getData()));
            }
        });
        while(true) {
            Thread.sleep(1000);
        }
    }
    
}
