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
        Double[] pi = new Double[]{param.getL0(), 1 - param.getL0()};
        Double[][] A = new Double[][]{
            {1.0, 0.0},
            {param.getT(), 1 - param.getT()}
        };
        Double[][] B = new Double[][]{
            {1 - param.getS(), param.getS()},
            {param.getG(), 1 - param.getG()}
        };
        testHMM = new HiddenMarkovModel(2, 2, A, B, pi);
    }

    private void init() {
        Parametros param = new Parametros();
        param.randomInit();
        //pi[0] -> known
        //pi[1] -> unknown
        Double[] pi = new Double[]{param.getL0(), 1 - param.getL0()};
        Double[][] A = new Double[][]{
            {1.0, 0.0},
            {param.getT(), 1 - param.getT()}
        };
        Double[][] B = new Double[][]{
            {1 - param.getS(), param.getS()},
            {param.getG(), 1 - param.getG()}
        };
        testHMM = new HiddenMarkovModel(2, 2, A, B, pi);
    }

    private void init2() {
        Double[] pi = new Double[]{0.4, 0.6};
        Double[][] A = new Double[][]{
            {1.0, 0.0},
            {0.2, 0.8}
        };
        Double[][] B = new Double[][]{
            {0.8, 0.2},
            {0.2, 0.8}
        };
        testHMM = new HiddenMarkovModel(2, 2, A, B, pi);
    }

    @Test
    public void hmmEM() {
        System.out.println("Training without scaling:");
        System.out.println("-------------------------");
        testHMM.print();
        this.init2();
        int[] obs = new int[]{1, 0, 0, 1, 0};
        assertTrue(testHMM.algorithmBaumWelch(obs, 0.01, 100));
        testHMM.print();
    }

    @Test public void hmmScalingTest(){
        System.out.println("Training with scaling:");
        System.out.println("-------------------------");
        this.init2();
        testHMM.print();
        int[] obs = new int[]{1,0,0,1,0};
        testHMM.algorithmBaumWelchScaling(obs, 0.01, 100);
        testHMM.print();
    }
    @Test
    public void hmmEMLog() {
        System.out.println("Training with log scaling:");
        System.out.println("-------------------------");
        testHMM.print();
        this.init2();
        int[] obs = new int[]{1, 0, 0, 1, 0};
        assertTrue(testHMM.algorithmBaumWelchLog(obs, 0.01, 100));
        testHMM.print();
    }
}
