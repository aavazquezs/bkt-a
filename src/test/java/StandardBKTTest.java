
import cu.uci.gitae.mdem.bkt.BKT;
import cu.uci.gitae.mdem.utils.LoadTSV;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author angel
 */
public class StandardBKTTest {

    String pathToDataset = "./data/dataset.tsv";
    List<String[]> dataset;

    public StandardBKTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void loadTSV() throws FileNotFoundException {
        dataset = LoadTSV.loadTSV(pathToDataset);
        assertEquals(5105, dataset.size());
        /*
         dataset.forEach(t->{
             Arrays.asList(t).forEach(x->{
                 System.out.print(x+"\t");
             });
             System.out.println("");
         });*/
    }

    @Test
    public void cargarHabilidadEspecificaPorEstudiante() throws FileNotFoundException {
        dataset = LoadTSV.loadTSV(pathToDataset);
        String estudianteId = "Stu_02ee1b3f31a6f6a7f4b8012298b2395e";
        String habilidad = "ALT:PARALLELOGRAM-AREA";
        assertTrue(dataset.size() > 0); //que haya cargado los datos
        assertEquals(4, dataset.get(0).length); //que tenga 4 columnas

        List<String[]> filasDeHabilidad = dataset.stream()
                .filter(row -> {
                    return row[1].equals(estudianteId) && row[3].equals(habilidad);
                })
                .collect(Collectors.toList());
        assertEquals(16, filasDeHabilidad.size());
    }

    @Test
    public void convertStringArrayToItem() throws FileNotFoundException {
        dataset = LoadTSV.loadTSV(pathToDataset);
        String estudianteId = "Stu_02ee1b3f31a6f6a7f4b8012298b2395e";
        String habilidad = "ALT:PARALLELOGRAM-AREA";

        List<BKT.Item> items = dataset.stream()
                .filter(row -> { //filtra las entradas para obtener las del estudianteId y la habilidad concreta
                    return row[1].equals(estudianteId) && row[3].equals(habilidad);
                })
                .map((String[] t) -> { //convierte cada entrada de un arreglo de String a un Item
                    BKT.Item item = new BKT.Item(t[0].equalsIgnoreCase("1"), habilidad);
                    return item;
                })
                .collect(Collectors.toList());
        assertEquals(16, items.size()); //comprueba que se obtuvieron los 16 elementos
        items.forEach(item -> {
            assertTrue(item instanceof BKT.Item); //comprueba que cada elemento sea del tipo Item
        });
    }

    @Test
    public void habilidadesPorEstudiante() throws FileNotFoundException {
        dataset = LoadTSV.loadTSV(pathToDataset);
        String estudianteId = "Stu_02ee1b3f31a6f6a7f4b8012298b2395e";
        List<String> habilidades = dataset
                .stream()
                .filter(row -> {
                    return row[1].equals(estudianteId);
                })
                .map((String[] t) -> {
                    return t[3];
                })
                .distinct()
                .collect(Collectors.toList());
        //habilidades.forEach(System.out::println);
        assertEquals(15, habilidades.size());
    }

    @Test
    public void estudiantesEnDataset() throws FileNotFoundException {
        dataset = LoadTSV.loadTSV(pathToDataset);
        List<String> estudiantes = dataset
                .stream()
                .map((String[] t) -> {
                    return t[1];
                })
                .distinct()
                .collect(Collectors.toList());
        assertEquals(60, estudiantes.size());//comprueba que haya 60 estudiantes distintos en el dataset
        //estudiantes.forEach(System.out::println);
    }
    @Test
    public void estimarUnaHabilidadDeUnEstudiante() throws FileNotFoundException{
        dataset = LoadTSV.loadTSV(pathToDataset);
        String estudianteId = "Stu_02ee1b3f31a6f6a7f4b8012298b2395e";
        String habilidad = "ALT:PARALLELOGRAM-AREA";

        List<BKT.Item> items = dataset.stream()
                .filter(row -> { //filtra las entradas para obtener las del estudianteId y la habilidad concreta
                    return row[1].equals(estudianteId) && row[3].equals(habilidad);
                })
                .map((String[] t) -> { //convierte cada entrada de un arreglo de String a un Item
                    BKT.Item item = new BKT.Item(t[0].equalsIgnoreCase("1"), habilidad);
                    return item;
                })
                .collect(Collectors.toList());
        BKT bkt = new BKT(0.3, 0.3, 0.3, 0.2, items);
        Double probabilidad = bkt.execute();
        //System.out.println("La probabilidad de la habilidad "+habilidad+" del estudiante "+estudianteId+" es: "+probabilidad);
        assertEquals(new Double(0.9999894763741958), probabilidad);
    }
}
