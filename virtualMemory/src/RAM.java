import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class RAM {

    public static HashMap<Integer, Integer> ram = new HashMap<Integer, Integer>();
    private static HashMap<Integer, Integer> bits = new HashMap<Integer, Integer>();
    private Integer n;

    public RAM(Integer pN) {
        this.n = pN;
        for (int i = 0; i < this.n; i++) {
            ram.put(i, null);
            bits.put(i, 0);
        }
    }

    public HashMap<Integer, Integer> getHashRAM() {
        return ram;
    }

    public HashMap<Integer, Integer> getHashBITS() {
        return bits;
    }
        
    public void setHashBITS(HashMap<Integer, Integer> map) {
        bits = map;
    }

    /*
     * Funciones para imprimir estructuras
     */

    public void loopRAM() {
        for (int i = 0; i < ram.size(); i++) {
            System.out.println("RAM[" + i + "] = " + ram.get(i));
        }
    }

    public void loopBITS() {
        for (int i = 0; i < bits.size(); i++) {
            if (bits.get(i) != null) {
                System.out.println("BITS[" + i + "] = " + Integer.toBinaryString(bits.get(i)));
            }
        }
    }

    /*
     * Lógica de la RAM
     */

    /**
     * Ejecuta el algoritmo de envejecimiento.
     * Recorre la tabla de Hash de bits y hace un corrimiento a la derecha
     * de cada secuencia de bits. El menor valor en Integer que se encuentre
     * es el que se debe sacar en caso de que no haya espacio en RAM.
     */
    public synchronized void envejecimiento() {
        for (int i = 0; i < this.n; i++) {
            Integer valor = bits.get(i);
            valor = valor >> 1;
            bits.put(i, valor);
        }
    }

    /**
     * Revisa si la RAM tiene espacio o no.
     * 
     * @return hayEspacio : false si la TLB esta llena, true si no lo esta.
     */
    public Boolean espacio() {
        Boolean lleno = false;
        for (int i = 0; i < this.n; i++) {
            if (ram.get(i) == null) {
                lleno = true;
            }
        }

        return lleno;
    }

    /**
     * Actualiza la RAM. Si hay espacio, agrega la dirección de la página a la RAM,
     * sino, busca el menor valor en la tabla de Hash de bits y lo reemplaza por el nuevo.
     * 
     * @param dir : dirección actual que se quiere cargar.
     * @return dirVieja : dirección que se reemplazó.
     */
    public synchronized Integer actualizar(Integer dir) {      
        Integer dirVieja = null; 
        Boolean hayEspacio = espacio();
        if (hayEspacio) {
            for (int i = 0; i < this.n; i++) {
                if (ram.get(i) == null) {
                    ram.put(i, dir);
                    bits.put(dir, 0);
                    break;
                }
            }
        }
        else {
            ArrayList<Integer> menores = new ArrayList<Integer>();
            Integer menor = Integer.MAX_VALUE;
            Integer indice = 0;
            for (int i = 0; i < this.n; i++) {
                if (bits.get(i) < menor) {
                    menor = bits.get(i); // busca cuál es el menor. El Integer menor es el menos usado hasta es momento.
                    indice = i; // guarda el índice del menor.
                }
                else if (bits.get(i) == menor) {
                    indice = i;
                    menores.add(indice); // si hay menores repetidos, los guarda en un arreglo para revisarlos.
                }
            }

            // saca un índice aleatorio entre los menores (en caso de que haya más de uno).
            Random rand = new Random();
            Integer randInt = rand.nextInt(menores.size());
            menor = menores.get(randInt);
            indice = menores.indexOf(menor);

            dirVieja = ram.get(indice);
            ram.put(indice, dir); // actualiza el valor del elemento eliminado.
            bits.put(dir, 0); // actualiza y reinicia el bitstring del elemento eliminado.

            // si la RAM está llena y se reemplaza un marco, se debe actualizar la TLB también:

            synchronized(TLB.fifo) {
                synchronized(TLB.tlb) {
                    for (int i = 0; i < this.n; i++) {
                        Integer valor = TLB.tlb.get(i);
                        if (valor != null) {
                            if (valor.equals(dirVieja)) {
                                TLB.tlb.remove(i, valor);
                                TLB.fifo.remove(i);
                            }
                        }
                    }
                }
            }
        }

        return dirVieja;
    }
        
}
