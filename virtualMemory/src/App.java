import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class App {
    
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        
        System.out.println("Ingrese el numero de entradas de la TLB:");
		int numEntTlb = sc.nextInt();
		System.out.println("Ingrese el numero de marcos de pagina en RAM:");
		int numMarcosPag = sc.nextInt();
		System.out.println("Ingrese el nombre del archivo (no incluya el .txt):");
		String archivo = sc.next();
        Integer numMarcosTp = 64;

        sc.close();

        ArrayList<Integer> direcciones = new ArrayList<Integer>();
        File file = new File("./lib/" + archivo + ".txt");
        BufferedReader br = new BufferedReader(new FileReader(file));

        String cada_direccion;
        while ((cada_direccion = br.readLine()) != null) {
            direcciones.add(Integer.parseInt(cada_direccion));
        }
        br.close();
        
        TLB tlb = new TLB(numEntTlb);
        
        RAM ram = new RAM(numMarcosPag);

        TP tp = new TP(numMarcosTp);
        
        Envejecimiento envejecimiento = new Envejecimiento(numMarcosPag, ram);
        envejecimiento.start();
        
        Referencias referencias = new Referencias(tlb, ram, tp, direcciones);
        referencias.start();

        try {
            envejecimiento.join();
            referencias.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
