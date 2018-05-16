package cu.uci.gitae.mdem.utils;

import org.apache.commons.math3.exception.OutOfRangeException;

/**
 * Clase que implementa una clase generica para modelos ocultos de Markov El
 * modelo está definido como M = (A, B, pi), donde A es matrix de probabilidades 
 * de emision, B matrix de probabilidad de emision y pi vector de probabilidades
 * iniciales.
 *
 * @author angel
 */
public class HiddenMarkovModel {

    /**
     * Número de estados
     */
    Integer cantidadEstados;
    /**
     * Número de simbolos
     */
    Integer cantidadSimbolos;
    /**
     * probabilidades de emisión
     */
    private Double[][] B;
    /**
     * Probabilidades de transmisión
     */
    private Double[][] A;
    /**
     * Probabilidades del estado inicial
     */
    private Double[] pi;

    public HiddenMarkovModel(Integer cantidadSimbolos, Double[] probabilidades) throws Exception {
        this.init(null, probabilidades, null, HMMType.Ergodic);
        if (cantidadSimbolos <= 0) {
            throw new OutOfRangeException(cantidadSimbolos, 1, Integer.MAX_VALUE);
        }
        this.cantidadSimbolos = cantidadSimbolos;
        this.B = new Double[this.cantidadEstados][this.cantidadSimbolos];
        //Inicializar B con probabilidades uniformes
        for (int i = 0; i < cantidadEstados; i++) {
            for (int j = 0; j < cantidadSimbolos; j++) {
                this.B[i][j] = 1.0 / cantidadSimbolos;
            }
        }

    }

    public HiddenMarkovModel(Double[][] transiciones, Double[][] emisiones, Double[] probabilidades) throws Exception {
        this.init(transiciones, probabilidades, null, HMMType.Ergodic);
        this.cantidadSimbolos = emisiones[1].length; //revisar porque en 1 y no en 0
        this.B = emisiones;
    }

    public HiddenMarkovModel(Integer cantidadSimbolos, Integer cantidadEstados, HMMType tipo) throws Exception {
        this.init(null, null, cantidadEstados, tipo);
        if (cantidadSimbolos <= 0) {
            throw new OutOfRangeException(cantidadSimbolos, 1, Integer.MAX_VALUE);
        }
        this.cantidadSimbolos = cantidadSimbolos;
        this.B = new Double[cantidadEstados][cantidadSimbolos];
        //inicializa B con probabilidad uniforme
        for (int i = 0; i < cantidadEstados; i++) {
            for (int j = 0; j < cantidadSimbolos; j++) {
                this.B[i][j] = 1.0 / cantidadSimbolos;
            }
        }
    }

