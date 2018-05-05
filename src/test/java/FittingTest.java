
import cu.uci.gitae.mdem.bkt.BKT;
import cu.uci.gitae.mdem.bkt.Item;
import cu.uci.gitae.mdem.bkt.parametersFitting.BruteForceFitting;
import cu.uci.gitae.mdem.bkt.parametersFitting.Parametros;
import cu.uci.gitae.mdem.utils.LoadTSV;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
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
public class FittingTest {

    String pathToFile;

    public FittingTest() {
        pathToFile = "./data/TestData.txt";
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

    //@Test 
    public void cargarDataset() throws FileNotFoundException{
        List<String[]> filas = LoadTSV.loadTSV(pathToFile);
        List<Item> items = filas
                .stream()
                .map((String[] fila) -> {
                    Item item = new Item(fila[2], fila[1], fila[5].equals("1"), fila[3]);
                    return item;
                })
                .collect(Collectors.toList());
        assertEquals(36, items.size());
        /**/
        items.forEach(item->{
            System.out.println(item.getEstudiante()+"   "+item.getHabilidad()+" "+item.getProblem()+"   "+ (item.isCorrecto()?"1":"0"));
        });
        
    }
    
    @Test public void secuenciaPorHabilidad() throws FileNotFoundException{
        List<String[]> filas = LoadTSV.loadTSV(pathToFile);
        List<Item> items = filas
                .stream()
                .map((String[] fila) -> {
                    Item item = new Item(fila[2], fila[1], fila[5].equals("1"), fila[3]);
                    return item;
                })
                .collect(Collectors.toList());
        assertEquals(36, items.size());
        List<String> habilidades = items.stream().parallel()
                .map(i->{
                    return i.getHabilidad();
                })
                .distinct()
                .collect(Collectors.toList());
        
        HashMap<String, List<Integer>> map = new HashMap();
        
        for(String habilidad: habilidades){
            List<Integer> secuencia = items.stream().parallel()
                    .filter(i->{
                        return i.getHabilidad().equals(habilidad);
                    })
                    .map(i->{
                        return i.isCorrecto()?1:0;
                    })
                    .collect(Collectors.toList());
            map.put(habilidad, secuencia);
        }
        
        //imprimir secuencia de respuestas por habilidad
        map.forEach((k,v)->{
            System.out.print(k);
            v.forEach(i->{
                System.out.print(" "+i);
            });
            System.out.println("");
        });
        
    }

    //@Test
    public void bruteForceFitting() throws FileNotFoundException {
        List<String[]> filas = LoadTSV.loadTSV(pathToFile);
        List<Item> items = filas
                .stream()
                .map((String[] fila) -> {
                    Item item = new Item(fila[2], fila[1], fila[5].equals("1"), fila[3]);
                    return item;
                })
                .collect(Collectors.toList());
        BruteForceFitting bff = new BruteForceFitting(false, true, false);
        Map<String, Parametros> resultado = bff.fitParameters(items);
        resultado.forEach((llave, valor) -> {
            System.out.print(llave + ": ");
            System.out.print(valor.getL0() + " ");
            System.out.print(valor.getG() + " ");
            System.out.print(valor.getS() + " ");
            System.out.println(valor.getT() + " ");
            
        });
    }
    
}
