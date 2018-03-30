package cu.uci.gitae.mdem.utils;

/**
 * Tipos de Modelo Oculto de Markov
 * @author angel
 */
public enum HMMType {
    /**
     * Especifica un modelo totalmente conectado en el cual todos los estados
     * son alcanzables de todos los otros estados.
     */
    Ergodic,
    /**
     * Especifica un modelo donde solamente los estados de transicion hacia 
     * adelante son permitidos.
     */
    Forward
}
