package cu.uci.gitae.mdem.test;

import cu.uci.gitae.mdem.bkt.Item;
import cu.uci.gitae.mdem.bkt.dataload.DataLoad;
import cu.uci.gitae.mdem.bkt.dataload.DataLoadImpl;
import cu.uci.gitae.mdem.bkt.dataload.DataSourceType;
import cu.uci.gitae.mdem.utils.LoadTSV;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
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
        String pathToDataset = "./data/Assistments_Math_2006_2007.txt";
        DataLoad dataLoad = new DataLoadImpl();
        Map<String, String> param = new HashMap<>();
        param.put("datasetPath", pathToDataset);
        Dataset<Row> dataset = dataLoad.loadData(sparkSession, DataSourceType.TSV, param);
        assertNotNull(dataset);
        System.out.println(dataset.count());
        dataset.printSchema();
        Dataset<Row> estudiantes = dataset
                //.select("Anon Student Id")
                .select("KC (MCAS39-State_WPI-Simple)")
                .distinct();
        System.out.println("KC: " + estudiantes.count());
//        final Dataset<Row> est_hab = dataset.select("Anon Student Id", "KC(SubSkills)");

//        Long sum = 0L;
//        Long cont = 0L;
//
//        List<Row> list = estudiantes.collectAsList();
//        for (Row row : list) {
//            Long count;
//            count = est_hab
//                    .filter(est_hab.col("Anon Student Id").equalTo(row.getString(0)))
//                    .select("KC(SubSkills)").distinct()
//                    .count();
//            System.out.println("[Estudiante: " + row.getString(0) + " - Habilidades: " + count + "]");
//            sum += count;
//            cont++;
//        }
//        System.out.println("Sum: "+sum+" - Cont: "+cont);
//        System.out.println("Promedio: "+(double)sum/cont);
    }

}
