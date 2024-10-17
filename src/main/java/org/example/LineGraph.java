package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.awt.RenderingHints.*;

public class LineGraph extends JPanel {

    private final GraphArea graphArea = new GraphArea();
    private final GraphAxis xAxis = new GraphAxis(GraphAxis.Type.X);
    private final GraphAxis yAxis = new GraphAxis(GraphAxis.Type.Y);
    private final List<Double> xData = new ArrayList<>();
    private final List<Double> yData = new ArrayList<>();

    private boolean autoUpdateGraphDataRange = true;



    public LineGraph() {
        addComponents();
        colourComponents();
        addResizeListener();
    }

    private void addComponents() {
        setLayout(new GridBagLayout());

        final GridBagConstraints gridBagConstraints = new GridBagConstraints();

        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.weighty = 1;
        add(xAxis, gridBagConstraints);

        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 0;
        add(yAxis, gridBagConstraints);

        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 10;
        gridBagConstraints.weighty = 10;
        add(graphArea, gridBagConstraints);
    }

    private void colourComponents() {
        this.setBackground(Color.ORANGE);
        this.setForeground(Color.WHITE);
        graphArea.setBackground(Color.DARK_GRAY);
        graphArea.setForeground(Color.WHITE);
        xAxis.setBackground(new Color(0xb98b46));
        yAxis.setBackground(new Color(0x6E8755));
    }

