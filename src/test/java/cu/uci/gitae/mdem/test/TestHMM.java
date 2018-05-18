/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cu.uci.gitae.mdem.test;

import cu.uci.gitae.mdem.utils.HiddenMarkovModel;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author angel
 */
public class TestHMM {
    
    Integer dataset[][] = 
            {
                {2,2,3,3,3,3,1},
                {1,3,2,3,3,3,3},
                {3,3,2,2},
                {2,1,2,2,1,1,3}
            };
    HiddenMarkovModel testHMM;
    
    public TestHMM() throws Exception {
        
    }
    
    @Test public void hmmEM(){
        
    }
}
