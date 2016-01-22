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
package de.root1.slicknx.konnekting.protocol0x00;

import de.root1.slicknx.GroupAddressEvent;
import de.root1.slicknx.GroupAddressListener;
import de.root1.slicknx.Knx;
import de.root1.slicknx.KnxException;
import de.root1.slicknx.Utils;
import de.root1.slicknx.konnekting.ComObject;
import de.root1.slicknx.konnekting.DeviceInfo;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tuwien.auto.calimero.IndividualAddress;

/**
 *
 * @author achristian
 */
public class ProgProtocol0x00 {

    private static final Logger log = LoggerFactory.getLogger(ProgProtocol0x00.class);

    public static ProgProtocol0x00 getInstance(Knx knx) {
        boolean debug = Boolean.getBoolean("de.root1.slicknx.konnekting.debug");
        if (debug) {
            WAIT_TIMEOUT = 5000;
            log.info("###### RUNNING DEBUG MODE #######");
        }
        return new ProgProtocol0x00(knx);
    }

    private final Knx knx;

    private static int WAIT_TIMEOUT = 500; // produktiv: 500ms, debug: 5000ms

    public static final String PROG_GA = "15/7/255";
    public static final byte PROTOCOL_VERSION = 0x00;

    public static final byte MSGTYPE_ACK = 0;
    public static final byte MSGTYPE_READ_DEVICE_INFO = 1;
    public static final byte MSGTYPE_ANSWER_DEVICE_INFO = 2;
    public static final byte MSGTYPE_RESTART = 9;

    public static final byte MSGTYPE_WRITE_PROGRAMMING_MODE = 10;
    public static final byte MSGTYPE_READ_PROGRAMMING_MODE = 11;
    public static final byte MSGTYPE_ANSWER_PROGRAMMING_MODE = 12;

    public static final byte MSGTYPE_WRITE_INDIVIDUAL_ADDRESS = 20;
    public static final byte MSGTYPE_READ_INDIVIDUAL_ADDRESS = 21;
    public static final byte MSGTYPE_ANSWER_INDIVIDUAL_ADDRESS = 22;

    public static final byte MSGTYPE_WRITE_PARAMETER = 30;
    public static final byte MSGTYPE_READ_PARAMETER = 31;
    public static final byte MSGTYPE_ANSWER_PARAMETER = 32;

    public static final byte MSGTYPE_WRITE_COM_OBJECT = 40;
    public static final byte MSGTYPE_READ_COM_OBJECT = 41;
    public static final byte MSGTYPE_ANSWER_COM_OBJECT = 42;

    private static final List<ProgMessage> receivedMessages = new ArrayList<>();

    private final GroupAddressListener gal = new GroupAddressListener() {

        @Override
        public void readRequest(GroupAddressEvent event) {
            // not handled
        }

        @Override
        public void readResponse(GroupAddressEvent event) {
            // not handled
        }

        @Override
        public void write(GroupAddressEvent event) {

            if (event.getDestination().equals(PROG_GA) && event.getData().length == 14 && event.getData()[0] == PROTOCOL_VERSION) {
                // seems to be relevant
                byte[] data = event.getData();
                ProgMessage msg = null;
                byte type = data[1];
                
                log.trace("Received: \n"
                        + "    data[0]={}\n"
                        + "    data[1]={}\n"
                        + "    data[2]={}\n"
                        + "    data[3]={}\n"
                        + "    data[4]={}\n"
                        + "    data[5]={}\n"
                        + "    data[6]={}\n"
                        + "    data[7]={}\n"
                        + "    data[8]={}\n"
                        + "    data[9]={}\n"
                        + "    data[10]={}\n"
                        + "    data[11]={}\n"
                        + "    data[12]={}\n"
                        + "    data[13]={}\n",
                        new Object[]{
                        String.format("%8s", Integer.toBinaryString(data[0]&0xff)).replace(" ", "0"),
                        String.format("%8s", Integer.toBinaryString(data[1]&0xff)).replace(" ", "0"),
                        String.format("%8s", Integer.toBinaryString(data[2]&0xff)).replace(" ", "0"),
                        String.format("%8s", Integer.toBinaryString(data[3]&0xff)).replace(" ", "0"),
                        String.format("%8s", Integer.toBinaryString(data[4]&0xff)).replace(" ", "0"),
                        String.format("%8s", Integer.toBinaryString(data[5]&0xff)).replace(" ", "0"),
                        String.format("%8s", Integer.toBinaryString(data[6]&0xff)).replace(" ", "0"),
                        String.format("%8s", Integer.toBinaryString(data[7]&0xff)).replace(" ", "0"),
                        String.format("%8s", Integer.toBinaryString(data[8]&0xff)).replace(" ", "0"),
                        String.format("%8s", Integer.toBinaryString(data[9]&0xff)).replace(" ", "0"),
                        String.format("%8s", Integer.toBinaryString(data[10]&0xff)).replace(" ", "0"),
                        String.format("%8s", Integer.toBinaryString(data[11]&0xff)).replace(" ", "0"),
                        String.format("%8s", Integer.toBinaryString(data[12]&0xff)).replace(" ", "0"),
                        String.format("%8s", Integer.toBinaryString(data[13]&0xff)).replace(" ", "0")
                        }
                        
                );
                
                switch (type) {
                    case MSGTYPE_ACK:
                        msg = new MsgAck(data);
                        break;
                    case MSGTYPE_ANSWER_DEVICE_INFO:
                        msg = new MsgAnswerDeviceInfo(data);
                        break;
                    case MSGTYPE_ANSWER_INDIVIDUAL_ADDRESS:
                        msg = new MsgAnswerIndividualAddress(data);
                        break;
                    case MSGTYPE_ANSWER_PROGRAMMING_MODE:
                        msg = new MsgAnswerProgrammingMode(data);
                        break;
                    case MSGTYPE_ANSWER_PARAMETER:
                        msg = new MsgAnswerParameter(data);
                        break;
                    case MSGTYPE_ANSWER_COM_OBJECT:
                        msg = new MsgAnswerComObject(data);
                        break;
                }
                synchronized (receivedMessages) {
                    receivedMessages.add(msg);
                    receivedMessages.notifyAll();
                }

            }
        }
    };

