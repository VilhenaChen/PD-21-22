package pd.tp.cliente.ui;

import pd.tp.cliente.Utilizador;

import java.util.Scanner;

public class UiTexto {
    private Scanner scanner = new Scanner(System.in);
    Utilizador user;

    public void menuInicial() { //Menu chamado quando o User se connecta ao servidor
        System.out.println("---- MENU INICIAL ----");
        System.out.println("1 - Login");
        System.out.println("2 - Registo");
        System.out.println("0 - Sair");
        System.out.println("> ");
    }

    public void uiLogin() { //Ui de Login
        String username;
        String password;
        System.out.println("---- Login ----");
        do {
            System.out.println("Insira o Username: ");
            username = scanner.nextLine();
            if(username.isEmpty()){
                System.out.println("Erro! O username não pode estar vazio");
            }
        }while(username.isEmpty());
        do{
            System.out.println("Insira a Password: ");
            password = scanner.nextLine();
            if(password.isEmpty()){
                System.out.println("Erro! A password não pode estar vazia");
            }
        }while (password.isEmpty());
        user = new Utilizador(username,password);
    }

    public void uiRegisto() { //Ui de Registo de Utilizador

        String nome = null, username = null, password = null;
        boolean igual = false;
        System.out.println("---- Registo de Utilizador ----");
        do{
            System.out.println("Insira o Nome: ");
            nome = scanner.nextLine();
            System.out.println("Insira o Username: ");
            username = scanner.nextLine();
            do {
                System.out.println("Insira a Password: ");
                password = scanner.nextLine();
                System.out.println("Reinsira a Password: ");
                String pass2 = scanner.nextLine();
                if (!pass2.equals(password) || password.isEmpty()) {
                    System.out.println("ERRO!!! As Passwords devem ser iguais");
                } else {
                    igual = true;
                }
            } while (!igual);
            if(username.isEmpty() || nome.isEmpty()) {
                System.out.println("ERRO!!!! O nome e o username nao podem estar vazios.");
            }
        }while(username.isEmpty() || nome.isEmpty());

        user = new Utilizador(username,password, nome);
    }

    public void run() {
        int op;
        boolean exit = false;
        while(!exit) {
            menuInicial();
            op = scanner.nextInt();
            scanner.nextLine();
                switch (op) {
                    case 1:
                        uiLogin();
                        System.out.println(user);
                        break;
                    case 2:
                        uiRegisto();
                        System.out.println(user);
                        //REGISTO
                        break;
                    case 0:
                        exit = true;
                        return;
                    default:
                        System.out.println("Escolha um opcao Valida");
                        break;
                }
        }
    }
}
