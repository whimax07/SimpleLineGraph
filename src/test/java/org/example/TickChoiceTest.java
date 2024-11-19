package org.example;

import org.junit.jupiter.api.Test;

import static org.example.TickChoice.calcUpperBase10;
import static org.example.TickChoice.scaleToZero2Dp;
import static org.junit.jupiter.api.Assertions.*;

public class TickChoiceTest {

    @Test
    public void calcBase10Size_GreaterThan10() {
        assertEquals(2, calcUpperBase10(50));
        assertEquals(2, calcUpperBase10(10.0001));
        assertEquals(2, calcUpperBase10(99.999));
        assertEquals(3, calcUpperBase10(100));
        assertEquals(3, calcUpperBase10(150));
        assertEquals(3, calcUpperBase10(654.7156));
        assertEquals(3, calcUpperBase10(999));
        assertEquals(4, calcUpperBase10(1023));
        assertEquals(13, calcUpperBase10(3_456_789_123_456.0));
    }

    @Test
    public void calcBase10Size_Between10And1() {
        assertEquals(1, calcUpperBase10(1.001));
        assertEquals(1, calcUpperBase10(3));
        assertEquals(2, calcUpperBase10(10));
    }

    @Test
    public void calcBase10Size_LessThan1() {
        assertEquals(0, calcUpperBase10(0.9));
        assertEquals(0, calcUpperBase10(0.2));
        assertEquals(0, calcUpperBase10(0.1));
        assertEquals(-1, calcUpperBase10(0.0672894));
        assertEquals(-1, calcUpperBase10(0.0372894));
        assertEquals(-1, calcUpperBase10(0.01));
        assertEquals(-2, calcUpperBase10(0.00672894));
        assertEquals(-3, calcUpperBase10(0.000672894));
        assertEquals(-5, calcUpperBase10(0.000008736291));
    }

    @Test
    public void calcBase10Size_Negative() {
        assertEquals(4, calcUpperBase10(-1023));
        assertEquals(3, calcUpperBase10(-100));
        assertEquals(2, calcUpperBase10(-50));
        assertEquals(1, calcUpperBase10(-3));
        assertEquals(0, calcUpperBase10(-0.9));
        assertEquals(0, calcUpperBase10(-0.1));
        assertEquals(-1, calcUpperBase10(-0.0672894));
        assertEquals(-1, calcUpperBase10(-0.01));
        assertEquals(-5, calcUpperBase10(-0.000008736291));
    }



    @Test
    public void scaleToZero_2Dp_MoreThan10() {
        assertEquals(10.0, scaleToZero2Dp(100.0));
        assertEquals(20.0, scaleToZero2Dp(200.0));
        assertEquals(31.1, scaleToZero2Dp(311.0));
        assertEquals(31.1, scaleToZero2Dp(31100.0));
    }

    @Test
    public void scaleToZero_2Dp_Between10And1() {
        assertEquals(30.0, scaleToZero2Dp(3.0));
    }

    @Test
    public void scaleToZero_2Dp_LessThan1() {
        assertEquals(30.0, scaleToZero2Dp(0.3));
        assertEquals(31.1, scaleToZero2Dp(0.0311));
    }

    @Test
    public void scaleToZero_2Dp_Negative() {
        assertEquals(-10.0, scaleToZero2Dp(-1.0));
        assertEquals(-50.0, scaleToZero2Dp(-5.0));
        assertEquals(-12.34, scaleToZero2Dp(-1234.0));
        assertEquals(-46.8, scaleToZero2Dp(-0.0468));
    }

}