    private void addResizeListener() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateGraph(xData, yData);
            }
        });
    }



    public <T extends Number> void setData(List<T> xData, List<T> yData) {
        if (xData.size() != yData.size()) {
            throw new RuntimeException("Data arrays of different size.");
        }

        this.xData.clear();
        this.yData.clear();

        if (xData.isEmpty()) {
            graphArea.clear();
            xAxis.clear();
            yAxis.clear();
            return;
        }

        final List<Double> xDoubles = xData.stream().map(Number::doubleValue).toList();
        final List<Double> yDoubles = yData.stream().map(Number::doubleValue).toList();
        this.xData.addAll(xDoubles);
        this.yData.addAll(yDoubles);
        updateGraph(xDoubles, yDoubles);
        repaint();
    }

    public void setAutoUpdateGraphDataRange(boolean enable) {
        autoUpdateGraphDataRange = enable;
    }

    public void setGraphBounds(double xMin, double xMax, double yMin, double yMax) {

    }

    public void setGraphBoundsX(double xMin, double xMax) {

    }

    public void setGraphBoundsY(double yMin, double yMax) {

    }



    private void updateGraph(List<Double> xDoubles, List<Double> yDoubles) {
        final ArrayList<V2> data = new ArrayList<>();
        for (int i = 0; i < xDoubles.size(); i++) {
            final double xDatum = xDoubles.get(i);
            final double yDatum = yDoubles.get(i);
            data.add(new V2(xDatum, yDatum));
        }
        graphArea.setGraphData(data);

        if (!autoUpdateGraphDataRange) return;

        final R2 dataRangeX = getDataRange(xDoubles);
        final R2 dataRangeY = getDataRange(yDoubles);

        xAxis.updateXAxis(dataRangeX, xDoubles);
        yAxis.updateYAxis(dataRangeY, yDoubles);
        graphArea.rescaleGraph(xAxis.getAxisLimits(), yAxis.getAxisLimits());
    }



    private static R2 getDataRange(List<Double> data) {
        double xMin = Double.MAX_VALUE;
        double xMax = Double.MIN_VALUE;

        for (final Double x : data) {
            xMin = Math.min(x, xMin);
            xMax = Math.max(x, xMax);
        }

        return new R2(xMin, xMax);
    }

    private static void setRenderingHints(Graphics2D g2) {
        final Map<Key, Object> renderingHints = Map.of(
                KEY_ANTIALIASING, VALUE_ANTIALIAS_ON,
                KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_QUALITY,
                KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_QUALITY,
                KEY_DITHERING, VALUE_DITHER_ENABLE,
                KEY_FRACTIONALMETRICS, VALUE_FRACTIONALMETRICS_ON,
                KEY_RENDERING, VALUE_RENDER_QUALITY,
                KEY_STROKE_CONTROL, VALUE_STROKE_NORMALIZE,
                KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON
        );

        g2.setRenderingHints(renderingHints);
    }



    public record V2(double x, double y) {  }

    public record R2(double l, double h) {
        public double range() {
            return h - l;
        }
    }



    private static class GraphArea extends JPanel {

        private static final float LINE_WIDTH = 1.5f;

        private final ArrayList<V2> graphData = new ArrayList<>();

        private R2 xAxisLimits;
        private R2 yAxisLimits;



        public void clear() {
            graphData.clear();
            xAxisLimits = null;
            yAxisLimits = null;
        }

        public void setGraphData(List<V2> data) {
            graphData.clear();
            graphData.addAll(data);
        }

        public void rescaleXAxis(R2 xAxisLimits) {
            rescaleGraph(xAxisLimits, yAxisLimits);
        }

        public void rescaleYAxis(R2 yAxisLimits) {
            rescaleGraph(xAxisLimits, yAxisLimits);
        }

        public void rescaleGraph(R2 xAxisLimits, R2 yAxisLimits) {
            this.xAxisLimits = xAxisLimits;
            this.yAxisLimits = yAxisLimits;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            final Graphics2D g2 = (Graphics2D) g;
            setRenderingHints(g2);
            setStroke(g2);

            if (graphData.isEmpty()) {
                displayText(g, "No Data");
                return;
            }

            if (xAxisLimits == null || yAxisLimits == null) {
                displayText(g, "Axis config error [internal]");
            }

            drawLine(g);
        }

        private void displayText(Graphics g, String text) {
            final FontMetrics metrics = g.getFontMetrics(getFont());
            final int textWidth = metrics.stringWidth(text);
            final int x = (getWidth() - textWidth) / 2;
            final int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
            g.drawString(text, x, y);
        }

        private void setStroke(Graphics2D g2) {
            final BasicStroke lineStyle = new BasicStroke(
                    LINE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 2.0f
            );
            g2.setStroke(lineStyle);

            final Color foreground = getForeground();
            g2.setColor(foreground);
        }

        private void drawLine(Graphics g) {
            final Dimension size = getSize();
            final V2 dataScaling = new V2(
                    size.width / (xAxisLimits.h - xAxisLimits.l),
                    size.height / (yAxisLimits.h - yAxisLimits.l)
            );

            if (graphData.size() == 1) {
                final V2 pixel = calcPixel(graphData.get(0), dataScaling);
                g.drawLine((int) pixel.x - 2, (int) pixel.y - 2, (int)pixel.x + 2, (int) pixel.y + 2);
                g.drawLine((int) pixel.x - 2, (int) pixel.y + 2, (int)pixel.x + 2, (int) pixel.y - 2);
            }

            for (int i = 1; i < graphData.size(); i++) {
                final V2 start = graphData.get(i - 1);
                final V2 end = graphData.get(i);
                final V2 startPos = calcPixel(start, dataScaling);
                final V2 endPos = calcPixel(end, dataScaling);

                g.drawLine((int) startPos.x, (int) startPos.y, (int) endPos.x, (int) endPos.y);
            }
        }

        private V2 calcPixel(V2 data, V2 dataScaling) {
            return new V2(
                    (data.x - xAxisLimits.l) * dataScaling.x,
                    (yAxisLimits.h - data.y) * dataScaling.y
            );
        }

    }

    protected static class GraphAxis extends JPanel {

        private static final double USAGE_MIN_X = 0.8;
        private static final double USAGE_MIN_Y = 0.4;
        private static final int MIN_TICK_SEPARATION = 5;
        private static final int AXIS_GAP = 5;
        private static final int TICK_LENGTH = 3;

        private final Type type;
        private final Font monoSpacedFont;

        private List<AxisTick> axisTicks = new ArrayList<>();
        private R2 axisLimits = new R2(0, 0);
        private int storedWidth = 0;
        private int storedHeight = 0;



        public GraphAxis(Type type) {
            this.type = type;
            Font defualtFont = new JLabel().getFont();
            this.monoSpacedFont = new Font(Font.MONOSPACED, Font.PLAIN, defualtFont.getSize() + 2);
        }



        public void updateXAxis(R2 dataBounds, List<Double> data) {
            final int numDataPoints = data.size();
            if (numDataPoints == 1) {
                singleDataPointAxis(data);
                return;
            }

            final int oldHeight = storedHeight;
            storedHeight = getHeight();
            final int oldWidth = storedWidth;
            storedWidth = getWidth();
            final boolean sizeRedraw = storedWidth != oldWidth || storedHeight != oldHeight;

            final Graphics graphics = getGraphics();
            final FontMetrics fontMetrics = graphics.getFontMetrics(monoSpacedFont);

            if (!sameGate(data)) {
                // If the gate is different then thread the x-axis the same as the y-axis.
                final R2 axisBounds = calculateIrregularDataAxis(dataBounds);
                final boolean dataRedraw = shouldRedrawIrregularAxis(axisLimits, axisBounds, USAGE_MIN_X);
                if (!sizeRedraw && !dataRedraw) return;

                final int width = getWidth();
                final int numberOfTicks = chooseNumberOfIrregularTicks(axisBounds, width, fontMetrics);

                axisTicks = calculateTicks(axisBounds, numberOfTicks);
                axisLimits = axisBounds;
                return;
            }

            // Same gate so redrawing is unnoticeable unless the data has changed or the size has.
            final int maxTickWidth = getMaxTickWidth(fontMetrics, data);
            final int width = getWidth();
            final int tickGroupSize = chooseNumberOfRegularTicks(maxTickWidth, width, numDataPoints);
            final int numberOfTicks = (int) Math.ceil((double) numDataPoints / tickGroupSize);

            final R2 axisBounds = calculateRegularDataAxis(dataBounds, numDataPoints, numberOfTicks, tickGroupSize);

            // NOTE(Max): I am assuming that you either won't notice a regular axis being redrawn or they should be.
            axisTicks = calculateTicks(dataBounds, numberOfTicks);
            axisLimits = axisBounds;
        }

        public void updateYAxis(R2 dataBounds, List<Double> data) {
            if (data.size() == 1) {
                singleDataPointAxis(data);
                return;
            }

            final R2 axisBounds = calculateIrregularDataAxis(dataBounds);
            if (!shouldRedrawIrregularAxis(axisLimits, axisBounds, USAGE_MIN_Y)) return;

            final FontMetrics fontMetrics = getGraphics().getFontMetrics(monoSpacedFont);
            final int textHeight = fontMetrics.getHeight();
            final int height = getHeight();
            final int numberOfTicks = 10;

            axisTicks = calculateTicks(axisBounds, numberOfTicks);
            axisLimits = axisBounds;
        }



        private void singleDataPointAxis(List<Double> data) {
            final double datum = data.get(0);
            final double min = (datum < 0) ? datum * 2 : 0;
            final double max = (datum < 0) ? 0 : datum * 2;

            final R2 axisBounds = new R2(min, max);
            axisTicks = calculateTicks(axisBounds, 3);
            axisLimits = axisBounds;
        }

        private R2 calculateIrregularDataAxis(R2 dataBounds) {
            final double difference = dataBounds.range();
            final int base10Size = calcBase10Size(difference);

            // Divide the block size by 2 to round to 0 and 5.
            final double blockSize = Math.pow(10, base10Size - 1) / 2;
            final double lowerLimit = Math.floor(dataBounds.l / blockSize) * blockSize;
            final double upperLimit = Math.ceil(dataBounds.h / blockSize) * blockSize;

            return new R2(lowerLimit, upperLimit);
        }

        private int chooseNumberOfIrregularTicks(R2 axisLimits, int totalSpace, FontMetrics fontMetrics) {
            int result = 10;
            int spaceUsed = tickSpace(result, axisLimits, fontMetrics);

            if (spaceUsed > totalSpace) {
                while (spaceUsed > totalSpace) {
                    result -= 1;
                    spaceUsed = tickSpace(result, axisLimits, fontMetrics);
                    if (result == 1) break;
                }
            } else {
                do {
                    result *= 2;
                    spaceUsed = tickSpace(result, axisLimits, fontMetrics);
                } while (spaceUsed < totalSpace);

                result /= 2;
            }

            return result;
        }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        private boolean shouldRedrawIrregularAxis(R2 oldBounds, R2 newBounds, double minUsage) {
            // If the new bounds are "bigger" then the range should be recalculated.
            // TODO(Max): Only recalculate both sides if needed.
            if (newBounds.l < oldBounds.l || newBounds.h > oldBounds.h) return true;
            final double newRange = newBounds.range();
            final double oldRange = oldBounds.range();
            return newRange / oldRange < minUsage;
        }

        private R2 calculateRegularDataAxis(R2 dataBounds, int numDataPoints, int numTicks, int tickGroupSize) {
            final double range = dataBounds.range();
            final double stride = range / (numDataPoints - 1);
            final double axisSpan = numTicks * tickGroupSize * stride;

            return new R2(dataBounds.l, dataBounds.l + axisSpan);
        }

        private int chooseNumberOfRegularTicks(int maxTickWidth, int width, int numDataPoints) {
            final int tickSpace = maxTickWidth + MIN_TICK_SEPARATION;
            int numberOfTicks = numDataPoints;
            double groupSize = 1;

            while (width < tickSpace * numberOfTicks) {
                groupSize += 1;
                numberOfTicks = (int) Math.ceil(numDataPoints / groupSize);
            }

            return (int) groupSize;
        }



        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setFont(monoSpacedFont);

            switch (type) {
                case X -> drawXLabels(g);
                case Y -> drawYLabels(g);
            }
        }

        private void drawXLabels(Graphics g) {
            final double range = axisLimits.range();
            final double pixelRatio = getWidth() / range;

            final FontMetrics fontMetrics = g.getFontMetrics(monoSpacedFont);

            for (AxisTick axisTick : axisTicks) {
                final int position = (int) ((axisTick.value - axisLimits.l) * pixelRatio);
                final int stringWidth = fontMetrics.stringWidth(axisTick.text);
//                final int stringStart = (int) (position - (stringWidth / 2d));
                g.drawString(axisTick.text, position, fontMetrics.getHeight());
                g.drawLine(position, 0, position, TICK_LENGTH);
            }
        }

        private void drawYLabels(Graphics g) {
            final int height = getHeight();
            final int width = getWidth();
            final double range = axisLimits.range();
            final double pixelRatio = height / range;

            final FontMetrics fontMetrics = g.getFontMetrics(monoSpacedFont);

            for (AxisTick axisTick : axisTicks) {
                final double verticalPixelOffset = (axisTick.value - axisLimits.l) * pixelRatio;
                final int yPosition = height - (int) verticalPixelOffset;
                final int stringWidth = fontMetrics.stringWidth(axisTick.text);
                final int stringStart = width - (stringWidth + AXIS_GAP);
                g.drawString(axisTick.text, stringStart, yPosition);
                g.drawLine(width, yPosition, width - TICK_LENGTH, yPosition);
            }
        }



        public void clear() {
            axisTicks.clear();
            axisLimits = null;
        }

        public R2 getAxisLimits() {
            return axisLimits;
        }



        private static boolean sameGate(List<Double> data) {
            if (data.size() < 2) return true;
            final double gate = data.get(1) - data.get(0);
            for (int i = 1; i < data.size(); i++) {
                final Double a = data.get(i - 1);
                final Double b = data.get(i);
                if ((b - a) != gate) {
                    return false;
                }
            }
            return true;
        }

        private static List<AxisTick> calculateTicks(R2 tickRange, int numberOfTicks) {
            final double stride = tickRange.range() / numberOfTicks;
            final ArrayList<AxisTick> ticks = new ArrayList<>();

            for (int i = 0; i <= numberOfTicks; i++) {
                final double value = tickRange.l + (i * stride);
                final AxisTick axisTick = new AxisTick(value, Double.toString(value));
                ticks.add(axisTick);
            }

            return ticks;
        }

        private static int tickSpace(int numGaps, R2 axisLimits, FontMetrics fontMetrics) {
            final double gap = axisLimits.range() / numGaps;
            final String lowString = Double.toString(axisLimits.l + gap);
            final String highString = Double.toString(axisLimits.h - gap);

            final int sampleWidth = Math.max(
                    fontMetrics.stringWidth(lowString),
                    fontMetrics.stringWidth(highString)
            );

            return (numGaps + 1) * (sampleWidth + MIN_TICK_SEPARATION);
        }

        private static int getMaxTickWidth(FontMetrics fontMetrics, List<Double> data) {
            return (int) data.stream()
                    .map(d -> Double.toString(d))
                    .mapToDouble(fontMetrics::stringWidth)
                    .max()
                    .orElseThrow(() -> new RuntimeException(String.format(
                            "Could not find max width. [Font=%s, Data=%s]", fontMetrics, data
                    )));
        }



        private static int calcBase10Size(double number) {
            if (number == 0) return 0;
            return (int) Math.ceil(Math.log10(Math.abs(number)));
        }



        public enum Type { X, Y }

        private record AxisTick(double value, String text) {  }

    }

}
