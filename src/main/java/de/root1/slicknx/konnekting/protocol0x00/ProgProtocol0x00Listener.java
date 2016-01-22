/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.root1.slicknx.konnekting.protocol0x00;

import de.root1.slicknx.GroupAddressEvent;
import de.root1.slicknx.GroupAddressListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author achristian
 */
public class ProgProtocol0x00Listener implements GroupAddressListener {

    private final Logger log = LoggerFactory.getLogger(ProgProtocol0x00Listener.class);

    @Override
    public void readRequest(GroupAddressEvent event) {
        log.warn("Illegal read request on 15/7/255 found from " + event.getSource());
    }

    @Override
    public void readResponse(GroupAddressEvent event) {
        log.warn("Illegal read response on 15/7/255 found from " + event.getSource());
    }

    public String msgToHex(byte[] data) {
        return String.format("%02x %02x %02x %02x %02x %02x %02x %02x %02x %02x %02x %02x %02x %02x",
            data[0],
            data[1],
            data[2],
            data[3],
            data[4],
            data[5],
            data[6],
            data[7],
            data[8],
            data[9],
            data[10],
            data[11],
            data[12],
            data[13]);
    }

    @Override
    public void write(GroupAddressEvent event) {
        
        StringBuilder sb = new StringBuilder();
        
        String line = "{}: {}";
        
        
        byte[] data = event.getData();

        byte type = data[1];
        ProgMessage msg = null;

        switch (type) {
            
            case ProgProtocol0x00.MSGTYPE_ACK:
                msg = new MsgAck(data);
                break;
            
            case ProgProtocol0x00.MSGTYPE_RESTART:
                msg = new MsgRestart(data);
                break;
                
            case ProgProtocol0x00.MSGTYPE_READ_DEVICE_INFO:
//                msg = new MsgReadDeviceInfo(data);
                break;
                
            case ProgProtocol0x00.MSGTYPE_ANSWER_DEVICE_INFO:
                msg = new MsgAnswerDeviceInfo(data);
                break;

            case ProgProtocol0x00.MSGTYPE_WRITE_PROGRAMMING_MODE:
//                msg = new MsgWriteProgrammingMode(data);
                break;
                
            case ProgProtocol0x00.MSGTYPE_READ_PROGRAMMING_MODE:
//                msg = new MsgReadProgrammingMode(data);
                break;
                
            case ProgProtocol0x00.MSGTYPE_ANSWER_PROGRAMMING_MODE:
                msg = new MsgAnswerProgrammingMode(data);
                break;

            case ProgProtocol0x00.MSGTYPE_WRITE_INDIVIDUAL_ADDRESS:
//                msg = new MsgWriteIndividualAddress(data);
                break;
                
            case ProgProtocol0x00.MSGTYPE_READ_INDIVIDUAL_ADDRESS:
//                msg = new MsgReadIndividualAddress(data);
                break;
                
            case ProgProtocol0x00.MSGTYPE_ANSWER_INDIVIDUAL_ADDRESS:
                msg = new MsgAnswerIndividualAddress(data);
                break;

            case ProgProtocol0x00.MSGTYPE_WRITE_PARAMETER:
//                msg = new MsgWriteParameter(data);
                break;
//                
            case ProgProtocol0x00.MSGTYPE_READ_PARAMETER:
//                msg = new MsgReadParameter(data);
                break;
                
            case ProgProtocol0x00.MSGTYPE_ANSWER_PARAMETER:
                msg = new MsgAnswerParameter(data);
                break;

            case ProgProtocol0x00.MSGTYPE_WRITE_COM_OBJECT:
//                msg = new MsgWriteComObject(data);
                break;
                
            case ProgProtocol0x00.MSGTYPE_READ_COM_OBJECT:
//                msg = new MsgReadComObject(data);
                break;
                
            case ProgProtocol0x00.MSGTYPE_ANSWER_COM_OBJECT:
                msg = new MsgAnswerComObject(data);
                break;
                
            default:
                log.error("Unsupported message detected: {}",msgToHex(data));

        }
        
        if (msg!=null) {
            log.info(line, event.getSource(), msg.toString());
        } else {
            log.info(line, event.getSource(), msgToHex(data));
        }
    }

}
