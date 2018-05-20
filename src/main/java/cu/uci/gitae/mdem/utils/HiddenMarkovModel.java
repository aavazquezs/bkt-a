package cu.uci.gitae.mdem.utils;

import java.util.Arrays;
import org.apache.commons.math3.exception.OutOfRangeException;

/**
 * Representacion compacta de un modelo oculto de markov lambda = (A; B; pi)
 * donde N; M son implicados por A y B. El problema de aprendizaje es ajustar el
 * modelo lambda, tal que P(O|lambda) sea maximizado.
 *
 * @author angel
 */
public class HiddenMarkovModel {

    private Integer n;      //Cantidad de estados
    private Integer m;      //Cantidad de observaciones por estado
    private Double[][] A;   //distribucion de probabilidad de transicion de estado
    private Double[][] B;   //distribucion de probabilidad de observacion de simbolo
    private Double[] pi;    //distribucion de estados iniciales
    private double[] scaling;     //scaling factor 

    public HiddenMarkovModel(Integer n, Integer m, Double[][] A, Double[][] B, Double[] pi) {
        this.n = n;
        this.m = m;
        this.A = A;
        this.B = B;
        this.pi = pi;
    }

    public HiddenMarkovModel(Integer n, Integer m) {
        if (n <= 0) {
            throw new OutOfRangeException(n, 1, Integer.MAX_VALUE);
        }
        if (m <= 0) {
            throw new OutOfRangeException(m, 1, Integer.MAX_VALUE);
        }
        this.n = n;
        this.m = m;
        pi = new Double[n];
        A = new Double[n][n];
        B = new Double[n][m];
        this.init();
    }

    private void init() {
        // set startup probability 
        Arrays.fill(pi, 0.0);
        pi[0] = 1.0;
        //inicializando la matriz de transiciones
        Double[] temp = new Double[n];
        Arrays.fill(temp, 0.0);
        Arrays.fill(A, temp);

        //inicializando la matriz de observacion por simbolo
        Double[] temp2 = new Double[m];
        Arrays.fill(temp2, 1.0 / m);
        Arrays.fill(B, temp2);
    }

    /**
     * Traditional Forward Algorithm.
     * The probability of the partial observation sequence, O_1 O_2 · · · O_t, (up 
     * to time t) and state S_i at time t, given the model λ.
     *
     * @param obs la secuencia de observacion O
     * @return alfa[Estado][Tiempo]
     *
     */
    protected double[][] forwardProc(int[] obs) {
        //Initialization:
        double[][] alpha = new double[this.n][obs.length];
        for (int l = 0; l < this.n; l++) {
            alpha[l][0] = pi[l] * B[l][obs[0]];
        }
        //Induction
        for (int t = 1; t < obs.length; t++) {
            for (int k = 0; k < alpha.length; k++) {
                double sum = 0;
                for (int l = 0; l < n; l++) {
                    sum += alpha[l][t - 1] * A[l][k];
                }
                alpha[k][t] = sum * B[k][obs[t]];
            }
        }
        return alpha;
    }

    /**
     * Algoritmo Forward con escala, normalizando los resultados para evitar que
     * caigan a cero rapidamente. También actualiza la variable scaling, la cuál
     * es el factor de escala en cada paso.
     *
     * @param obs
     * @return
     */
    protected double[][] forwardScaling(int[] obs) {
        int T = obs.length;
        double[] c = new double[T];//scaling factor
        double[][] alpha = this.forwardProc(obs);
        double[][] alpha_t = new double[this.n][obs.length];
        //Initialization
        double sum = 0.0;
        for (int i = 0; i < this.n; i++) {
            sum += alpha[i][0];
        }
        c[0] = 1.0 / sum;
        for (int i = 0; i < this.n; i++) { //para todo alphan_1(i) = c_1 * alpha_1(i)
            alpha_t[i][0] = c[0] * alpha[i][0];
        }
        //Induccion
        for (int t = 1; t < T; t++) {
            for (int i = 0; i < this.n; i++) {
                sum = 0.0;
                for (int j = 0; j < this.n; j++) {
                    sum += alpha_t[i][t - 1] * A[j][i];
                }
                alpha_t[i][t] = sum * B[i][obs[t]];
            }
            sum = 0.0;
            for (int i = 0; i < this.n; i++) {
                sum += alpha_t[i][t];
            }
            c[t] = 1.0 / sum;
            for (int i = 0; i < this.n; i++) {
                alpha_t[i][t] = c[t] * alpha_t[i][t];
            }
        }
        this.scaling = c;
        return alpha_t;
    }