    private ProgProtocol0x00(Knx knx) {
        this.knx = knx;
        knx.addGroupAddressListener(PROG_GA, gal);
    }

    /**
     * Wait for messages.
     *
     * @param timeout milliseconds to wait for messages
     * @param returnOnFirstMsg if method should return on first received message
     * @return
     */
    private List<ProgMessage> waitForMessage(int timeout, boolean returnOnFirstMsg) {
        log.debug("Waiting for message. timeout={} returnOnFirst={}", timeout, returnOnFirstMsg);
        long start = System.currentTimeMillis();
        List<ProgMessage> list = null;
        while ((System.currentTimeMillis() - start) < timeout) {
            synchronized (receivedMessages) {
                try {
                    receivedMessages.wait(timeout / 10);
                    if (receivedMessages.size() > 0) {
                        list = new ArrayList<>(receivedMessages);
                        receivedMessages.clear();
                    }
                } catch (InterruptedException ex) {
                }
            }
            // return immediatelly if required
            if (returnOnFirstMsg && list != null) {
                return list;
            }
        }
        if (list==null) {
            list = new ArrayList<>();
        }
        return list;
    }

    private <T extends ProgMessage> T expectSingleMessage(Class<T> msgClass) throws KnxException {
        log.debug("Waiting for single message [{}]", msgClass.getName());
        List<ProgMessage> list = waitForMessage(WAIT_TIMEOUT, true);
        if (list.size() != 1) {
            throw new KnxException("Received " + list.size() + " messages. Expected 1 of type "+msgClass.getName()+". Aborting");
        }
        if (!(list.get(0).getClass().isAssignableFrom(msgClass))) {
            throw new KnxException("Wrong message type received. Expected:" + msgClass + ". Got: " + list.get(0));
        }
        return (T) list.get(0);
    }

    private byte[] createNewMsg(byte type) {
        byte[] data = new byte[14];
        data[0] = PROTOCOL_VERSION;
        data[1] = type;
        for (int i = 2; i < data.length; i++) {
            data[i] = 0x00;
        }
        return data;
    }

    private void sendMessage(byte[] msgData) throws KnxException {
        log.trace("Sending message \n"
            + "ProtocolVersion: {}\n"
            + "MsgTypeId      : {}\n"
            + "data[2..13]    : {} {} {} {} {} {} {} {} {} {} {} {}", new Object[]{
        String.format("%02x", msgData[0]),
        String.format("%02x", msgData[1]),
        String.format("%02x", msgData[2]),
        String.format("%02x", msgData[3]),
        String.format("%02x", msgData[4]),
        String.format("%02x", msgData[5]),
        String.format("%02x", msgData[6]),
        String.format("%02x", msgData[7]),
        String.format("%02x", msgData[8]),
        String.format("%02x", msgData[9]),
        String.format("%02x", msgData[10]),
        String.format("%02x", msgData[11]),
        String.format("%02x", msgData[12]),
        String.format("%02x", msgData[13]),
        
        });
        knx.writeRaw(false, PROG_GA, msgData);
    }

