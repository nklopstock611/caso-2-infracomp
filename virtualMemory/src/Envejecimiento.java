
public class Envejecimiento extends Thread {
    
    private RAM ram;
    public static Boolean env = true;

    public Envejecimiento(Integer pN, RAM pRam) {
        this.ram = pRam;
    }

    /**
     * Llama la función "envejecimiento" de la RAM.
     */
    public void envejecimiento() {
        this.ram.envejecimiento();
    }

    public void run() {
        while (env) {
            envejecimiento();
            try {
                sleep(1);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