    /**
     * Backward algorithm.
     * The probability of the partial observation sequence from t + 1 to the 
     * end, given state Si at time t and the model λ.
     *
     * @param obs observation sequence o
     * @return beta[State][Time]
     */
    protected double[][] backwardProc(int[] obs) {
        int T = obs.length;
        double[][] bwd = new double[this.n][T];
        /* Initializacion */
        for (int i = 0; i < this.n; i++) {
            bwd[i][T - 1] = 1;
        }
        /* Induccion */
        for (int t = T - 2; t >= 0; t--) {
            for (int i = 0; i < this.n; i++) {
                bwd[i][t] = 0;
                for (int j = 0; j < this.n; j++) {
                    bwd[i][t] += (bwd[j][t + 1] * A[i][j] * B[j][obs[t + 1]]);
                }
            }
        }
        return bwd;
    }

    /**
     * Algoritmo Backward con escala, utiliza la variable scaling actualizada en
     * el método forwardScaling.
     *
     * @param obs
     * @return beta[State][Time]
     */
    protected double[][] backwardScaling(int[] obs) {
        int T = obs.length;
        double[][] beta = new double[this.n][T];
        //Inicializacion
        for (int i = 0; i < this.n; i++) {
            beta[i][T - 1] = this.scaling[T - 1] * 1;
        }
        //Induccion
        for (int t = T - 2; t >= 0; t--) {
            for (int i = 0; i < this.n; i++) {
                beta[i][t] = 0;
                for (int j = 0; j < this.n; j++) {
                    beta[i][t] += (beta[j][t + 1] * A[i][j] * B[j][obs[t + 1]]);
                }
                beta[i][t] = this.scaling[t] * beta[i][t];
            }
        }
        return beta;
    }

    /**
     * Calcula la probabilidad de que las observaciones sean generadas por el
     * modelo dado;
     *
     * @param obs
     * @return
     */
    public double observationProbability(int[] obs) {
        double[][] alpha = this.forwardScaling(obs);
        double prob = 0.0;
        int T = obs.length;
        for (int i = 0; i < this.n; i++) {
            prob += alpha[i][T - 1];
        }
        return prob;
    }
    /**
     * Calcula la probabilidad de que las observaciones sean generadas por el
     * modelo dado; dado el valor de alfa.
     *
     * @param obs
     * @param alpha
     * @return
     */
    public double observationProbability(int[] obs, double[][] alpha) {
        double prob = 0.0;
        int T = obs.length;
        for (int i = 0; i < this.n; i++) {
            prob += alpha[i][T - 1];
        }
        return prob;
    }

    /**
     * Computa el log-likelihood para las observaciones que fueron usadas en el
     * metodo forwardScaling
     *
     * @return
     */
    public double logLikelihoodObservation() {
        double logLikelihood = 0.0;
        int T = this.scaling.length;
        for (int t = 0; t < T; t++) {
            logLikelihood += Math.log(this.scaling[t]);
        }
        return -1.0 * logLikelihood;
    }

    /**
     * Returns the probability that a observation sequence O belongs to this
     * Hidden Markov Model without using the bayes classifier. Internally the
     * well known forward algorithm is used.
     *
     * @param obs observation sequence
     * @return probability that sequence o belongs to this hmm
     */
    public double getProbability(int[] obs) {
        double prob = 0.0;
        double[][] forward = this.forwardProc(obs);
        // add probabilities 
        for (int i = 0; i < this.n; i++) { // for every state 
            prob += forward[i][forward[i].length - 1];
        }
        return prob;
    }
    /**
     * Returns the probability that a observation sequence O belongs to this
     * Hidden Markov Model without using the bayes classifier. Internally the
     * well known forward algorithm is used. Given alpha.
     *
     * @param obs observation sequence
     * @param alpha
     * @return probability that sequence o belongs to this hmm
     */
    public double getProbability(int[] obs, double[][] alpha) {
        double prob = 0.0;
        double[][] forward = alpha;
        // add probabilities 
        for (int i = 0; i < this.n; i++) { // for every state 
            prob += forward[i][forward[i].length - 1];
        }
        return prob;
    }

    /**
     * Obtiene la probabilidad de estar en el estado State en el momento Time
     * dada una secuencia de observaciones para el modelo actual.
     *
     * @param state
     * @param time
     * @param obs
     * @return
     */
    public double getProbability(int state, int time, int[] obs) {
        double[][] gamma = this.getGamma(obs);
        return gamma[state][time];
    }

    /**
     * Calcula todas las probabilidades de estar en un Estado determinado(1ra
     * dimension) en un Tiempo determinado (2da dimension), dada una
     * observacion.
     *
     * @param obs
     * @return
     */
    protected double[][] getGamma(int[] obs) {
        int T = obs.length;
        double[][] gamma = new double[this.n][T];
        double[][] alpha = this.forwardProc(obs);
        double[][] beta = this.backwardProc(obs);
        double probObs = this.getProbability(obs);
        for (int i = 0; i < this.n; i++) {
            for (int t = 0; t < T; t++) {
                gamma[i][t] = alpha[i][t] * beta[i][t] / probObs;
            }
        }
        return gamma;
    }