    /**
     *
     * @return true, if exactly one device responds to read-device-info -->
     * @throws KnxException
     */
    public boolean onlyOneDeviceInProgMode() throws KnxException {
        byte[] msgData = createNewMsg(MSGTYPE_READ_PROGRAMMING_MODE);
        sendMessage(msgData);
        List<ProgMessage> waitForMessage = waitForMessage(WAIT_TIMEOUT, false);
        int count = 0;
        
        for (ProgMessage msg : waitForMessage) {// FIXME check also for IA matching
            if (msg.getType() == MSGTYPE_ANSWER_PROGRAMMING_MODE) {
                count++;
            }
        }
        return count == 1;
    }

    /**
     * Reads the individual address of a konnekting device
     * <p>
     * The konnekting device is a device in programming mode. In situations
     * necessary to know whether more than one device is in programming mode,
     * <code>oneAddressOnly</code> is set to <code>false</code> and the device
     * addresses are listed in the returned address array. In this case, the
     * whole response timeout is waited for read responses. If
     * <code>oneAddressOnly</code> is <code>true</code>, the method returns
     * after receiving the first read response.
     *
     * @param oneAddressOnly
     * @return lis of found addresseskk
     * @throws KnxException
     */
    public List<String> readIndividualAddress(boolean oneAddressOnly) throws KnxException {
        List<String> list = new ArrayList<>();
        byte[] msgData = createNewMsg(MSGTYPE_READ_INDIVIDUAL_ADDRESS);
        sendMessage(msgData);
        if (oneAddressOnly) {
            MsgAnswerIndividualAddress expectSingleMessage = expectSingleMessage(MsgAnswerIndividualAddress.class);
            list.add(expectSingleMessage.getAddress());
        } else {
            List<ProgMessage> msgList = waitForMessage(WAIT_TIMEOUT, false);
            for (ProgMessage msg : msgList) {
                if (msg instanceof MsgAnswerIndividualAddress) {
                    MsgAnswerIndividualAddress ia = (MsgAnswerIndividualAddress) msg;
                    list.add(ia.getAddress());
                }
            }
        }
        return list;
    }

    public DeviceInfo readDeviceInfo(String individualAddress) throws KnxException {
        byte[] msgData = createNewMsg(MSGTYPE_READ_DEVICE_INFO);
        System.arraycopy(Utils.getIndividualAddress(individualAddress).toByteArray(), 0, msgData, 2, 2);
        sendMessage(msgData);
        MsgAnswerDeviceInfo msg = expectSingleMessage(MsgAnswerDeviceInfo.class);
        
        return new DeviceInfo(msg.getManufacturerId(), msg.getDeviceId(), msg.getRevisionId(), msg.getDeviceFlags(), msg.getIndividualAddress());
    }

    /**
     * Writes address to device which is in programming mode
     *
     * @param address address to write to device
     * @return true if setting address succeeded, false if not
     * @throws KnxException if f.i. a timeout occurs or more than one device is
     * in programming mode
     */
    public void writeIndividualAddress(String address) throws KnxException {
        boolean exists = false;

        try {
            readDeviceInfo(address);
            exists = true;
            log.debug("Device with {} exists",address);
        } catch (KnxException ex) {

        }

        boolean setAddr = false;
        int attempts = 20;
        int count = 0;

        while (count != 1 && attempts-- > 0) {
            try {
                // gibt nur Antwort von Geräten im ProgMode
                List<String> list = readIndividualAddress(false);
                count = list.size();
                
                
                if (count ==1 && !list.get(0).equals(address)) {
                    setAddr = true;
                } else if (count == 1 && list.get(0).equals(address)) {
                    log.debug("One device responded, but already has {}.", address);
                }
            } catch (KnxException ex) {
                if (exists) {
                    log.warn("device exists but is not in programming mode, cancel writing address");
                    throw new KnxException("device exists but is not in programming mode, cancel writing address");
                }
            }
            log.debug("KONNEKTINGs in programming mode: {}", count);
        }
        if (!setAddr) {
            log.warn("Will not set address. Too much devices in prog-mode or wrong device in prog mode, or device to program has already same address");
            throw new KnxException("Will not set address. Too much devices in prog-mode or wrong device in prog mode, or device to program has already same address");
        }
        log.debug("Writing address ...");
        byte[] msgData = createNewMsg(MSGTYPE_WRITE_INDIVIDUAL_ADDRESS);

        // insert address
        IndividualAddress ia = Utils.getIndividualAddress(address);
        System.arraycopy(ia.toByteArray(), 0, msgData, 2, 2);

        sendMessage(msgData);
        expectSingleMessage(MsgAck.class);
    }

