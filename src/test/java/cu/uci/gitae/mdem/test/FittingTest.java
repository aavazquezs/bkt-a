package cu.uci.gitae.mdem.test;

import cu.uci.gitae.mdem.bkt.BKT;
import cu.uci.gitae.mdem.bkt.Item;
import cu.uci.gitae.mdem.bkt.parametersFitting.BruteForceFitting;
import cu.uci.gitae.mdem.bkt.parametersFitting.EmpiricalProbabilitiesFitting;
import cu.uci.gitae.mdem.bkt.parametersFitting.ExpectationMaximizationFitting;
import cu.uci.gitae.mdem.bkt.parametersFitting.FittingMethod;
import cu.uci.gitae.mdem.bkt.parametersFitting.Parametros;
import cu.uci.gitae.mdem.bkt.parametersFitting.SimulatedAnnealingFitting;
import cu.uci.gitae.mdem.metrics.FittingMethodValidation;
import cu.uci.gitae.mdem.utils.AnnotatingKnowledge;
import cu.uci.gitae.mdem.utils.LoadTSV;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author angel
 */
public class FittingTest {

    String pathToFile;

    public FittingTest() {
        pathToFile = "./data/dataset2.tsv";
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    private List<Item> cargaDataset() throws FileNotFoundException {
        List<String[]> dataset;
        dataset = LoadTSV.loadTSV(pathToFile);
        List<Item> items;
        items = dataset.stream().parallel()
                .map((String[] t) -> {
                    Item item = new Item(t[1], t[2], t[0].equalsIgnoreCase("1"), t[3]);
                    return item;
                })
                .collect(Collectors.toList());
        return items;
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

    //PRUEBAS SOBRE AJUSTE DE PARAMETROS
//    @Test
    public void cargarDataset() throws FileNotFoundException {
        System.out.println("TEST: cargarDataset");

        List<String[]> filas = LoadTSV.loadTSV(pathToFile);
        List<Item> items = this.cargaDatasetStream()
                .collect(Collectors.toList());
        assertEquals(5105, items.size());
        /*
        items.forEach(item -> {
            System.out.println(item.getEstudiante() + "   " + item.getHabilidad() + " " + item.getProblem() + "   " + (item.isCorrecto() ? "1" : "0"));
        });*/
    }

    //@Test 
    public void secuenciaPorHabilidad() throws FileNotFoundException {
        List<String[]> filas = LoadTSV.loadTSV(pathToFile);
        List<Item> items = filas
                .stream()
                .map((String[] fila) -> {
                    Item item = new Item(fila[2], fila[1], fila[0].equals("1"), fila[3]);
                    return item;
                })
                .collect(Collectors.toList());
        assertEquals(36, items.size());
        List<String> habilidades = items.stream().parallel()
                .map(i -> {
                    return i.getHabilidad();
                })
                .distinct()
                .collect(Collectors.toList());

        HashMap<String, List<Integer>> map = new HashMap();

        for (String habilidad : habilidades) {
            List<Integer> secuencia = items.stream().parallel()
                    .filter(i -> {
                        return i.getHabilidad().equals(habilidad);
                    })
                    .map(i -> {
                        return i.isCorrecto() ? 1 : 0;
                    })
                    .collect(Collectors.toList());
            map.put(habilidad, secuencia);
        }

        //imprimir secuencia de respuestas por habilidad
        map.forEach((k, v) -> {
            System.out.print(k);
            v.forEach(i -> {
                System.out.print(" " + i);
            });
            System.out.println("");
        });

    }

//    @Test
    public void bruteForceFitting() throws FileNotFoundException {
        System.out.println("TEST: bruteForceFitting");
        List<Item> items = this.cargaDatasetStream()
                .collect(Collectors.toList());
        BruteForceFitting bff = new BruteForceFitting(true, true);
        Map<String, Parametros> resultado = bff.fitParameters(items);
        resultado.forEach((llave, valor) -> {
            System.out.println("[Habilidad: " + llave + ", Parametros: " + valor.toString() + "]");
        });
    }

    //Pruebas para el metodo de probabilidad empirica
    //@Test 
    public void knowledgeHeuristic() {
        AnnotatingKnowledge ep = new AnnotatingKnowledge(new Integer[]{1, 0, 1, 1, 1});
        Double[] k = ep.getK();
        assertArrayEquals(new Double[]{0.5, 0.5, 1.0, 1.0, 1.0}, k);
    }

//    @Test
    public void empiricalProbabilitiesFitting() throws FileNotFoundException {
        System.out.println("TEST: empiricalProbabilitiesFitting");
        String habilidad = "ALT:PARALLELOGRAM-AREA";
        List<Item> items;
        items = this.cargaDatasetStream()
                .filter(i -> {
                    return i.getHabilidad().equals(habilidad);
                })
                .collect(Collectors.toList());
        FittingMethod fm = new EmpiricalProbabilitiesFitting();
        Map<String, Parametros> map = fm.fitParameters(items);
        map.forEach((k, v) -> {
            System.out.println("[Habilidad: " + k + ", Parametros: " + v.toString() + "]");
        });
    }

//    @Test
    public void simulatedAnnealingFitting() {
        System.out.println("TEST: simulatedAnnealingFitting");
        try {
            List<Item> items = cargaDataset();
            FittingMethod fm = new SimulatedAnnealingFitting();
            Map<String, Parametros> map = fm.fitParameters(items);
            map.forEach((k, v) -> {
                System.out.println("[Habilidad: " + k + ", Parametros: " + v.toString() + "]");
            });
        } catch (FileNotFoundException ex) {
            fail("Exception ocurred");
            Logger.getLogger(FittingTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
//    @Test
    public void emFitting(){
        System.out.println("TEST: Expectation Maximization Fitting");
        try {
            List<Item> items = cargaDataset();
            FittingMethod fm = new ExpectationMaximizationFitting();
            Map<String, Parametros> map = fm.fitParameters(items);
            map.forEach((k, v) -> {
                System.out.println("[Habilidad: " + k + ", Parametros: " + v.toString() + "]");
            });
        } catch (FileNotFoundException ex) {
            fail("Exception ocurred");
            Logger.getLogger(FittingTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
