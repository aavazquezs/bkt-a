package cu.uci.gitae.mdem.test;

import cu.uci.gitae.mdem.bkt.BKTA;
import cu.uci.gitae.mdem.bkt.Item;
import cu.uci.gitae.mdem.bkt.dataload.DataLoad;
import cu.uci.gitae.mdem.bkt.dataload.DataLoadImpl;
import cu.uci.gitae.mdem.bkt.dataload.DataSourceType;
import cu.uci.gitae.mdem.bkt.parametersFitting.Parametros;
import java.util.HashMap;
import java.util.Map;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
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
public class TestBKTA {

    private BKTA bkta;
    private String masterConfig = "local[2]";
    private String path = "./data/dataset2.tsv";

    public TestBKTA() {
        bkta = new BKTA();
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void prueba() {
        //carga de datos
        Map<String, String> param = new HashMap<>();
        param.put("datasetPath", "./data/dataset2.tsv");
        param.put("emptySymbol", "?");
        /*
         Dataset<Row> dataset = bkta.getDataset(DataSourceType.TSV, param);
         */
        SparkSession spark;
        SparkConf conf = new SparkConf().setAppName("BKT-A").setMaster(masterConfig);
        spark = SparkSession.builder()
                .config(conf)
                .getOrCreate();
        DataLoad dataLoad = new DataLoadImpl();
        Dataset<Row> dataset = dataLoad.loadData(spark, DataSourceType.TSV, param);
        assertNotNull(dataset);
        //bkta.showDatasetSchema();
        System.out.println("TEST: Field names: ");
        String[] fieldNames = dataset.schema().fieldNames();
        for (String fieldName : fieldNames) {
            System.out.println(fieldName);
        }
        System.out.println("TEST: Esquema: ");
        dataset.printSchema();
        //preprocesamiento
        //dataset = dataset.select("First Attempt","Anon Student Id","Problem","KC (Original)");
        long cant = dataset.count();
        
        bkta.setDataset(dataset);
        
        System.out.println("TEST: Cantidad de tuplas sin pre-procesar: " + cant);
        Dataset<Item> items = bkta.preProcessDataset(param);
        items.printSchema();
        long cant2 = items.count();
        System.out.println("TEST: Cantidad de tuplas despues de pre-procesar: " + cant2);
        assertEquals(cant - 3, cant2);
        items.show(20);
        //probando fit parameters
        Map<String, Map<String, Parametros>> ehp = bkta.fitParameters(items, param);
        ehp.forEach((k, v) -> {
            System.out.println("Estudiante ID: " + k + "\n--------------------");
            v.forEach((vk, vv) -> {
                System.out.println("[Habilidad: " + vk + ", Parametros: " + vv.toString() + "]");
            });
        });
    }
}
