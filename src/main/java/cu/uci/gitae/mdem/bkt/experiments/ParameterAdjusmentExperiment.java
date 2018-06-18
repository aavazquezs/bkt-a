package cu.uci.gitae.mdem.bkt.experiments;

import cu.uci.gitae.mdem.bkt.parametersFitting.FittingMethod;

/**
 * Clase para experimentar sobre el ajuste de parametros en el algoritmo BKT.
 * Se compara:
 *  -> EM - algoritmo Baum-Welch 
 *  -> BF - algoritmo de fuerza bruta
 *  -> EP - algoritmo empirical propabilities
 * Se utilizan diferentes datasets:
 *  ->  Dataset provisto en: Baker, R.S.J.d., Corbett, A.T., Aleven, V. (2008) 
 *      More Accurate Student Modeling Through Contextual Estimation of Slip and
 *      Guess Probabilities in Bayesian Knowledge Tracing. Proceedings of the 
 *      9th International Conference on Intelligent Tutoring Systems, 406-415.
 *  ->  
 * @author angel
 */
public class ParameterAdjusmentExperiment {
    String[] pathToDatasets;
    FittingMethod baumWelch;
    FittingMethod bruteForce;
    FittingMethod empiricalProbabilities;

    public ParameterAdjusmentExperiment() {
        pathToDatasets = new String[]{
            "./data/dataset.tsv",
            ""
        };
    }
    
}
