package cu.uci.gitae.mdem.bkt.parametersFitting;

import cu.uci.gitae.mdem.bkt.BKT;
import java.util.List;

/**
 * Clase abstracta que 
 * @author angel
 */
public abstract class FittingMethodImpl implements FittingMethod {
    /**
     * Probabilidad inicial de dominar la habilidad
     */
    protected Double pL0;
    /**
     * Probabilidad de adivinar la respuesta si no domina la habilidad
     */
    protected Double pG;
    /**
     * Probabilidad de equivocarse si domina la habilidad
     */
    protected Double pS;
    /**
     * Probabilidad de dominar la habilidad durante la resolución del problema
     */
    protected Double pT;

 
    //getters
    public Double getpL0() {
        return pL0;
    }

    public Double getpG() {
        return pG;
    }

    public Double getpS() {
        return pS;
    }

    public Double getpT() {
        return pT;
    }
    
    
}
