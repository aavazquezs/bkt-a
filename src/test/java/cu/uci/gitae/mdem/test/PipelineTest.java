package cu.uci.gitae.mdem.test;

import cu.uci.gitae.mdem.bkt.dataload.DataLoad;
import cu.uci.gitae.mdem.bkt.dataload.DataLoadImpl;
import cu.uci.gitae.mdem.bkt.dataload.DataSourceType;
import cu.uci.gitae.mdem.bkt.ml.BKTModel;
import cu.uci.gitae.mdem.bkt.ml.FitParameterEstimator;
import cu.uci.gitae.mdem.bkt.ml.PreprocessTransformer;
import java.util.HashMap;
import java.util.Map;
import org.apache.spark.SparkConf;
import org.apache.spark.ml.Estimator;
import org.apache.spark.ml.Model;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.Transformer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author angel
 */
public class PipelineTest {

    private String masterConfig = "local[2]";
    private String path = "./data/dataset2.tsv";

    public PipelineTest() {
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
//    @Test
    public void pipelineTest() {

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

        long countBefore = dataset.count();
        System.out.println("Cantidad antes: "+countBefore);
        
        Pipeline pipeline = new Pipeline();
        PipelineStage[] array = new PipelineStage[1];
        Transformer pre = new PreprocessTransformer();
        Dataset<Row> ndata = pre.transform(dataset);
        ndata.printSchema();
        long countAfter = ndata.count();
        System.out.println("Cantidad despues: "+countAfter);
        
        Estimator estimador = new FitParameterEstimator();
        Model model = estimador.fit(ndata);
        model.transform(ndata);
//        PipelineModel model = pipeline.fit(dataset);
//        Dataset<Row> result = model.transform(dataset);
    }
}
