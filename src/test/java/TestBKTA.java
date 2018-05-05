
import cu.uci.gitae.mdem.bkt.BKTA;
import cu.uci.gitae.mdem.bkt.Item;
import cu.uci.gitae.mdem.bkt.dataload.DataSourceType;
import java.util.HashMap;
import java.util.Map;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
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

    public TestBKTA() {
        bkta = new BKTA("local[2]", "./data/dataset.tsv");
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
         param.put("datasetPath", "./data/dataset.tsv");
         param.put("emptySymbol", "?");
         Dataset<Row> dataset = bkta.getDataset(DataSourceType.TSV, param);
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
         System.out.println("TEST: Cantidad de tuplas sin pre-procesar: "+cant);
         Dataset<Item> items = bkta.preProcessDataset(param);
         items.printSchema();
         long cant2 = items.count();
         System.out.println("TEST: Cantidad de tuplas despues de pre-procesar: "+cant2);
         assertEquals(cant-3, cant2);
         dataset.show(20);
     }

     @Test public void fittingEM(){
         
     }
}
