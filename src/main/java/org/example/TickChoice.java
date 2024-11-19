package org.example;

import org.example.LineGraph.R2;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

public class TickChoice {


    public List<Double> chooseTick(R2 axisRange, int numPoints) {
        final double range = axisRange.range();
        final double scaledRange = scaleToZero2Dp(range);

        return null;
    }



    public static double scaleToZero2Dp(double range) {
        final int base10Size = calcUpperBase10(range);
        return new BigDecimal(range)
                .scaleByPowerOfTen(2 - base10Size)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public static int calcUpperBase10(double value) {
        if (value == 0) return 0;
        final double base10 = Math.log10(Math.abs(value));
        final double ceiling = Math.ceil(base10);

        // Cast to int to get as close to int boundaries as possible.
        return (int) ((ceiling == base10) ? base10 + 1 : ceiling);
    }


}
