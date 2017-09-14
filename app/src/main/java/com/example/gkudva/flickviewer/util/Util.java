package com.example.gkudva.flickviewer.util;

import java.math.BigDecimal;

/**
 * Created by gkudva on 14/09/17.
 */

public class Util {

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

}
