package cu.uci.gitae.mdem.test;

import cu.uci.gitae.mdem.bkt.Item;
import cu.uci.gitae.mdem.bkt.parametersFitting.BaumWelchFitting;
import cu.uci.gitae.mdem.bkt.parametersFitting.EmpiricalProbabilitiesFitting;
import cu.uci.gitae.mdem.bkt.parametersFitting.ExpectationMaximizationFitting;
import cu.uci.gitae.mdem.bkt.parametersFitting.FittingMethod;
import cu.uci.gitae.mdem.bkt.parametersFitting.Parametros;
import cu.uci.gitae.mdem.bkt.parametersFitting.RandomFitting;
import cu.uci.gitae.mdem.bkt.parametersFitting.SimulatedAnnealingFitting;
import cu.uci.gitae.mdem.metrics.FittingMethodValidation;
import cu.uci.gitae.mdem.metrics.ROC;
import cu.uci.gitae.mdem.utils.LoadTSV;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.math3.util.Pair;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author angel
 */
public class MetricsTest {

    String pathToFile;

    public MetricsTest() {
        pathToFile = "./data/dataset1.tsv";
    }

    private Stream<Item> cargaDatasetStream() throws FileNotFoundException {
        List<String[]> dataset;
        dataset = LoadTSV.loadTSV(pathToFile);
        Stream<Item> items;
        items = dataset.stream().parallel()
                .map((String[] t) -> {
                    Item item = new Item(t[1], t[2], t[0].equalsIgnoreCase("1"), t[3]);
                    return item;
                });
        return items;
    }
    
    private Stream<Item> cargaDatasetStream(String path) throws FileNotFoundException{
        List<String[]> dataset;
        dataset = LoadTSV.loadTSV(path);
        Stream<Item> items;
        items = dataset.stream().parallel()
                .map((String[] t) -> {
//                    System.out.println(t[0]+"\t"+t[1]+"\t"+t[2]+"\t"+t[3]);
                    Item item = new Item(t[1], t[2], t[0].equalsIgnoreCase("1"), t[3]);
                    return item;
                });
        return items;
    }

