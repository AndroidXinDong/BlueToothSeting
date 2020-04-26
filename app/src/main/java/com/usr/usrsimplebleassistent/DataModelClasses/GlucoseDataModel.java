package com.usr.usrsimplebleassistent.DataModelClasses;

import java.util.Calendar;

/**
 * Glucose profile data constants
 */
public class GlucoseDataModel {
    public static final int UNIT_kgpl = 0;
    public static final int UNIT_molpl = 1;
    public float glucoseConcentration;
    public int sampleLocation;
    public int sequenceNumber;
    public int status;
    public Calendar time;
    public int timeOffset;
    public int type;
    public int unit;

    public GlucoseDataModel() {

    }

}
