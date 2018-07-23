package cu.uci.gitae.mdem.bkt;

import cu.uci.gitae.mdem.bkt.parametersFitting.Parametros;
import java.util.ArrayList;
import java.util.List;

/**
 * Algoritmo BKT clasico. Calcula la probabilidad de dominar una habilidad dada
 * una secuencia de respuestas (correctas o no) a dicha habilidad.
 *
 * @author angel
 */
public class BKT {

    /**
     * Probabilidad inicial de dominar la habilidad
     */
    private Double pL0;
    /**
     * Probabilidad de adivinar la respuesta del item.
     */
    private Double pGuess;
    /**
     * Probabilidad de que se equivoque.
     */
    private Double pSlip;
    /**
     * Probabilidad de aprender la habilidad al responder un item
     */
    private Double pT;
    /**
     * Listado de items de respuestas de la habilidad
     */
    private List<Item> items;

    public BKT() {
        this.items = new ArrayList<>();
    }

    public BKT(Double pL0, Double pGuess, Double pSlip, Double pT) {
        this.pL0 = pL0;
        this.pGuess = pGuess;
        this.pSlip = pSlip;
        this.pT = pT;
        this.items = new ArrayList<>();
    }
    
    public BKT(Parametros param){
        this.pL0 = param.getL0();
        this.pT = param.getT();
        this.pGuess = param.getG();
        this.pSlip = param.getS();
    }

    public BKT(Double pL0, Double pGuess, Double pSlip, Double pT, List<Item> items) {
        this.pL0 = pL0;
        this.pGuess = pGuess;
        this.pSlip = pSlip;
        this.pT = pT;
        this.items = items;
    }

    /**
     * Metodo para establecer el conjunto de respuestas para esa habilidad.
     *
     * @param items
     */
    public void setItems(List<Item> items) {
        this.items = items;
    }

    /**
     * Metodo que se encarga de ejecutar el algoritmo BKT dado una lista de
     * Item.
     *
     * @return
     */
    public Double execute() {
        Double pL[] = new Double[this.items.size() + 1];
        pL[0] = this.pL0;
        Double pLParcial;
        for (int i = 1; i <= items.size(); i++) {
            Item actual = this.items.get(i - 1);
            if (actual.isCorrecto()) {
                pLParcial = (pL[i - 1] * (1 - pSlip)) / (pL[i - 1] * (1 - pSlip) + (1 - pL[i - 1]) * pGuess);
            } else {
                pLParcial = (pL[i - 1] * pSlip) / (pL[i - 1] * pSlip + (1 - pL[i - 1]) * (1 - pGuess));
            }
            pL[i] = pLParcial + ((1 - pLParcial) * pT);
        }
        return pL[items.size()];
    }
}
