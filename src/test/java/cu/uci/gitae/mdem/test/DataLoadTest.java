package cu.uci.gitae.mdem.test;


import cu.uci.gitae.mdem.bkt.dataload.DataLoad;
import cu.uci.gitae.mdem.bkt.dataload.DataLoadImpl;
import cu.uci.gitae.mdem.bkt.dataload.DataSourceType;
import java.util.HashMap;
import java.util.Map;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
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
public class DataLoadTest {

    SparkConf conf;
    SparkSession sparkSession;

    public DataLoadTest() {
        conf = new SparkConf().setAppName("DataLoadTest").setMaster("local[2]");
        sparkSession = SparkSession.builder().config(conf).getOrCreate();
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
        DataLoad dataLoad = new DataLoadImpl(sparkSession);
        Map<String, String> param = new HashMap<>();
        param.put("datasetPath", pathToDataset);
        Dataset<Row> dataset = dataLoad.loadData(DataSourceType.TSV, param);
        assertNotNull(dataset);
        System.out.println(dataset.count());
    }
}
