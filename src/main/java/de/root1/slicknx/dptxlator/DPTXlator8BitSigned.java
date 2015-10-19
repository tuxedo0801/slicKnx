package de.root1.slicknx.dptxlator;

import java.util.HashMap;
import java.util.Map;
import tuwien.auto.calimero.dptxlator.DPT;
import tuwien.auto.calimero.dptxlator.DPTXlator;

import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.exception.KNXIllegalArgumentException;
import tuwien.auto.calimero.log.LogLevel;

/**
 * Translator for KNX DPTs with main number 6, type <b>8 Bit signed value</b>.
 * <p>
 * The KNX data type width is 1 byte.<br>
 * The default return value after creation is 0.<br>
 *
 */
public class DPTXlator8BitSigned extends DPTXlator {

    /**
     * DPT ID 6.001, Percent 8 Bit; values from <b>-128</b> to <b>127</b> %.
     * <p>
     */
    public static final DPT DPT_PERCENT_V8 = new DPT("6.001", "Percent (8 Bit)", "-128", "127", "%");

    /**
     * DPT ID 6.010, Value 1 signed count; values from <b>-128</b> to <b>127</b>
     * counter pulses.
     * <p>
     */
    public static final DPT DPT_VALUE_1_UCOUNT = new DPT("6.010", "signed count", "-128", "127",
            "counter pulses");

//    /**
//     * DPT ID 6.020, Status with Mode; 
//     * <p>
//     */
//    public static final DPT DPT_STATUS_MODE3 = new DPT("6.020", "signed count", "-128", "127",
//            "counter pulses");
    private static final Map types;

    static {
        types = new HashMap();
        types.put(DPT_PERCENT_V8.getID(), DPT_PERCENT_V8);
        types.put(DPT_VALUE_1_UCOUNT.getID(), DPT_VALUE_1_UCOUNT);
//        types.put(DPT_STATUS_MODE3.getID(), DPT_STATUS_MODE3);
    }

    /**
     * Creates a translator for the given datapoint type.
     * <p>
     *
     * @param dpt the requested datapoint type
     * @throws KNXFormatException on not supported or not available DPT
     */
    public DPTXlator8BitSigned(final DPT dpt) throws KNXFormatException {
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
    public DPTXlator8BitSigned(final String dptID) throws KNXFormatException {
        super(1);
        setSubType(dptID);
    }

    /* (non-Javadoc)
     * @see tuwien.auto.calimero.dptxlator.DPTXlator#getValue()
     */
    @Override
    public String getValue() {
        return makeString(0);
    }

    // overwritten to avoid conversion from signed to unsigned
    @Override
    public void setData(final byte[] data, final int offset) {
        if (offset < 0 || offset > data.length) {
            throw new KNXIllegalArgumentException("illegal offset " + offset);
        }
        final int size = Math.max(1, getTypeSize());
        final int length = (data.length - offset) / size * size;
        if (length == 0) {
            throw new KNXIllegalArgumentException("data length " + (data.length - offset)
                    + " < required KNX data type width " + size);
        }
        this.data = new short[length];
        for (int i = 0; i < length; ++i) {
            this.data[i] = data[offset + i];
        }
    }

    /**
     * Sets one new translation item from an signed value, replacing any old
     * items.
     * <p>
     *
     * @param value signed value, 0 &lt;= <code>value</code> &lt;= 255, the
     * higher bytes are ignored
     */
    public final void setValue(final int value) {
        data = new short[]{(short) value};
    }

    @Override
    public double getNumericValue() throws KNXFormatException {
        return data[0];
    }

    /* (non-Javadoc)
     * @see tuwien.auto.calimero.dptxlator.DPTXlator#getAllValues()
     */
    @Override
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
    @Override
    public final Map getSubTypes() {
        return types;
    }

    /**
     * @return the subtypes of the 8 Bit signed translator type
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
        return value;
    }

    private String makeString(final int index) {
        return appendUnit(Short.toString(fromDPT(data[index])));
    }

    @Override
    protected void toDPT(final String value, final short[] dst, final int index)
            throws KNXFormatException {
        try {
            dst[index] = toDPT(Short.decode(removeUnit(value)));
        } catch (final NumberFormatException e) {
            logThrow(LogLevel.WARN, "wrong value format " + value, null, value);
        }
    }

    private short toDPT(final int value) throws KNXFormatException {

        int convert = value;
        return (short) convert;
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
}