    /**
     * Calcula todas las probabilidades de estar en un Estado determinado(1ra
     * dimension) en un Tiempo determinado (2da dimension), dada una
     * observacion. Reutiliza los valores de alfa, beta y probObs obtenidos en 
     * otro metodo.
     *
     * @param obs secuencia de observaciones
     * @param alpha
     * @param beta
     * @param probObs
     * @return
     */
    protected double[][] getGamma(int[] obs, double[][] alpha, double[][] beta, double probObs) {
        int T = obs.length;
        double[][] gamma = new double[this.n][T];
        for (int i = 0; i < this.n; i++) {
            for (int t = 0; t < T; t++) {
                gamma[i][t] = alpha[i][t] * beta[i][t] / probObs;
            }
        }
        return gamma;
    }

    /**
     * Probabilidad de estar en el estado S_i en el tiempo t y en el estado S_j
     * en el tiempo t+1, dado el modelo y la secuencia de observacion.
     *
     * @param obs
     * @return
     */
    protected double[][][] getXi(int[] obs) {
        int T = obs.length;

        double[][][] xi = new double[this.n][this.n][T];
        double[][] alpha = this.forwardProc(obs);
        double[][] beta = this.backwardProc(obs);
        double probObs = this.getProbability(obs);

        for (int i = 0; i < this.n; i++) {
            for (int j = 0; j < this.n; j++) {
                for (int t = 0; t < T; t++) {
                    xi[i][j][t] = alpha[i][t] * A[i][j] * B[j][obs[t + 1]] * beta[j][t + 1] / probObs;
                }
            }
        }
        return xi;
    }

    /**
     * Probabilidad de estar en el estado S_i en el tiempo t y en el estado S_j
     * en el tiempo t+1, dado el modelo y la secuencia de observacion.Reutiliza
     * los valores obtenidos de alpha, beta y probObs calculados en otro metodo.
     *
     * @param obs
     * @param alpha
     * @param beta
     * @param probObs
     * @return
     */
    protected double[][][] getXi(int[] obs, double[][] alpha, double[][] beta, double probObs) {
        int T = obs.length;
        double[][][] xi = new double[this.n][this.n][T];
        for (int i = 0; i < this.n; i++) {
            for (int j = 0; j < this.n; j++) {
                for (int t = 0; t < T-1; t++) {
                    xi[i][j][t] = alpha[i][t] * A[i][j] * B[j][obs[t + 1]] * beta[j][t + 1] / probObs;
                }
            }
        }
        return xi;
    }

    /**
     * Producto de los scaling desde 0 hasta t.
     * @param t Limite superior de la piatoria.
     * @return double
     */
    protected double getC(int t){
        double prom = 1.0;
        for (int i = 0; i < t; i++) {
            prom *= this.scaling[i];
        }
        return prom;
    }
    /**
     * Producto de los scaling desde t hasta T.
     * @param t
     * @return double
     */
    protected double getD(int t){
        double prom = 1.0;
        for (int i = 0; i < this.scaling.length; i++) {
            prom *= this.scaling[i];
        }
        return prom;
    }
    
    public void algorithmBaumWelch(int[] obs, double threshold){
        double error = Double.MAX_VALUE;
        double[][] alpha = this.forwardProc(obs);
        double[][] beta = this.backwardProc(obs);
        double prob = this.getProbability(obs,alpha);
        double[][] gamma = this.getGamma(obs, alpha, beta, prob);
        double[][][] xi = this.getXi(obs, alpha, beta, prob);
        double probNew = 0.0;
        while(error - threshold > 0){
            //actualizar pi
            Double[] new_pi = new Double[this.n];
            for (int i = 0; i < this.n; i++) {
                new_pi[i] = gamma[i][0]; 
            }
            this.pi = new_pi;
            //actualizar A
            Double[][] a = new Double[this.n][this.n];
            for (int i = 0; i < this.n; i++) {
                for (int j = 0; j < this.n; j++) {
                    double acumGanmma = 0.0, acumXi = 0.0;
                    for (int t = 0; t < obs.length-1; t++) {
                        acumGanmma += gamma[i][t];
                        acumXi += xi[i][j][t];
                    }
                    a[i][j] = acumXi/acumGanmma;
                }
            }
            this.A = a;
            //actualizar B
            Double[][] b = new Double[this.n][this.m];
            double sum = 0.0;
            for (int j = 0; j < this.n; j++) {
                for (int k = 0; k < this.m; k++) {
                    double acum_k = 0.0, acum = 0.0;
                    for (int t = 0; t < obs.length; t++) {
                        if(obs[t]==k){
                            acum_k += gamma[j][t];
                        }
                        acum += gamma[j][t];
                    }
                    b[j][k] = acum_k / acum;
                }
            }
            this.B = b;
            
            alpha = this.forwardProc(obs);
            beta = this.backwardProc(obs);
            probNew = this.getProbability(obs,alpha);
            gamma = this.getGamma(obs, alpha, beta, prob);
            xi = this.getXi(obs, alpha, beta, prob);
            error = Math.abs(prob-probNew);
            prob = probNew;
        }
    }
    
