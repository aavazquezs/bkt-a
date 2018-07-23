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
    /**
     * Pares de puntos, clasificacion y estimado
     */
    private List<Pair<Double, Double>> pares;
    /**
     * The number of positive examples
     */
    private Long P;
    /**
     * The number of negative examples
     */
    private Long N;

    /**
     * Crea una curva ROC a partir del patrón de respuestas correctas e incorrectas
     * y las predicciones hechas para ese patrón, tambien es necesario proveer el
     * número de casos positivos y falsos.
     * @param L
     * @param f
     * @param P
     * @param N 
     */
    public ROC(List<Double> L, List<Double> f, Long P, Long N) {
        if (P <= 0 || N <= 0) {
            throw new IllegalArgumentException("Value most be greater than zero");
        }
        
        if(L.size()!=f.size()){
            throw  new IllegalArgumentException("Both list most have equal size");
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
     * Crea una curva ROC a partir del patrón de respuestas correctas e incorrectas
     * y las predicciones hechas para ese patrón, tambien es necesario proveer el
     * número de casos positivos y falsos.
     * @param L patron de respuestas
     * @param f valor de probabilidad de las respuestas
     * @param P cantidad de respuestas positivas
     * @param N cantidad de respuestas negativas
     */
    public ROC(List<Double> L, List<Double> f, Integer P, Integer N) {
        if (P <= 0 || N <= 0) {
            throw new IllegalArgumentException("Value most be greater than zero");
        }
        if(L.size()!=f.size()){
            throw  new IllegalArgumentException("Both list must have equal size");
        }
        this.L = L;
        this.f = f;
        this.P = P.longValue();
        this.N = N.longValue();

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
        Long FP = 0L, TP = 0L;
        Long fpPrev = 0L, tpPrev = 0L;
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
        A += this.trapezoidArea(N, fpPrev, P, tpPrev);//Revisar en el algoritmo del paper dice N
        A = A/(N*P); /* scale from P · N onto the unit square */
        return A;
    }
    
    public Double getAPrime(){
        List<Double> zeroPredictions = new ArrayList<>();
        List<Double> onePredictions = new ArrayList<>();
        for (int i = 0; i < this.L.size(); i++) {
            Double label = this.L.get(i);
            if(label.equals(1.0)){
                onePredictions.add(this.f.get(i));
            }else{
                zeroPredictions.add(this.f.get(i));
            }
        }
        Double correct = 0.0;
        Long count = 0L;
        for (Double zeroP : zeroPredictions) {
            for (Double oneP : onePredictions) {
                if(Math.abs(zeroP - oneP) < 0.000001){
                    correct += 0.5;
                }else if(zeroP < oneP){
                    correct += 1.0;
                }
                ++count;
            }
        }
        Double aPrime = correct/count;
        return aPrime;
    }
    
    private Double trapezoidArea(Double x1, Double x2, Double y1, Double y2){
        Double base = Math.abs(x1 - x2);
        Double heightAvg = (y1+y2)/2.0;
        return base*heightAvg;
    }
    
    private Double trapezoidArea(Long x1, Long x2, Long y1, Long y2){
        Double base = Math.abs(x1.doubleValue() - x2.doubleValue());
        Double heightAvg = (y1+y2)/2.0;
        return base*heightAvg;
    }

    public void exportAucToFile(Double auc, List<Pair<Double, Double>> points, String pathToFile, int width, int height) throws FileNotFoundException, IOException{
        FileOutputStream output = new FileOutputStream(new File(pathToFile));
        
        XYSeries curve = new XYSeries("ROC Curve");
        points.forEach((point) -> {
            curve.add(point.getFirst(), point.getSecond());
        });
        XYSeriesCollection serie = new XYSeriesCollection(curve);
        JFreeChart chart = ChartFactory.createXYAreaChart("AUC="+auc, "False positive rate", "True positive rate", serie);
        chart.getXYPlot().getDomainAxis().setRange(-0.05, 1.05);
        chart.getXYPlot().getRangeAxis().setRange(-0.05, 1.05);
        BufferedImage image = chart.createBufferedImage(width, height);
        ImageIO.write(image, "png", output);
    }
    
    public void exportRocCurveToFile(List<Pair<Double, Double>> points, String pathToFile, int width, int height) throws FileNotFoundException, IOException {
        XYSeries curve = new XYSeries("ROC Curve");
        points.forEach((point) -> {
            curve.add(point.getFirst(), point.getSecond());
        });
        FileOutputStream output = new FileOutputStream(new File(pathToFile));
        XYSeriesCollection serie = new XYSeriesCollection(curve);
        JFreeChart chart = ChartFactory.createXYLineChart("ROC Curve", "False positive rate", "True positive rate", serie);
        chart.getXYPlot().getDomainAxis().setRange(-0.05, 1.05);
        chart.getXYPlot().getRangeAxis().setRange(-0.05, 1.05);
        BufferedImage image = chart.createBufferedImage(width, height);
        ImageIO.write(image, "png", output);
    }

}