    private void init(Double[][] transiciones, Double[] probabilidades, Integer cantidadEstados, HMMType tipo) throws Exception {
        if (cantidadEstados != null) {
            if (cantidadEstados <= 0) {
                throw new OutOfRangeException(cantidadSimbolos, 1, Integer.MAX_VALUE);
            }
        } else {
            if (probabilidades != null) {
                cantidadEstados = probabilidades.length;
            } else if (transiciones != null) {
                cantidadEstados = transiciones[0].length;
            } else {
                throw new Exception("El número de estados no se puede determinar");
            }
        }
        int n = cantidadEstados;
        //matrix de transicion A
        if (transiciones != null) {
            if (transiciones[0].length != cantidadEstados) {
                throw new Exception("La matriz de transiciones debe tener las "
                        + "mismas dimensiones que el numeros de estados del modelo");
            }
            if (transiciones[0].length != transiciones[1].length) {
                throw new Exception("La matriz debe ser cuadrada");
            }
        } else {
            if (tipo == HMMType.Ergodic) {
                //Crear A usando una distribucion uniforme
                transiciones = new Double[n][n];
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        transiciones[i][j] = 1.0 / n;
                    }
                }
            } else {
                //crear A usando distribucion uniforme sin permitir transiciones
                //de regreso
                transiciones = new Double[n][n];
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        transiciones[i][j] = 1.0 / (n - i);
                    }
                }
            }
        }
        this.A = transiciones;
        //probabilidades iniciales
        if (probabilidades != null) {
            if (probabilidades.length != n) {
                throw new Exception("Las probabilidades iniciales debe tener la "
                        + "misma cantidad que el numero de estados");
            }
        } else {
            //crear pi como izquierda-a-derecha
            probabilidades = new Double[n];
            probabilidades[0] = 1.0;
        }
        this.pi = probabilidades;
    }

    //Getters and setters
    public Integer getCantidadEstados() {
        return cantidadEstados;
    }

    public void setCantidadEstados(Integer cantidadEstados) {
        this.cantidadEstados = cantidadEstados;
    }

    public Integer getCantidadSimbolos() {
        return cantidadSimbolos;
    }

    public void setCantidadSimbolos(Integer cantidadSimbolos) {
        this.cantidadSimbolos = cantidadSimbolos;
    }

    public Double[][] getB() {
        return B;
    }

    public void setB(Double[][] B) {
        this.B = B;
    }

    public Double[][] getA() {
        return A;
    }

    public void setA(Double[][] A) {
        this.A = A;
    }

    public Double[] getPi() {
        return pi;
    }

    public void setPi(Double[] pi) {
        this.pi = pi;
    }

    //Principales metodos
    /**
     * Calcula la secuencia más probable de estados ocultos que produce la
     * secuencia de observación dada. Problema de decoficación: Dado el HMM M =
     * (A,B,pi) y la secuencia de observación O = {o1,o2, ..., oK}, calcula la
     * secuencia más probable de estados ocultos Si que produce la secuencia de
     * observación O. Esto puede ser computado eficientemente usando el
     * algoritmo de Viterbi.
     *
     * @param observaciones Una secuencia de observaciones
     * @param probabilidad La probabilidad optimizada del estado
     * @return La secuencia de estados que con mayor probabilidad produce las
     * observaciones
     */
    public Integer[] decode(Integer[] observaciones, Double probabilidad) {
        return this.decode(observaciones, false, probabilidad);
    }

    /**
     * Calcula la secuencia más probable de estados ocultos que produce la
     * secuencia de observación dada. Problema de decoficación: Dado el HMM M =
     * (A,B,pi) y la secuencia de observación O = {o1,o2, ..., oK}, calcula la
     * secuencia más probable de estados ocultos Si que produce la secuencia de
     * observación O. Esto puede ser computado eficientemente usando el
     * algoritmo de Viterbi.
     *
     * @param observaciones Una secuencia de observaciones
     * @param logarithm Cuando true devuelve el log-likelihood, si es false
     * retorna el likelihood. Por defecto es false.
     * @param probabilidad La probabilidad optimizada del estado
     * @return La secuencia de estados que con mayor probabilidad produce las
     * observaciones
     */
    public Integer[] decode(Integer[] observaciones, boolean logarithm, Double probabilidad) {
        if (observaciones == null) {
            throw new NullPointerException("Las observaciones no puede ser null");
        }
        if (observaciones.length == 0) {
            probabilidad = 0.0;
            return new Integer[0];
        }
        // Viterbi-forward algorithm.
        int T = observaciones.length;
        int estados = this.cantidadEstados;
        int estadoMin;
        double pesoMin;
        double peso;

        Double[] pi = this.pi;
        Double[][] A = this.A;

        Integer[][] s = new Integer[cantidadEstados][T];
        Double[][] a = new Double[cantidadEstados][T];

        //base
        for (int i = 0; i < cantidadEstados; i++) {
            a[i][0] = -1.0 * Math.log(pi[i]) - Math.log(B[i][observaciones[0]]);
        }
        //induccion
        for (int t = 0; t < T; t++) {
            Integer observacion = observaciones[t];
            for (int j = 0; j < cantidadEstados; j++) {
                estadoMin = 0;
                pesoMin = a[0][t - 1] - Math.log(A[0][j]);
                for (int i = 0; i < cantidadEstados; i++) {
                    peso = a[i][t - 1] - Math.log(A[i][j]);
                    if (peso < pesoMin) {
                        estadoMin = i;
                        pesoMin = peso;
                    }
                }
                a[j][t] = pesoMin - Math.log(B[j][observacion]);
                s[j][t] = estadoMin;
            }
        }
        //encontrar el valor minimo para el tiempo T-1
        estadoMin = 0;
        pesoMin = a[0][T - 1];
        for (int i = 1; i < cantidadEstados; i++) {
            if (a[i][T - 1] < pesoMin) {
                estadoMin = i;
                pesoMin = a[i][T - 1];
            }
        }
        //trackback
        Integer[] path = new Integer[T];
        path[T - 1] = estadoMin;
        for (int t = T - 2; t >= 0; t--) {
            path[t] = s[path[t + 1]][t + 1];
        }
        //retornar la probabilidad de la secuencia como un parametro de salida (VER SI ES POSIBLE)
        probabilidad = logarithm ? -1.0 * pesoMin : Math.exp(-1.0 * pesoMin);
        //retornar el camino de Viterbi mas probable para la secuencia dada.
        return path;
    }

    /**
     * Calcula la probabilidad de que el modelo actual haya generado la
     * secuencia dada. Problema de evaluación: Dado un HMM M=(A,B,pi) y la
     * secuencia de observación O = {o1, o2, ..., oK}, calcula la probabilidad
     * de que el modelo M haya generado la secuencia O. Esto puede ser computado
     * eficientemente usando tanto con el algoritmo de Viterbi o el algoritmo
     * Forward.
     *
     * @param observaciones Una secuencia de observaciones
     * @return La probabilidad de esta secuencia haya sido generada por este
     * modelo.
     */
    public Double evaluate(Integer[] observaciones) {
        return this.evaluate(observaciones, false);
    }

    /**
     * Calcula la probabilidad de que el modelo actual haya generado la
     * secuencia dada. Problema de evaluación: Dado un HMM M=(A,B,pi) y la
     * secuencia de observación O = {o1, o2, ..., oK}, calcula la probabilidad
     * de que el modelo M haya generado la secuencia O. Esto puede ser computado
     * eficientemente usando tanto con el algoritmo de Viterbi o el algoritmo
     * Forward.
     *
     * @param observaciones Una secuencia de observaciones
     * @param logarithm True para devolver el log-likelihood, o False para
     * retornar el likelihood. Por defecto es false.
     * @return La probabilidad de esta secuencia haya sido generada por este
     * modelo.
     */
    public Double evaluate(Integer[] observaciones, boolean logarithm) {
        if (observaciones == null) {
            throw new NullPointerException("Las observaciones no puede ser null");
        }
        if (observaciones.length == 0) {
            return 0.0;
        }
        //algoritmo Forward
        Double likelihood = 0.0;
        Double[] coeficientes = new Double[observaciones.length];
        //computar las probabilidades hacia adelante (forward)
        //coeficientes = forwardCoeficientes(observaciones);
        forward(observaciones, coeficientes);
        for (int i = 0; i < coeficientes.length; i++) {
            likelihood += Math.log(coeficientes[i]);
        }
        //retornar la probabilidad de la secuencia
        return logarithm ? likelihood : Math.exp(likelihood);
    }

    /**
     * Ejecuta el algoritmo de aprendizaje Baum-Welch para modelos ocultos de
     * Markov Problea de aprendizaje: Dada la secuencia de obervaciones de
     * entrenamiento O = {o1, o2, ..., oK} y la estructura general del HMM
     * (número de estados ocultos y visibles) determina los parámetros de HMM
     * M=(A, B, pi) que mejor se ajustan a los datos de entrenamiento.
     *
     * @param observaciones La secuencia de observaciones que será usada para
     * entrenar el modelo
     * @param iteraciones El número máximo de iteraciones para ser ejectadas por
     * el algoritmo de entrenamiento. Si se especfica cero el algoritmo se
     * ejecutará hasta la convergencia del modelo promediando la probabilidad
     * respecto a un límite deseado.
     * @param tolerancia El límite L de convergencia de la probabilidad entre
     * dos iteraciones del algoritmo. El algoritmo se detendrá cuando el cambio
     * de la probabilidad durante dos iteraciones consecutivas no haya cambiado
     * mas que un L porciento de la probabilidad. Si es dejada en zero, el
     * algoritmo ignorará este parametro e iterará sobre un número fijo de
     * iteraciones especificado en el parámetro anterior.
     * @return El promedio de log-likelihood para las observaciones luego que el
     * modelo haya sido entrenado.
     */
    public Double learn(Integer[] observaciones, Integer iteraciones, Double tolerancia) throws Exception {
        return this.learn(new Integer[][]{observaciones}, iteraciones, tolerancia);
    }

    /**
     * Ejecuta el algoritmo de aprendizaje Baum-Welch para modelos ocultos de
     * Markov Problea de aprendizaje: Dada la secuencia de obervaciones de
     * entrenamiento O = {o1, o2, ..., oK} y la estructura general del HMM
     * (número de estados ocultos y visibles) determina los parámetros de HMM
     * M=(A, B, pi) que mejor se ajustan a los datos de entrenamiento.
     *
     * @param observaciones Un arreglo de secuencias de entrenamiento para
     * entrenar el modelo.
     * @param iteraciones El número máximo de iteraciones para ser ejectadas por
     * el algoritmo de entrenamiento. Si se especfica cero el algoritmo se
     * ejecutará hasta la convergencia del modelo promediando la probabilidad
     * respecto a un límite deseado.
     * @param tolerancia El límite L de convergencia de la probabilidad entre
     * dos iteraciones del algoritmo. El algoritmo se detendrá cuando el cambio
     * de la probabilidad durante dos iteraciones consecutivas no haya cambiado
     * mas que un L porciento de la probabilidad. Si es dejada en zero, el
     * algoritmo ignorará este parametro e iterará sobre un número fijo de
     * iteraciones especificado en el parámetro anterior.
     * @return El promedio de log-likelihood para las observaciones luego que el
     * modelo haya sido entrenado.
     */
    public Double learn(Integer[][] observaciones, Integer iteraciones, Double tolerancia) throws Exception {
        if (iteraciones == 0 && tolerancia == 0) {
            throw new Exception("Las iteraciones y tolerancia no pueden ser cero los dos");
        }
        // Baum-Welch algorithm.
        // The Baum–Welch algorithm is a particular case of a generalized expectation-maximization
        // (GEM) algorithm. It can compute maximum likelihood estimates and posterior mode estimates
        // for the parameters (transition and emission probabilities) of an HMM, when given only
        // emissions as training data.

        // The algorithm has two steps:
        //  - Calculating the forward probability and the backward probability for each HMM state;
        //  - On the basis of this, determining the frequency of the transition-emission pair values
        //    and dividing it by the probability of the entire string. This amounts to calculating
        //    the expected count of the particular transition-emission pair. Each time a particular
        //    transition is found, the value of the quotient of the transition divided by the probability
        //    of the entire string goes up, and this value can then be made the new value of the transition.
        int N = observaciones.length;
        int iteracionActual = 1;
        boolean parar = false;
        Double[] pi = this.pi;
        Double[][] A = this.A;
        //Inicializacion
        Double[][][][] epsilon = new Double[N][][][];
        Double[][][] gamma = new Double[N][][];
        for (int i = 0; i < N; i++) {
            int T = observaciones[i].length;
            epsilon[i] = new Double[T][cantidadEstados][cantidadEstados];
            gamma[i] = new Double[T][cantidadEstados];
        }
        // Calcula el log-likelihood inicial del modelo
        Double oldLikelihood = Double.MIN_VALUE;
        Double newLikelihood = 0.0;
        do { // Hasta que converge o se alcance al maximo de iteraciones
            //Para cada secuencia en las observaciones de entrada
            for (int i = 0; i < N; i++) {
                Integer[] secuencia = observaciones[i];
                int T = secuencia.length;
                Double[] scaling = new Double[T];
                /*1er paso - Calculando la probabilidad hacia adelante y 
                            la probabilidad hacia atras de cada estado del HMM*/
                Double[][] fwd = forward(secuencia, scaling);
                Double[][] bwd = backward(secuencia, scaling);
                
                //2do paso - Determinar la frecuencia del par de valores 
                //trancicion-emision y dividirlo por la probabilidad de toda la cadena.
                
                // Calculate gamma values for next computations
                for (int t = 0; t < T; t++) {
                    double s = 0.0;
                    for (int k = 0; k < cantidadEstados; k++) {
                        gamma[i][t][k] = fwd[t][k]*bwd[t][k];
                        s += gamma[i][t][k];
                    }
                    if(s!=0){//Scaling
                        for (int k = 0; k < cantidadEstados; k++) {
                            gamma[i][t][k] /= s;
                        }
                    }
                }
                // Calculate epsilon values for next computations
                for (int t = 0; t < T-1; t++) {
                    double s = 0.0;
                    for (int k = 0; k < cantidadEstados; k++) {
                        for (int l = 0; l < cantidadEstados; l++) {
                            s += epsilon[i][t][k][l] = fwd[t][k]*A[k][l]*bwd[t+1][l]*B[l][secuencia[t+1]];
                        }
                    }
                    if(s!=0){ //Scaling
                        for (int k = 0; k < cantidadEstados; k++) {
                            for (int l = 0; l < cantidadEstados; l++) {
                                epsilon[i][t][k][l] /= s;
                            }
                        }
                    }
                }
                //Compute log-likelihood for the given sequence
                for (int t = 0; t < scaling.length; t++) {
                    newLikelihood += Math.log(scaling[t]);
                }
            }
            // Average the likelihood for all sequences
            newLikelihood /= observaciones.length;
            // Check if the model has converged or we should stop
            if(this.compruebaConvergencia(oldLikelihood, newLikelihood, iteracionActual, iteraciones, tolerancia)){
                parar = true;
            }else{
                // 3. Continue with parameter re-estimation
                iteracionActual++;
                oldLikelihood = newLikelihood;
                newLikelihood = 0.0;
                // 3.1 Re-estimation of initial state probabilities 
                for (int k = 0; k < cantidadEstados; k++) {
                    double sum = 0.0;
                    for (int i = 0; i < N; i++) {
                        sum += gamma[i][0][k];
                    }
                    pi[k] = sum / N;
                }
                // 3.2 Re-estimation of transition probabilities 
                for (int i = 0; i < cantidadEstados; i++) {
                    for (int j = 0; j < cantidadEstados; j++) {
                        double den = 0.0, num = 0;
                        for (int k = 0; k < N; k++) {
                            int T = observaciones[k].length;
                            for (int l = 0; l < T-1; l++) {
                                num += epsilon[k][l][i][j];
                            }
                            for (int l = 0; l < T-1; l++) {
                                den += gamma[k][l][i];
                            }
                        }
                        A[i][j] = (den!=0)?num/den:0.0;
                    }
                }
                // 3.3 Re-estimation of emission probabilities
                for (int i = 0; i < cantidadEstados; i++) {
                    for (int j = 0; j < cantidadSimbolos; j++) {
                        double den=0.0, num = 0.0;
                        for (int k = 0; k < N; k++) {
                            int T = observaciones[k].length;
                            for (int l = 0; l < T; l++) {
                                if(observaciones[k][l] == j){
                                    num += gamma[k][l][i];
                                }
                            }
                            for (int l = 0; l < T; l++) {
                                den += gamma[k][l][i];
                            }
                        }
                        // avoid locking a parameter in zero.
                        B[i][j] = (num==0)?1e-10:num/den;
                    }
                }
            }
        } while (!parar);
        // Returns the model average log-likelihood
        return newLikelihood;
    }

    /**
     * Baum-Welch forward pass (with scaling) Reference:
     * http://courses.media.mit.edu/2010fall/mas622j/ProblemSets/ps4/tutorial.pdf
     *
     * @param observaciones
     * @param c
     * @return
     */
    private Double[][] forward(Integer[] observaciones, Double[] c) {
        int T = observaciones.length;
        Double[] pi = this.pi;
        Double[][] A = this.A;
        Double[][] fwd = new Double[T][cantidadEstados];
        if (c.length != T) {
            c = new Double[T];
        }
        //1. Inicializacion
        for (int i = 0; i < cantidadEstados; i++) {
            fwd[0][i] = pi[i] * B[i][observaciones[0]];
            c[0] += fwd[0][i];
        }
        if (c[0] != 0) {//Scaling
            for (int i = 0; i < cantidadEstados; i++) {
                fwd[0][i] = fwd[0][i] / c[0];
            }
        }
        //2.Induccion
        for (int t = 0; t < T; t++) {
            for (int i = 0; i < cantidadEstados; i++) {
                double p = B[i][observaciones[t]];
                double sum = 0.0;
                for (int j = 0; j < cantidadEstados; j++) {
                    sum += fwd[t - 1][j] * A[j][i];
                }
                fwd[t][i] = sum * p;
                c[t] += fwd[t][i];
            }
            if (c[0] != 0) {//Scaling
                for (int i = 0; i < cantidadEstados; i++) {
                    fwd[t][i] = fwd[t][i] / c[t];
                }
            }
        }
        return fwd;
    }

    /**
     * Baum-Welch forward pass (with scaling) Reference:
     * http://courses.media.mit.edu/2010fall/mas622j/ProblemSets/ps4/tutorial.pdf
     *
     * @param observaciones
     * @param c
     * @return coeficientes
     */
    /*private Double[] forwardCoeficientes(Integer[] observaciones){
        int T = observaciones.length;
        Double[] pi = this.pi;
        Double[][] A = this.A;
        Double[][] fwd = new Double[T][cantidadEstados];
        Double[] c = new Double[T];
        //1. Inicializacion
        for (int i = 0; i < cantidadEstados; i++) {
            fwd[0][i] = pi[i]*B[i][observaciones[0]];
            c[0]+= fwd[0][i];
        }
        if(c[0]!=0){//Scaling
            for (int i = 0; i < cantidadEstados; i++) {
                fwd[0][i] = fwd[0][i]/c[0];
            }
        }
        //2.Induccion
        for (int t = 0; t < T; t++) {
            for (int i = 0; i < cantidadEstados; i++) {
                double p = B[i][observaciones[t]];
                double sum = 0.0;
                for (int j = 0; j < cantidadEstados; j++) {
                    sum += fwd[t-1][j]*A[j][i];
                }
                fwd[t][i] = sum*p;
                c[t] += fwd[t][i];
            }
            if(c[0]!=0){//Scaling
                for (int i = 0; i < cantidadEstados; i++) {
                    fwd[t][i] = fwd[t][i]/c[t];
                }
            }
        }
        return c;
    }*/

    /**
     * Baum-Welch backward pass (with scaling)
     * Reference: http://courses.media.mit.edu/2010fall/mas622j/ProblemSets/ps4/tutorial.pdf
     * @param observaciones
     * @param c
     * @return 
     */
    private Double[][] backward(Integer[] observaciones, Double[] c) {
        int T = observaciones.length;
        Double[] pi = this.pi;
        Double[][] A = this.A;
        Double[][] bwd = new Double[T][cantidadEstados];
        // Para cada variable de regreso (backward), usamos el mismo factor de scala
        //para cada tiempo t como usamos para las variables forward.

        // 1. Initialization
        for (int i = 0; i < cantidadEstados; i++) {
            bwd[T-1][i] = 1.0 / c[T-1];
        }
        // 2. Inducción
        for (int t = T - 2; t >=0; t--) {
            for (int i = 0; i < cantidadEstados; i++) {
                Double sum = 0.0;
                for (int j = 0; j < cantidadEstados; j++) {
                    sum += A[i][j]*B[j][observaciones[t+1]]*bwd[t+1][j];
                }
                bwd[t][i] += sum / c[t];
            }
        }
        return bwd;
    }
    
    private boolean compruebaConvergencia(Double oldLikelihood, Double newLikelihood, Integer iteracionActual, Integer maxIteraciones, Double tolerancia){
        // Actualiza y verifica el criterio de parada
        if(tolerancia > 0){
            // Stopping criteria is likelihood convergence
            if(Math.abs(oldLikelihood-newLikelihood)<=tolerancia){
                return true;
            }
            if(maxIteraciones > 0){
                // Maximum iterations should also be respected
                if(iteracionActual >= maxIteraciones){
                    return true;
                }
            }
        }else{
            // Stopping criteria is number of iterations
            if(iteracionActual.equals(maxIteraciones))
                return true;
        }
        // Check if we have reached an invalid state
        return Double.isNaN(newLikelihood) || Double.isInfinite(newLikelihood);
    }
    

}
