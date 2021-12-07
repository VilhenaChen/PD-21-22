package pd.tp.cliente.ui;

import java.util.Scanner;

public class UiTexto {
    private Scanner scanner = new Scanner(System.in);

    public void menuInicial() {
        System.out.println("MENU INICIAL");
        System.out.println("1 - Login");
        System.out.println("2 - Registo");
        System.out.println("0 - Sair");
        System.out.println("> ");
    }

    public void run() {
        int op;
        menuInicial();
        op = scanner.nextInt();
        switch (op) {
            case 1:
                //LOGIN
                break;
            case 2:
                //REGISTO
                break;
            case 0:
                return;
            default:
                System.out.println("Escolha um opcao Valida");
                break;
        }
    }
}
