package cu.uci.gitae.mdem.metrics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;
import org.apache.commons.math3.util.Pair;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author angel
 */
public class ROC {

    /**
     * L, the set of test examples;
     */
    private List<Double> L;
    /**
     * f(i), the probabilistic classifier's estimate that example i is positive
     */
    private List<Double> f;

    private List<Pair<Double, Double>> pares;
    /**
     * The number of positive examples
     */
    private Integer P;
    /**
     * The number of negative examples
     */
    private Integer N;

    public ROC(List<Double> L, List<Double> f, Integer P, Integer N) {
        if (P <= 0 || N <= 0) {
            throw new IllegalArgumentException("Value most be greater than zero");
        }
        this.L = L;
        this.f = f;
        this.P = P;
        this.N = N;

        pares = new ArrayList<>();

        for (int i = 0; i < L.size(); i++) {
            Pair<Double, Double> par = new Pair(L.get(i), f.get(i));
            pares.add(par);
        }
    }

    /**
     * Metodo para obtener los puntos pertenecientes a la curva ROC dados los
     * valores de
     *
     * @return
     * @throws Exception
     */
    public List<Pair<Double, Double>> getRocCurve() throws Exception {
        if (this.N > 0 && this.P > 0) {
        } else {
            throw new Exception("Values most be greater than zero");
        }
        //Ordenar descendentemente los elementos por la funcion 
        pares.sort((p1, p2) -> {
            return -1 * p1.getSecond().compareTo(p2.getSecond());
        });
        //inicializar los elementos
        Integer FP = 0, TP = 0;
        List<Pair<Double, Double>> points = new ArrayList<>();

        Double fPrev = Double.NEGATIVE_INFINITY;
        Integer i = 0;
        while (i < pares.size()) {
            if (!pares.get(i).getSecond().equals(fPrev)) {
                points.add(new Pair(FP.doubleValue() / this.N, TP.doubleValue() / this.P));
                fPrev = pares.get(i).getSecond();
            }
            if (pares.get(i).getFirst() > 0) { //is a positive example
                TP++;
            } else { //is a negative example
                FP++;
            }
            i++;
        }
        points.add(new Pair(FP.doubleValue() / this.N, TP.doubleValue() / this.P)); //Este es el punto (1,1).
        return points;
    }
    /**
     * Calculating the area under an ROC curve
     * @return 
     */
    public Double getAUC() throws Exception {
        if (P <= 0 || N <= 0) {
            throw new Exception("Values most be greater than zero");
        }
        //Ordenar descendentemente los elementos por la funcion 
        pares.sort((p1, p2) -> {
            return -1 * p1.getSecond().compareTo(p2.getSecond());
        });
        //inicializar los elementos
        Integer FP = 0, TP = 0;
        Integer fpPrev = 0, tpPrev = 0;
        Double A = 0.0;
        Double fPrev = Double.NEGATIVE_INFINITY;
        int i = 0;
        while(i < pares.size()){
            if(!pares.get(i).getSecond().equals(fPrev)){
                A += this.trapezoidArea(FP, fpPrev, TP, tpPrev);
                fPrev = pares.get(i).getSecond();
                fpPrev = FP;
                tpPrev = TP;
            }
            if(pares.get(i).getFirst()>0){ //is a positive example
                TP++;
            }else{ /* i is a negative example */
                FP++;
            }
            i++;
        }
        A += this.trapezoidArea(N, fpPrev, N, tpPrev);
        A = A/(N*P); /* scale from P · N onto the unit square */
        return A;
    }
    
    private Double trapezoidArea(Double x1, Double x2, Double y1, Double y2){
        Double base = Math.abs(x1 - x2);
        Double heightAvg = (y1+y2)/2.0;
        return base*heightAvg;
    }
    
    private Double trapezoidArea(Integer x1, Integer x2, Integer y1, Integer y2){
        Double base = Math.abs(x1.doubleValue() - x2.doubleValue());
        Double heightAvg = (y1+y2)/2.0;
        return base*heightAvg;
    }

    public void exportRocCurveToFile(List<Pair<Double, Double>> points, String pathToFile, int width, int height) throws FileNotFoundException, IOException {
        XYSeries curve = new XYSeries("ROC Curve");
        points.forEach((point) -> {
            curve.add(point.getFirst(), point.getSecond());
        });
        FileOutputStream output = new FileOutputStream(new File(pathToFile));
        XYSeriesCollection serie = new XYSeriesCollection(curve);
        JFreeChart chart = ChartFactory.createXYLineChart("ROC Curve", "False positive rate", "True positive rate", serie);
        chart.getXYPlot().getDomainAxis().setRange(0.0, 1.0);
        chart.getXYPlot().getRangeAxis().setRange(0.0, 1.0);
        BufferedImage image = chart.createBufferedImage(width, height);
        ImageIO.write(image, "png", output);
    }

}