    public void algorithmBaumWelchScaling(int[] obs, double threshold, int maxIterations){
        double error = Double.MAX_VALUE;
        int it = 0;
        
        double[][] alpha = this.forwardScaling(obs);
        double[][] beta = this.backwardScaling(obs);
        double prob = this.getProbability(obs,alpha);
        double[][] gamma = this.getGamma(obs, alpha, beta, prob);
        double[][][] xi = this.getXi(obs, alpha, beta, prob);
        double probNew = 0.0;
        while(error - threshold > 0 && it < maxIterations){
            //actualizar pi
            Double[] new_pi = new Double[this.n];
            for (int i = 0; i < this.n; i++) {
                new_pi[i] = gamma[i][0]; 
            }
            this.pi = new_pi;
            //actualizar A
            Double[][] a = new Double[this.n][this.n];
            for (int i = 0; i < this.n; i++) {
                for (int j = 0; j < this.n; j++) {
                    double numerador = 0.0, denominador = 0.0;
                    for (int t = 0; t < obs.length-1; t++) {
                        numerador += alpha[i][t]*A[i][j]*B[j][obs[t+1]]*beta[j][t+1];
                        denominador += alpha[i][t]*beta[i][t]/this.scaling[t];
                    }
                    a[i][j] = numerador/denominador;
                }
            }
            this.A = a;
            //actualizar B
            Double[][] b = new Double[this.n][this.m];
            double sum = 0.0;
            for (int j = 0; j < this.n; j++) {
                for (int k = 0; k < this.m; k++) {
                    double acum_k = 0.0, acum = 0.0;
                    for (int t = 0; t < obs.length; t++) {
                        double actual = alpha[j][t]*beta[j][t]/this.scaling[t];
                        if(obs[t]==k){
                            acum_k += actual;
                        }
                        acum += actual;
                    }
                    b[j][k] = acum_k / acum;
                }
            }
            this.B = b;
            
            alpha = this.forwardProc(obs);
            beta = this.backwardProc(obs);
            probNew = this.getProbability(obs,alpha);
            gamma = this.getGamma(obs, alpha, beta, prob);
            xi = this.getXi(obs, alpha, beta, prob);
            error = Math.abs(prob-probNew);
            prob = probNew;
            it++;
        }
    }
    /**
     * Prints everything about this model, including all values. For debug
     * purposes or if you want to comprehend what happend to the model.
     *
     */
    public void print() {
        int numStates = this.n;
        int numObservations = this.m;
        System.out.println("Pi:");
        for (int i = 0; i < this.n; i++) {
            System.out.printf("%.2f ",pi[i]);
        }
        System.out.println("");
        System.out.println("A:");
        for (int i = 0; i < this.n; i++) {
            for (int j = 0; j < this.n; j++) {
                System.out.printf("%.2f ",A[i][j]);
            }
            System.out.println("");
        }
        System.out.println("B:");
        for (int i = 0; i < this.n; i++) {
            for (int j = 0; j < this.m; j++) {
                System.out.printf("%.2f ",B[i][j]);
            }
            System.out.println("");
        }
        /*
        DecimalFormat fmt = new DecimalFormat();
        fmt.setMinimumFractionDigits(5);
        fmt.setMaximumFractionDigits(5);
        for (int i = 0; i < numStates; i++) {
            System.out.println("pi(" + i + ") = " + fmt.format(pi[i]));
        }
        System.out.println("");
        for (int i = 0; i < numStates; i++) {
            for (int j = 0; j < numStates; j++) {
                System.out.println("a(" + i + "," + j + ") = "
                        + fmt.format(A[i][j]) + " ");
            }
            System.out.println("");
        }
        System.out.println("");
        for (int i = 0; i < numStates; i++) {
            for (int k = 0; k < numObservations; k++) {
                System.out.println("b(" + i + "," + k + ") = "
                        + fmt.format(B[i][k]) + " ");
            }
            System.out.println("");
        }
        */
    }

    //getters and setters
    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public Integer getM() {
        return m;
    }

    public void setM(Integer m) {
        this.m = m;
    }

    public Double[][] getA() {
        return A;
    }

    public void setA(Double[][] A) {
        this.A = A;
    }

    public Double[][] getB() {
        return B;
    }

    public void setB(Double[][] B) {
        this.B = B;
    }

    public Double[] getPi() {
        return pi;
    }

    public void setPi(Double[] pi) {
        this.pi = pi;
    }

}
