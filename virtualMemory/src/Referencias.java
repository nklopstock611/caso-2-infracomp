import java.util.ArrayList;

public class Referencias extends Thread {

    private TLB tlb;
    private RAM ram;
    private TP tp;
    private ArrayList<Integer> direcciones;
    public static ArrayList<Integer> referenciadas = new ArrayList<Integer>();

    // Variables cambiantes de tiempo (ns):
    private Long tempTrad = Long.valueOf(0);
    private Long tempCarga = Long.valueOf(0);
    private Integer numFallosPagina = 0;
    
    // Constantes de tiempo (ns):
    private final Integer tempTradTLB = 2;
    private final Integer tempTradTP = 30;
    private final Integer tempFalloPag = tempTradTP * 2;
    //private final Integer tempTradRAM = 30;
    private final Integer tempArregloFallPag = 10000000;


    public Referencias(TLB pTlb, RAM pRam, TP pTp, ArrayList<Integer> pDirecciones) {
        this.tlb = pTlb;
        this.ram = pRam;
        this.tp = pTp;
        this.direcciones = pDirecciones;
    }

    /**
     * Sigue el if del algoritmo de búsqueda de página cuando se tiene TLB.
     * Básicamente, sigue:
     * if (está en TLB) {
     *    *nice*
     * }
     * else {
     *    if (está en RAM) {
     *       actualizar TLB
     *    }
     *    else {
     *       actualizar RAM
     *       actualizar TP
     *       actualizar TLB
     *    }
     * }
     * 
     * @param direccion
     */
    public void validarReferencias(Integer direccion) {
        
        Boolean estaTLB = this.tlb.getHashTLB().containsValue(direccion);
            
        if (estaTLB) { // ¿Está en TLB?
            this.tempTrad += this.tempTradTLB;
            this.tempCarga += this.tempTradTLB;
        }
        else {
            Boolean estaTP = this.tp.getHashTP().get(direccion);
            
            if (estaTP) { // ¿Está en RAM?
                this.tempTrad += this.tempTradTP;
                this.tempCarga += this.tempTradTP;

                this.tlb.actualizar(direccion); // ¡actualizar es algoritmo FIFO!
            }
            else { // Aseguradito no está en RAM.
                this.numFallosPagina++;

                Integer dirVieja = this.ram.actualizar(direccion);
                Boolean hayEspacioRAM = this.ram.espacio();

                this.tempTrad += this.tempFalloPag;
                this.tempCarga += this.tempFalloPag;

                this.tp.actualizar(direccion, hayEspacioRAM, dirVieja);
                
                this.tlb.actualizar(direccion); // ¡actualizar es algoritmo FIFO!
                
                this.tempCarga += this.tempArregloFallPag;

            }
        }

        for (int i = 0; i < this.ram.getHashBITS().size(); i++) {
            Integer valor = this.ram.getHashBITS().get(i);
            if ((i == direccion) && valor != null) {
                valor = valor + (int) Math.pow(2, 30); // 2^30 es "sumarle" 1 a la izquierda.
                //valor = valor ^ 1;
                //String s = "1" + Integer.toBinaryString(valor);
                //valor = Integer.parseInt(s, 2);
                break;
            }
        }
    }

    public void run() {
        for (int i = 0; i < this.direcciones.size(); i++) {
            validarReferencias(this.direcciones.get(i));
            try {
                sleep(2);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Envejecimiento.env = false;
        System.out.println("Fin de ejecución.");
        System.out.println("Fallos de página: " + this.numFallosPagina);
        System.out.println("Tiempo de traducción: " + this.tempTrad + " ns");
        System.out.println("Tiempo de carga: " + this.tempCarga + " ns");
    }
    
}
