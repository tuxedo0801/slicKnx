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

import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import tuwien.auto.calimero.KNXException;
import tuwien.auto.calimero.KNXFormatException;
import tuwien.auto.calimero.dptxlator.DPTXlator;
import tuwien.auto.calimero.dptxlator.DPTXlator2ByteFloat;
import tuwien.auto.calimero.dptxlator.DPTXlator3BitControlled;
import tuwien.auto.calimero.dptxlator.DPTXlator4ByteFloat;
import tuwien.auto.calimero.dptxlator.DPTXlator8BitSigned;
import tuwien.auto.calimero.dptxlator.DPTXlator8BitUnsigned;
import tuwien.auto.calimero.dptxlator.DPTXlatorBoolean;
import tuwien.auto.calimero.dptxlator.DPTXlatorString;
import tuwien.auto.calimero.dptxlator.TranslatorTypes;
import tuwien.auto.calimero.process.ProcessCommunicationBase;

/**
 *
 * @author achristian
 */
public class GroupAddressEvent {

  private final String source;
  private final String destination;
  private final byte[] data;

  public enum Type {

    UNDEFINED, GROUP_READ, GROUP_RESPONSE, GROUP_WRITE
  };

  private final Type type;

  GroupAddressEvent(String source, String destination, Type type, byte[] data) {
    this.source = source;
    this.destination = destination;
    this.type = type;
    this.data = data;
  }

  /**
   * Gets the source of the group address event.
   *
   * @return indiviudual address of event source, format "x.y.z"
   */
  public String getSource() {
    return source;
  }

  /**
   * Gets the destination of the event
   *
   * @return group address, format "x/y/z"
   */
  public String getDestination() {
    return destination;
  }

  /**
   * Returns the raw data that is sent to the group adress
   *
   * @return application layer service data unit (ASDU)
   */
  public byte[] getData() {
    return data;
  }

  /**
   * The type of the event. See GroupAddressEvent#Type
   *
   * @return
   */
  public Type getType() {
    return type;
  }

  /**
   * Returns the data of the received event as boolean datapoint value.
   * <p>
   *
   * @return the received value of type boolean
   * @throws KnxFormatException on not supported or not available boolean DPT
   */
  public boolean asBool() throws KnxFormatException {
    try {
      final DPTXlatorBoolean t = new DPTXlatorBoolean(DPTXlatorBoolean.DPT_BOOL);
      t.setData(data);
      return t.getValueBoolean();
    } catch (KNXFormatException ex) {
      throw new KnxFormatException(ex);
    }
  }

  /**
   * Returns the data of the received event as 8 Bit signed, unscaled value DPT
   * 5.005 DPT 5.010
   *
   * @return 8 bit value 0..255
   * @throws KnxFormatException
   */
  public int asUnscaled() throws KnxFormatException {
    try {
      final DPTXlator8BitUnsigned t = new DPTXlator8BitUnsigned(ProcessCommunicationBase.UNSCALED);
      t.setData(data);
      return t.getValueUnsigned();
    } catch (KNXFormatException ex) {
      throw new KnxFormatException(ex);
    }
  }

  /**
   * Returns the data of the received event as 8 Bit signed, 0..100 scaled (f.i.
   * %) value. DPT 5.001
   *
   * @return 8 bit value 0..255 mapped to 0..100, f.i. %
   * @throws KnxFormatException
   */
  public int asScaled() throws KnxFormatException {
    try {
      final DPTXlator8BitUnsigned t = new DPTXlator8BitUnsigned(ProcessCommunicationBase.SCALING);
      t.setData(data);
      return t.getValueUnsigned();
    } catch (KNXFormatException ex) {
      throw new KnxFormatException(ex);
    }
  }

  /**
   * Returns the data of the received event as 8 Bit signed, angle scaled value.
   * DPT 5.003
   *
   * @return 8 bit value 0..255 mapped to 0..360°
   * @throws KnxFormatException
   */
  public int asAngle() throws KnxFormatException {
    try {
      final DPTXlator8BitUnsigned t = new DPTXlator8BitUnsigned(ProcessCommunicationBase.ANGLE);
      t.setData(data);
      return t.getValueUnsigned();
    } catch (KNXFormatException ex) {
      throw new KnxFormatException(ex);
    }
  }

