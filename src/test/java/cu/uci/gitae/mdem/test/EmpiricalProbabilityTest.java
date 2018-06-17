/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cu.uci.gitae.mdem.test;

import cu.uci.gitae.mdem.bkt.Item;
import cu.uci.gitae.mdem.bkt.parametersFitting.EmpiricalProbabilitiesFitting;
import cu.uci.gitae.mdem.bkt.parametersFitting.FittingMethod;
import cu.uci.gitae.mdem.bkt.parametersFitting.Parametros;
import cu.uci.gitae.mdem.utils.AnnotatingKnowledge;
import cu.uci.gitae.mdem.utils.LoadTSV;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author angel
 */
public class EmpiricalProbabilityTest {
    
    public EmpiricalProbabilityTest() {
    }
    @Test public void knowledgeHeuristic(){
        AnnotatingKnowledge ep =new AnnotatingKnowledge(new Integer[]{1,0,1,1,1});
        Double[] k = ep.getK();
        assertArrayEquals(new Double[]{0.5, 0.5, 1.0, 1.0, 1.0}, k);
    }
    @Test public void empiricalProbabilitiesFitting() throws FileNotFoundException{
        String pathToDataset = "./data/dataset.tsv";
        String habilidad = "ALT:PARALLELOGRAM-AREA";
        List<String[]> dataset;
        dataset = LoadTSV.loadTSV(pathToDataset);
        List<Item> items;
        items = dataset.stream().parallel()
                .filter(row->{
                    return row[3].equals(habilidad);
                })
                .map((String[] t)->{
                    Item item = new Item(t[1], t[2], t[0].equalsIgnoreCase("1"), habilidad);
                    return item;
                })
                .collect(Collectors.toList());
        FittingMethod fm = new EmpiricalProbabilitiesFitting();
        Map<String, Parametros> map = fm.fitParameters(items);
        map.forEach((k,v)->{
            System.out.println("[Habilidad: "+k+", Parametros: "+v.toString()+"]");
        });
    }
}