    public void writeParameter(byte id, byte[] data) throws KnxException {
        if (data.length > 11) {
            throw new IllegalArgumentException("Data must not exceed 11 bytes.");
        }
        byte[] msgData = createNewMsg(MSGTYPE_WRITE_PARAMETER);
        msgData[2] = id;
        System.arraycopy(data, 0, msgData, 3, data.length);
        sendMessage(msgData);
        expectSingleMessage(MsgAck.class);
    }

    public byte[] readParameter(byte id) throws KnxException {
        byte[] msgData = createNewMsg(MSGTYPE_READ_PARAMETER);
        msgData[2] = id;
        sendMessage(msgData);
        MsgAnswerParameter parameter = expectSingleMessage(MsgAnswerParameter.class);
        return parameter.getParamValue();
    }

    public void writeComObject(List<ComObject> list) throws KnxException {

        for (int i = 0; i < list.size(); i += 3) {

            ComObject co1 = list.get(i);
            ComObject co2 = null;
            ComObject co3 = null;
            int num = 1;

            if (i + 1 < list.size()) {
                co2 = list.get(i + 1);
                num = 2;
            }
            if (i + 2 < list.size()) {
                co3 = list.get(i + 2);
                num = 3;
            }

            byte[] msgData = createNewMsg(MSGTYPE_WRITE_COM_OBJECT);
            msgData[2] = (byte) (num & 0xFF);

            msgData[3] = co1.getId();
            System.arraycopy(Utils.getGroupAddress(co1.getGroupAddress()).toByteArray(), 0, msgData, 4, 2);

            if (co2 != null) {
                msgData[6] = co2.getId();
                System.arraycopy(Utils.getGroupAddress(co2.getGroupAddress()).toByteArray(), 0, msgData, 7, 2);
            }

            if (co3 != null) {
                msgData[9] = co3.getId();
                System.arraycopy(Utils.getGroupAddress(co3.getGroupAddress()).toByteArray(), 0, msgData, 10, 2);
            }

            sendMessage(msgData);
            expectSingleMessage(MsgAck.class);

        }

    }

    public List<ComObject> readComObject(List<Byte> ids) throws KnxException {

        List<ComObject> list = new ArrayList<>();

        for (int i = 0; i < ids.size(); i += 3) {

            Byte id1 = ids.get(i);
            Byte id2 = null;
            Byte id3 = null;
            int num = 1;

            if (i + 1 < list.size()) {
                id2 = ids.get(i + 1);
                num = 2;
            }
            if (i + 2 < list.size()) {
                id3 = ids.get(i + 2);
                num = 3;
            }

            byte[] msgData = createNewMsg(MSGTYPE_READ_COM_OBJECT);
            msgData[2] = (byte) (num & 0xFF);

            msgData[3] = id1;

            if (id2 != null) {
                msgData[4] = id2;
            }

            if (id3 != null) {
                msgData[5] = id1;
            }

            sendMessage(msgData);
            MsgAnswerComObject comObj = expectSingleMessage(MsgAnswerComObject.class);
            list.addAll(comObj.getComObjects());

        }
        return list;
    }

    public void writeProgrammingMode(String individualAddress, boolean progMode) throws KnxException {
        byte[] msgData = createNewMsg(MSGTYPE_WRITE_PROGRAMMING_MODE);
        System.arraycopy(Utils.getIndividualAddress(individualAddress).toByteArray(), 0, msgData, 2, 2);
        msgData[4] = (byte) (progMode ? 0x01 : 0x00);
        sendMessage(msgData);
        expectSingleMessage(MsgAck.class);
    }

    public void restart(String individualAddress) throws KnxException {
        byte[] msgData = createNewMsg(MSGTYPE_RESTART);
        System.arraycopy(Utils.getIndividualAddress(individualAddress).toByteArray(), 0, msgData, 2, 2);
        sendMessage(msgData);
//        expectSingleMessage(MsgAck.class);
    }

}