  /**
   * Returns the data of the received event as 3 Bit controlled datapoint value.
   * DPT 3.007
   *
   * @return the received value of type 3 Bit controlled
   * @throws KnxFormatException on not supported or not available 3 Bit
   * controlled DPT
   */
  public int asControl() throws KnxFormatException {
    try {
      final DPTXlator3BitControlled t = new DPTXlator3BitControlled(
              DPTXlator3BitControlled.DPT_CONTROL_DIMMING);
      t.setData(data);
      return t.getValueSigned();
    } catch (KNXFormatException ex) {
      throw new KnxFormatException(ex);
    }
  }

  /**
   * Returns the data of the received event as 8bit signed value. DPT 6
   *
   * @return the received value of type integer, containing only 8 bit signed
   * @throws KnxFormatException on not supported or not available float DPT
   */
  public int asDpt6()
          throws KnxFormatException {
    try {
      final DPTXlator8BitSigned t = new DPTXlator8BitSigned(DPTXlator8BitSigned.DPT_VALUE_1_UCOUNT);
      t.setData(data);
      return (int) t.getNumericValue();
    } catch (KNXFormatException ex) {
      throw new KnxFormatException(ex);
    }
  }

  /**
   * Returns the data of the received event as 2-byte KNX float value. DPT
   * 9.xxx, i.e. 9.001 for temperature
   *
   * @return the received value of type float
   * @throws KnxFormatException on not supported or not available float DPT
   */
  public float as2ByteFloat() throws KnxFormatException {
    try {
      final DPTXlator2ByteFloat t = new DPTXlator2ByteFloat(
              DPTXlator2ByteFloat.DPT_RAIN_AMOUNT /* using rainamount to get full 2byte spectrum */);
      t.setData(data);
      return (float) t.getNumericValue();
    } catch (KNXFormatException ex) {
      throw new KnxFormatException(ex);
    }
  }

  /**
   * Returns the data of the received event as 4-byte KNX float value. DPT
   * 14.xxx, i.e. 14.070 for temperature difference
   *
   * @return the received value of type double
   * @throws KnxFormatException on not supported or not available float DPT
   */
  public double as4ByteFloat()
          throws KnxFormatException {
    try {
      final DPTXlator4ByteFloat t = new DPTXlator4ByteFloat(
              DPTXlator4ByteFloat.DPT_TEMPERATURE_DIFFERENCE);
      t.setData(data);
      return t.getValueFloat();
    } catch (KNXFormatException ex) {
      throw new KnxFormatException(ex);
    }
  }

  /**
   * Returns the data of the received event as string value. DPT16.001
   * <p>
   * The used character set is ISO-8859-1 (Latin 1), with an allowed string
   * length of 14 characters.
   *
   * @return the received value of type String
   * @throws KnxFormatException on not supported or not available ISO-8859-1 DPT
   */
  public String asString() throws KnxFormatException {
    try {
      final DPTXlatorString t = new DPTXlatorString(DPTXlatorString.DPT_STRING_8859_1);
      t.setData(data);
      return t.getValue();
    } catch (KNXFormatException ex) {
      throw new KnxFormatException(ex);
    }
  }

  /**
   * Returns the data of the received event as datapoint value of the requested
   * DPT in String representation.
   *
   * @param dptMainNumber datapoint type main number, number >= 0; use 0 to
   * infer translator type from <code>dptID</code> argument only
   * @param dptID datapoint type ID for selecting a particular kind of value
   * translation
   * @return the received value of the requested type as String representation
   * @throws KnxFormatException on not supported or not available DPT
   */
  public String asString(final int dptMainNumber, final String dptID)
          throws KnxFormatException {
    try {
      final DPTXlator t = TranslatorTypes.createTranslator(dptMainNumber, dptID);
      t.setData(data);
      return t.getValue();
    } catch (KNXException ex) {
      throw new KnxFormatException(ex);
    }
  }

  @Override
  public String toString() {
    return "GroupAddressEvent{" + "source=" + source + ", destination=" + destination + ", data=" + Arrays.toString(data) + ", type=" + type + '}';
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 41 * hash + Objects.hashCode(this.source);
    hash = 41 * hash + Objects.hashCode(this.destination);
    hash = 41 * hash + Arrays.hashCode(this.data);
    hash = 41 * hash + Objects.hashCode(this.type);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final GroupAddressEvent other = (GroupAddressEvent) obj;
    if (!Objects.equals(this.source, other.source)) {
      return false;
    }
    if (!Objects.equals(this.destination, other.destination)) {
      return false;
    }
    if (!Arrays.equals(this.data, other.data)) {
      return false;
    }
    if (this.type != other.type) {
      return false;
    }
    return true;
  }

}
