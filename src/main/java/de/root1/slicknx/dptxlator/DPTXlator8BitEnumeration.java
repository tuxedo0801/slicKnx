/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.root1.slicknx.dptxlator;

import java.util.HashMap;
import java.util.Map;
import tuwien.auto.calimero.dptxlator.DPT;
import tuwien.auto.calimero.dptxlator.DPTXlator;

import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.log.LogLevel;

/**
 * Translator for KNX DPTs with main number 20, type <b>8 Bit Enumeration
 * Value</b>.
 */
public class DPTXlator8BitEnumeration extends DPTXlator {

    /**
     * DPT ID 5.001, Scaling; values from <b>0</b> to <b>100</b> %.
     * <p>
     */
    public static final DPT DPT_FUELTYPE = new DPT("20.100", "FuelType", "0", "3", null);

    public static final DPT DPT_BURNERTYPE = new DPT("20.101", "BurnerType", "0", "3", null);

    public static final DPT DPT_HVACMODE = new DPT("20.102", "HVAC Mode", "0", "4", null);
    public static final DPT DPT_DHWMODE = new DPT("20.102", "DHW Mode", "0", "4", null);

    private static final Map types;

    static {
        types = new HashMap();
        types.put(DPT_FUELTYPE.getID(), DPT_FUELTYPE);
        types.put(DPT_BURNERTYPE.getID(), DPT_BURNERTYPE);
        types.put(DPT_HVACMODE.getID(), DPT_HVACMODE);
        types.put(DPT_DHWMODE.getID(), DPT_DHWMODE);
    }

    /**
     * Creates a translator for the given datapoint type.
     * <p>
     *
     * @param dpt the requested datapoint type
     * @throws KNXFormatException on not supported or not available DPT
     */
    public DPTXlator8BitEnumeration(final DPT dpt) throws KNXFormatException {
        this(dpt.getID());
    }

    /**
     * Creates a translator for the given datapoint type ID.
     * <p>
     *
     * @param dptID available implemented datapoint type ID
     * @throws KNXFormatException on wrong formatted or not expected (available)
     * <code>dptID</code>
     */
    public DPTXlator8BitEnumeration(final String dptID) throws KNXFormatException {
        super(1);
        setSubType(dptID);
    }

    /* (non-Javadoc)
     * @see tuwien.auto.calimero.dptxlator.DPTXlator#getValue()
     */
    public String getValue() {
        return makeString(0);
    }

    /**
     * Sets one new translation item
     */
    public final void setValue(final int value) throws KNXFormatException {
        data = new short[]{(short) (value & 0xff)};
    }

    /**
     * Returns the first translation item, the value scaled conforming to the
     * range of the set DPT.
     *
     * @return scaled numeric value
     * @see tuwien.auto.calimero.dptxlator.DPTXlator#getNumericValue()
     * @see #getValueUnsigned()
     */
    public final double getNumericValue() {
        return fromDPT(data[0]);
    }

    /**
     * Returns the first translation item without any scaling.
     * <p>
     * The returned value is the raw KNX data value (0..255), not adjusted to
     * the value range of the set DPT.
     *
     * @return unscaled representation as unsigned byte
     */
    public final short getValueUnscaled() {
        return data[0];
    }

    /* (non-Javadoc)
     * @see tuwien.auto.calimero.dptxlator.DPTXlator#getAllValues()
     */
    public String[] getAllValues() {
        final String[] s = new String[data.length];
        for (int i = 0; i < data.length; ++i) {
            s[i] = makeString(i);
        }
        return s;
    }

    /* (non-Javadoc)
     * @see tuwien.auto.calimero.dptxlator.DPTXlator#getSubTypes()
     */
    public final Map getSubTypes() {
        return types;
    }

    /**
     * @return the subtypes of the 8 Bit unsigned translator type
     * @see DPTXlator#getSubTypesStatic()
     */
    protected static Map getSubTypesStatic() {
        return types;
    }

    /**
     * Sets a new subtype to use for translating items.
     * <p>
     * The translator is reset into default state, all currently contained items
     * are removed (default value is set).
     *
     * @param dptID new subtype ID to set
     * @throws KNXFormatException on wrong formatted or not expected (available)
     * <code>dptID</code>
     */
    private void setSubType(final String dptID) throws KNXFormatException {
        setTypeID(types, dptID);
        data = new short[1];
    }

    private short fromDPT(final short data) {
        short value = data;
        if (dpt.equals(DPT_FUELTYPE)) {
            value = (short) Math.round(data * 100.0f / 255);
        } else if (dpt.equals(DPT_BURNERTYPE)) {
            value = (short) Math.round(data * 360.0f / 255);
        }
        return value;
    }

    private String makeString(final int index) {
        return appendUnit(Short.toString(fromDPT(data[index])));
    }

    protected void toDPT(final String value, final short[] dst, final int index)
        throws KNXFormatException {
        try {
            dst[index] = toDPT(Short.decode(removeUnit(value)));
        } catch (final NumberFormatException e) {
            logThrow(LogLevel.WARN, "wrong value format " + value, null, value);
        }
    }

    private short toDPT(final int value) throws KNXFormatException {
        try {
            if (value < 0 || value > Integer.parseInt(dpt.getUpperValue())) {
                logThrow(LogLevel.WARN, "translation error for " + value,
                    "input value out of range [" + dpt.getLowerValue() + ".."
                    + dpt.getUpperValue() + "]", Integer.toString(value));
            }
        } catch (final NumberFormatException e) {
            logThrow(LogLevel.ERROR, "parsing " + dpt, null, dpt.getUpperValue());
        }
        int convert = value;
        return (short) convert;
    }

    final String appendUnit(final String value) {
        if (appendUnit) {
            return value + " " + dpt.getUnit();
        }
        return value;
    }

    /**
     * Returns value with unit cut off at end of string, if current DPT has a
     * unit specified.
     * <p>
     * Whitespace are removed from both ends.
     *
     * @param value value string representation
     * @return trimmed value string without unit
     */
    final String removeUnit(final String value) {
        final int i;
        if (dpt.getUnit().length() > 0 && (i = value.lastIndexOf(dpt.getUnit())) > -1) {
            return value.substring(0, i).trim();
        }
        // java number parsing routines are really picky, remove WS
        return value.trim();
    }

    /**
     * Helper which logs message and creates a format exception.
     * <p>
     * Adds the current dpt ID as prefix to log output.
     *
     * @param level log level
     * @param msg log output, exception message if <code>excMsg</code> is
     * <code>null</code>
     * @param excMsg exception message, if <code>null</code> <code>msg</code> is
     * used
     * @param item item in KNXFormatException, might be <code>null</code>
     * @throws KNXFormatException the created format exception
     */
    final void logThrow(final LogLevel level, final String msg, final String excMsg,
        final String item) throws KNXFormatException {
        final KNXFormatException e = new KNXFormatException(excMsg != null ? excMsg : msg, item);
        logger.log(level, dpt.getID() + " - " + msg, excMsg != null ? e : null);
        throw e;
    }
}
