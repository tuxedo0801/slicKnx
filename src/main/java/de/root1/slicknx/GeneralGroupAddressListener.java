/*
 * Copyright (C) 2015 Alexander Christian <alex(at)root1.de>. All rights reserved.
 * 
 * This file is part of slicKnx.
 *
 *   slicKnx is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   slicKnx is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with slicKnx.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.root1.slicknx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import tuwien.auto.calimero.DetachEvent;
import tuwien.auto.calimero.process.ProcessEvent;
import tuwien.auto.calimero.process.ProcessListener;

/**
 *
 * @author achristian
 */
public class GeneralGroupAddressListener implements ProcessListener {

    private final Map<String, List<GroupAddressListener>> listeners;
    private GroupAddressListener globalGroupAddressListener;

    GeneralGroupAddressListener(GroupAddressListener globalGroupAddressListener, Map<String, List<GroupAddressListener>> listeners) {
        this.listeners = listeners;
        this.globalGroupAddressListener = globalGroupAddressListener;
    }

    private void convertAndForward(ProcessEvent e) {
        try {
            // convert
            String destination = e.getDestination().toString();
            GroupAddressEvent.Type type = GroupAddressEvent.Type.UNDEFINED;
            switch (e.getServiceCode()) {
                case /* GROUP_READ */ 0x0:
                    type = GroupAddressEvent.Type.GROUP_READ;
                    break;
                case /* GROUP_RESPONSE */ 0x40:
                    type = GroupAddressEvent.Type.GROUP_RESPONSE;
                    break;
                case /* GROUP_WRITE */ 0x80:
                    type = GroupAddressEvent.Type.GROUP_WRITE;
                    break;
            }

            GroupAddressEvent gae = new GroupAddressEvent(e.getSourceAddr().toString(), destination, type, e.getASDU());

            if (globalGroupAddressListener != null) {
                switch (gae.getType()) {
                    case GROUP_READ:
                        globalGroupAddressListener.readRequest(gae);
                        break;
                    case GROUP_RESPONSE:
                        globalGroupAddressListener.readResponse(gae);
                        break;
                    case GROUP_WRITE:
                        globalGroupAddressListener.write(gae);
                        break;
                }
            }
            // forward
            synchronized (listeners) {
                List<GroupAddressListener> l = listeners.get(destination);
                if (l != null) {

                    List<GroupAddressListener> list = new ArrayList<>(l);
//                    System.out.println("Forward " + gae + " to " + list.size() + " listeners");
                    for (GroupAddressListener listener : list) {
                        switch (gae.getType()) {
                            case GROUP_READ:
                                listener.readRequest(gae);
                                break;
                            case GROUP_RESPONSE:
                                listener.readResponse(gae);
                                break;
                            case GROUP_WRITE:
                                listener.write(gae);
                                break;
                        }
                    }
                }

                List<GroupAddressListener> w = listeners.get("*");

                if (w != null) {
                    List<GroupAddressListener> wildcardlist = new ArrayList<>(w);
//                    System.out.println("Forward " + gae + " to " + list.size() + " listeners");
                    for (GroupAddressListener listener : wildcardlist) {
                        switch (gae.getType()) {
                            case GROUP_READ:
                                listener.readRequest(gae);
                                break;
                            case GROUP_RESPONSE:
                                listener.readResponse(gae);
                                break;
                            case GROUP_WRITE:
                                listener.write(gae);
                                break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void groupReadRequest(ProcessEvent e) {
        convertAndForward(e);
    }

    @Override
    public void groupReadResponse(ProcessEvent e) {
        convertAndForward(e);
    }

    @Override
    public void groupWrite(ProcessEvent e) {
        convertAndForward(e);
    }

    @Override
    public void detached(DetachEvent e) {
        // not of interest
    }

    void setMaster(GroupAddressListener listener) {
        globalGroupAddressListener = listener;
    }

    GroupAddressListener getMaster() {
        return globalGroupAddressListener;
    }

}
