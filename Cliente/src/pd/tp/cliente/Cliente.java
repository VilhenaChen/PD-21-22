package pd.tp.cliente;

import pd.tp.cliente.ui.UiTexto;

import java.util.Scanner;

public class Cliente {
    private Scanner scanner;
    private String username;

    public static void main(String[] args) {
        if(args.length <= 0) {
            System.out.println("Falta de IP e Porto do GRDS por parametros");
            System.exit(1);
        }
        String IP_GRDS = args[0];
        String PORTO_GRDS = args[1];
        System.out.println("IP = " + IP_GRDS + " PORTO = " + PORTO_GRDS);
        UiTexto ui = new UiTexto();
        ui.run();
        System.exit(0);
    }
}
