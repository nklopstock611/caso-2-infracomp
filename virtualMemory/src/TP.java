import java.util.HashMap;

public class TP {
    
    private static HashMap<Integer, Boolean> tp = new HashMap<Integer, Boolean>(); // true es que está en RAM, false es que está en disco.
    private Integer n;

    public TP(Integer pN) {
        this.n = pN;
        for (int i = 0; i < this.n; i++) {
            tp.put(i, false);
        }
    }

    public HashMap<Integer, Boolean> getHashTP() {
        return tp;
    }

    /*
     * Funciones para imprimir estructuras
     */

    public void loopTP() {
        for (int i = 0; i < this.n; i++) {
            System.out.println("TP[" + i + "] = " + tp.get(i));
        }
    }

    /*
     * Lógica de la TP
     */

    /**
     * Actualiza la TP. Si la dirección está en RAM, la actualiza a true. Si está en
     * disco, la actualiza a false.
     * En caso de que la RAM no tenga espacio y haya reemplazado alguna página, se
     * actualiza la TP, dejando en false la página que se sacó de la RAM.
     * 
     * @param dir
     * @param estadoRAM
     * @param dirVieja
     */
    public synchronized void actualizar(Integer dir, Boolean estadoRAM, Integer dirVieja) {
        if (estadoRAM) {
            tp.put(dir, true);
        }
        else {
            tp.put(dirVieja, false);
            tp.put(dir, true);
        }
    }
}
