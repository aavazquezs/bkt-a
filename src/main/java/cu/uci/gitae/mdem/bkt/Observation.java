package cu.uci.gitae.mdem.bkt;

/**
 * POJO para representar una observacion
 * @author angel
 */
public class Observation {
    private boolean isCorrecto;
    private String estudianteId;
    private String problemaId;
    private String habilidad;

    public Observation(boolean isCorrecto, String estudianteId, String problemaId, String habilidad) {
        this.isCorrecto = isCorrecto;
        this.estudianteId = estudianteId;
        this.problemaId = problemaId;
        this.habilidad = habilidad;
    }

    public Observation() {
    }

    public boolean isIsCorrecto() {
        return isCorrecto;
    }

    public void setIsCorrecto(boolean isCorrecto) {
        this.isCorrecto = isCorrecto;
    }

    public String getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(String estudianteId) {
        this.estudianteId = estudianteId;
    }

    public String getProblemaId() {
        return problemaId;
    }

    public void setProblemaId(String problemaId) {
        this.problemaId = problemaId;
    }

    public String getHabilidad() {
        return habilidad;
    }

    public void setHabilidad(String habilidad) {
        this.habilidad = habilidad;
    }
}
