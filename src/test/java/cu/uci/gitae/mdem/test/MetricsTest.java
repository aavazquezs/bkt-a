package cu.uci.gitae.mdem.test;

import cu.uci.gitae.mdem.metrics.ROC;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.math3.util.Pair;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author angel
 */
public class MetricsTest {
    
    public MetricsTest() {
    }
    /**

p
n
p
n

     */
    @Test
    public void rocCurveTest() throws Exception{
        Double[] classification = new Double[]{1.0, 1.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 1.0, 0.0, 
                                               1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0};
        Double[] score = new Double[]{0.9, 0.8, 0.7, 0.6, 0.55, 0.54, 0.53, 0.52, 0.51, 0.505, 
                                      0.4, 0.39, 0.38, 0.37, 0.36, 0.35, 0.34, 0.33, 0.3, 0.1};
        ROC roc = new ROC(Arrays.asList(classification), Arrays.asList(score), 10, 10);
        List<Pair<Double, Double>> points = roc.getRocCurve();
        List<Pair<Double, Double>> results = new ArrayList<>();
        results.add(new Pair<>(0.0, 0.0));
        results.add(new Pair<>(0.0, 0.1));
        results.add(new Pair<>(0.0, 0.2));
        results.add(new Pair<>(0.1, 0.2));
        results.add(new Pair<>(0.1, 0.3));
        results.add(new Pair<>(0.1, 0.4));
        results.add(new Pair<>(0.1, 0.5));
        results.add(new Pair<>(0.2, 0.5));
        results.add(new Pair<>(0.3, 0.5));
        results.add(new Pair<>(0.3, 0.6));
        results.add(new Pair<>(0.4, 0.6));
        results.add(new Pair<>(0.4, 0.7));
        results.add(new Pair<>(0.5, 0.7));
        results.add(new Pair<>(0.5, 0.8));
        results.add(new Pair<>(0.6, 0.8));
        results.add(new Pair<>(0.7, 0.8));
        results.add(new Pair<>(0.8, 0.8));
        results.add(new Pair<>(0.8, 0.9));
        results.add(new Pair<>(0.9, 0.9));
        results.add(new Pair<>(0.9, 1.0));
        results.add(new Pair<>(1.0, 1.0));
        assertArrayEquals(results.toArray(), points.toArray());
        //Saving to a File
        roc.exportRocCurveToFile(points, "./out/RocCurve.png", 500, 500);
    }
}
