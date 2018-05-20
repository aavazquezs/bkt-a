/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cu.uci.gitae.mdem.test;

import cu.uci.gitae.mdem.bkt.parametersFitting.Parametros;
import cu.uci.gitae.mdem.utils.HiddenMarkovModel;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author angel
 */
public class TestHMM {
    
    HiddenMarkovModel testHMM;
    
    public TestHMM() throws Exception {
        Parametros param = new Parametros();
        param.randomInit();
        //pi[0] -> known
        //pi[1] -> unknown
        Double[] pi = new Double[]{param.getL0(),1-param.getL0()};
        Double[][] A = new Double[][]{
            {1.0, 0.0},
            {param.getT(), 1-param.getT()}
        };
        Double[][] B = new Double[][]{
            {1-param.getS(), param.getS()}, 
            {param.getG(), 1-param.getG()}
        };
        testHMM = new HiddenMarkovModel(2, 2, A, B, pi);
    }
    
    private void init(){
        Parametros param = new Parametros();
        param.randomInit();
        //pi[0] -> known
        //pi[1] -> unknown
        Double[] pi = new Double[]{param.getL0(),1-param.getL0()};
        Double[][] A = new Double[][]{
            {1.0, 0.0},
            {param.getT(), 1-param.getT()}
        };
        Double[][] B = new Double[][]{
            {1-param.getS(), param.getS()}, 
            {param.getG(), 1-param.getG()}
        };
        testHMM = new HiddenMarkovModel(2, 2, A, B, pi);
    }
    
    @Test public void hmmEM(){
        testHMM.print();
        int[] obs = new int[]{1,1,0,1,0};
        testHMM.algorithmBaumWelch(obs, 0.01);
        testHMM.print();
    }
    
//    @Test public void hmmScalingTest(){
//        this.init();
//        testHMM.print();
//        int[] obs = new int[]{1,1,0,1,0};
//        testHMM.algorithmBaumWelchScaling(obs, 0.01, 1000);
//        testHMM.print();
//    }
}
