/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import cu.uci.gitae.mdem.bkt.BKTA;
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
     public void dataload() {
         Map<String, String> param = new HashMap<>();
         param.put("datasetPath", "./data/dataset.tsv");
         Dataset<Row> dataset = bkta.getDataset(DataSourceType.TSV, param);
         assertNotNull(dataset);
         bkta.showDatasetSchema();
     }
}