    /**
     *
     * @throws java.lang.Exception
     */
//    @Test
    public void rocCurveTest() throws Exception {
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

//    @Test
    public void calculateAUC() {
        Double[] classification = new Double[]{1.0, 1.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 1.0, 0.0,
            1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0};
        Double[] score = new Double[]{0.9, 0.8, 0.7, 0.6, 0.55, 0.54, 0.53, 0.52, 0.51, 0.505,
            0.4, 0.39, 0.38, 0.37, 0.36, 0.35, 0.34, 0.33, 0.3, 0.1};
        try {
            ROC roc = new ROC(Arrays.asList(classification), Arrays.asList(score), 10, 10);
            Double auc = roc.getAUC();
            roc.exportAucToFile(auc, roc.getRocCurve(), "./out/auc.png", 500, 500);
            assertEquals(new Double(0.68), auc);
        } catch (Exception e) {
            assertTrue(false);
        }
    }
    
//    @Test
    public void problematicCase2(){
        Double[] classification = new Double[]{0.0, 0.0, 0.0, 0.0, 1.0};
        Double[] score = new Double[]{0.01599, 0.01599, 0.01599, 0.01599, 0.812};
        try {
            ROC roc = new ROC(Arrays.asList(classification), Arrays.asList(score), 1L, 4L);
            Double auc = roc.getAUC();
            System.out.println(auc);
            roc.exportAucToFile(auc, roc.getRocCurve(), "./out/case2.png", 500, 500);
        } catch (Exception e) {
        }
    }

//    @Test
    public void calculateAUCFromDataset() {
        System.out.println("TEST: calculate AUC with simulated annealing fitting");
        try {
            Stream<Item> itemsStream = cargaDatasetStream();
            List<Item> items = itemsStream.collect(Collectors.toList());
            /*
            items.forEach(i->{
                System.out.println(i.toString());
            });*/
            FittingMethod fm = new SimulatedAnnealingFitting();
            Map<String, Parametros> map = fm.fitParameters(items);
            /*
            map.forEach((k,v)->{
                System.out.println("[Habilidad: "+k+",\tparametros: "+v.toString());
            });*/
            String currentHab = "ALT:TRIANGLE-SIDE";
            List<Item> prueba = items.stream().parallel()
                    .filter(i -> {
                        return i.getEstudiante().equals("Stu_9fb3481981a38a7299e9b3c96e0d0218")
                                && i.getHabilidad().equals(currentHab);
                    })
                    .collect(Collectors.toList());
            prueba.forEach(i -> {
                System.out.println(i.toString());
            });
            Parametros param = map.get(currentHab);
            System.out.println(param.toString());

            long N = items.stream().parallel()
                    .filter(i -> {
                        return !i.isCorrecto();
                    })
                    .count();
            long P = items.size() - N;

            List<Double> clasification = items.stream().parallel()
                    .map(i -> {
                        return i.isCorrecto() ? 1.0 : 0.0;
                    })
                    .collect(Collectors.toList());

            List<Double> pL = new ArrayList<>();
            List<Double> pC = new ArrayList<>();

            pL.add(param.getL0());
            pC.add(Double.NaN); //comenzamos a medir la probabilidad de responder correctamente
            //a partir de la primera probabilidad de dominar la habilidad
            Double probL;
            for (int i = 1; i <= items.size(); i++) {
                Item actual = items.get(i - 1);
                if (actual.isCorrecto()) {
                    probL = (pL.get(i - 1) * (1.0 - param.getS())) / (pL.get(i - 1) * (1.0 - param.getS() + (1.0 - pL.get(i - 1)) * param.getG()));
                } else {
                    probL = (pL.get(i - 1) * param.getS()) / (pL.get(i - 1) * param.getS() + (1. - 0 - pL.get(i - 1)) * (1.0 - param.getG()));
                }
                pL.add(probL);
                pC.add(probL * (1.0 - param.getS() + (1.0 - probL) * param.getG()));
            }
            pC.remove(0);
            ROC curve = new ROC(clasification, pC, P, N);
            Double auc = curve.getAUC();
            System.out.println(auc);
            curve.exportRocCurveToFile(curve.getRocCurve(), "./out/Stu_9fb3481981a38a7299e9b3c96e0d0218.ROCCurve.png", 300, 300);

        } catch (FileNotFoundException e) {
            System.out.println("ERROR: " + e.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(MetricsTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void calculateAucForAllStudent() throws FileNotFoundException, Exception {
        Stream<Item> itemsStream = cargaDatasetStream("./data/test/dataset1.tsv");
        List<Item> items = itemsStream.collect(Collectors.toList());
        
        assertNotEquals("La lista de items no puede ser vacia",0, items.size());
        //items.remove(0);
        
//        FittingMethod fm = new SimulatedAnnealingFitting();
//        FittingMethod fm = new RandomFitting();
//        FittingMethod fm = new EmpiricalProbabilitiesFitting();
//        FittingMethod fm = new BaumWelchFitting();
          FittingMethod fm = new ExpectationMaximizationFitting();
        //Map<String, Parametros> map = fm.fitParameters(items);
        FittingMethodValidation fmv = new FittingMethodValidation(items, fm);
//        Map<String, Double> map = fmv.obtenerAucPorEstudiante();
//        System.out.println("AUCs:");
//        map.forEach((k,v)->{
//            System.out.println(v);
//        });
        Double sum=0.0;
        Integer count=0;
//        for (Map.Entry<String, Double> entry : map.entrySet()) {
//            Double value = entry.getValue();
//            if(!value.equals(Double.NaN)){
//                sum+=value;
//                ++count;
//            }
//        }
//        System.out.println("Average AUC: "+sum/count.doubleValue());
        System.out.println("A Primes:");
        Map<String, Double> map2 = fmv.obtenerAPrimaPorEstudiante();
        map2.forEach((k,v)->{
            System.out.println(/*k+"->"+*/v);
        });
        for (Map.Entry<String, Double> entry : map2.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();
            if(!value.equals(Double.NaN)){
                sum+= value;
                ++count;
            }
        }
        System.out.println("Average A Prime: "+ sum/count);
    }
}
