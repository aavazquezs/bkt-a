package cu.uci.gitae.mdem.metrics;

/**
 * Matrix de confusion
 * @author angel
 */
public class ConfusionMatrix {
    private Integer truePositives;
    private Integer falsePositives;
    private Integer falseNegatives;
    private Integer trueNegatives;

    public ConfusionMatrix() {
        this.trueNegatives = 0;
        this.truePositives = 0;
        this.falseNegatives = 0;
        this.falsePositives = 0;
    }

    public ConfusionMatrix(Integer truePositives, Integer falsePositives, Integer falseNegatives, Integer trueNegatives) {
        this.truePositives = truePositives;
        this.falsePositives = falsePositives;
        this.falseNegatives = falseNegatives;
        this.trueNegatives = trueNegatives;
    }
    
    public void incrementarTrueNegatives(){
        this.trueNegatives++;
    }
    
    public void incrementarTruePositives(){
        this.truePositives++;
    }
    
    public void incrementarFalseNegatives(){
        this.falseNegatives++;
    }
    
    public void incrementarFalsePositives(){
        this.falsePositives++;
    }
    
    /**
     * Obtiene el total de clasificaciones
     * @return 
     */
    public Integer getTotal(){
        return this.getTotalNegatives()+this.getTotalPositives();
    }
    /**
     * Obtiene el total de elementos clasificados como positivos.
     * @return 
     */
    public Integer getTotalPositives(){
        return truePositives + falseNegatives;
    }
    /**
     * Obtiene el total de elementos clasificados como negativos.
     * @return 
     */
    public Integer getTotalNegatives(){
        return trueNegatives + falsePositives;
    }
    /**
     * Obtiene el radio de falsos positivos.
     * @return 
     */
    public Double getFalsePositiveRate(){
        return this.falsePositives.doubleValue()/this.getTotalNegatives();
    }
    /**
     * Obtiene el radio de falsos positivos.
     * @return 
     */
    public Double getFPR(){
        return this.getFalsePositiveRate();
    }
    /**
     * Obtiene el radio de verdaderos positivos.
     * @return 
     */
    public Double getTruePositiveRate(){
        return this.truePositives.doubleValue()/this.getTotalPositives();
    }
    
    public Double getTPR(){
        return this.getTruePositiveRate();
    }
    /**
     * Obtiene el radio de falsos positivos.
     * @return 
     */
    public Double getFalseNegativeRate(){
        return this.falseNegatives.doubleValue()/this.getTotalPositives();
    }
    /**
     * Obtiene el radio de falsos positivos.
     * @return 
     */
    public Double getFNR(){
        return this.getFalseNegativeRate();
    }
    /**
     * Obtiene el radio de verdaderos negativos.
     * @return 
     */
    public Double getTrueNegativeRate(){
        return this.trueNegatives.doubleValue()/this.getTotalNegatives();
    }
    /**
     * Obtiene el radio de verdaderos negativos.
     * @return 
     */
    public Double getTNR(){
        return this.getTrueNegativeRate();
    }
    /**
     * Obtiene la especificidad.  1 - FPR
     * @return 
     */
    public Double getSpecificity(){
        return 1.0 - this.getFalsePositiveRate();
    }
    /**
     * Obtiene la precición.
     * @return 
     */
    public Double getPrecision(){
        return this.truePositives.doubleValue()/(this.truePositives + this.falsePositives);
    }
    /**
     * Obtiene la memoria (recall).
     * @return 
     */
    public Double getRecall(){
        return truePositives.doubleValue()/this.getTotalPositives();
    }
    /**
     * Obtiene la sensitividad. (Los mismo que recall).
     * @return 
     */
    public Double getSensitivity(){
        return this.getRecall();
    }
    /**Obtiene la exactitud
     * 
     * @return 
     */
    public Double getAccuracy(){
        return (truePositives.doubleValue() + trueNegatives.doubleValue())/this.getTotal();
    }
    /**
     * Obtiene la métrica F.
     * @return 
     */
    public Double getFMeasure(){
        return 2.0/(1.0/this.getPrecision() + 1.0/this.getRecall());
    }
    /**
     * Obtiene el radio de probabilidad positivo
     * @return 
     */
    public Double getPositiveLikelihoodRatio(){
        return this.getTPR()/this.getFPR();
    }
     /**
     * Obtiene el radio de probabilidad positivo
     * @return 
     */
    public Double getLRP(){
        return this.getPositiveLikelihoodRatio();
    }
    /**
     * Obtiene el ratio de probabilidad negativo.
     * @return 
     */
    public Double getNegativeLikelihoodRatio(){
        return this.getFNR()/this.getTNR();
    }
    /**
     * Obtiene el ratio de probabilidad negativo.
     * @return 
     */
    public Double getLRN(){
        return this.getNegativeLikelihoodRatio();
    }
    
    //setters and getters
    public Integer getTruePositives() {
        return truePositives;
    }

    public void setTruePositives(Integer truePositives) {
        this.truePositives = truePositives;
    }

    public Integer getFalsePositives() {
        return falsePositives;
    }

    public void setFalsePositives(Integer falsePositives) {
        this.falsePositives = falsePositives;
    }

    public Integer getFalseNegatives() {
        return falseNegatives;
    }

    public void setFalseNegatives(Integer falseNegatives) {
        this.falseNegatives = falseNegatives;
    }

    public Integer getTrueNegatives() {
        return trueNegatives;
    }

    public void setTrueNegatives(Integer trueNegatives) {
        this.trueNegatives = trueNegatives;
    }
  
}
