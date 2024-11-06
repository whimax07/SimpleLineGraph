package org.example;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {


    @Test
    public void largeDataSmallDelta() throws InterruptedException {
        Main.createGraph(
                List.of(1_000_000d, 1_000_005d, 1_000_007d, 1_000_012d),
                List.of(300_126d, 300_081d, 300_160d, 300_170d)
        );
        Thread.sleep(100_000);
    }

    @Test
    public void lotsOfData() throws InterruptedException {
        final List<Double> xs = IntStream.range(0, 98)
                .asDoubleStream()
                .boxed()
                .toList();

        final List<Double> ys = IntStream.range(0, 98)
                .asDoubleStream()
                .map(x -> x * 5)
                .map(x -> Math.max(Math.abs(100 - x), 0))
                .map(x -> x - 50)
                .map(x -> -x)
                .map(x -> Math.max(0, x))
                .map(Math::sqrt)
                .boxed()
                .toList();

        Main.createGraph(xs, ys);
        Thread.sleep(100_000);
    }

}
