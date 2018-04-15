
import cu.uci.gitae.mdem.bkt.dataload.DataLoad;
import cu.uci.gitae.mdem.bkt.dataload.DataLoadImpl;
import cu.uci.gitae.mdem.bkt.dataload.DataSourceType;
import java.util.HashMap;
import java.util.Map;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
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
public class DataLoadTest {

    SparkConf conf;
    JavaSparkContext sc;

    public DataLoadTest() {
        conf = new SparkConf().setAppName("DataLoadTest").setMaster("local[2]");
        sc = new JavaSparkContext(conf);
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
    public void dataLoad() {
        String pathToDataset = "./data/dataset.tsv";
        String masterConfig = "local";
        DataLoad dataLoad = new DataLoadImpl("testDataLoad", masterConfig);
        Map<String, String> param = new HashMap<>();
        param.put("datasetPath", pathToDataset);
        Dataset<Row> dataset = dataLoad.loadData(DataSourceType.TSV, param);
        assertNotNull(dataset);
        System.out.println(dataset.count());
    }
}
