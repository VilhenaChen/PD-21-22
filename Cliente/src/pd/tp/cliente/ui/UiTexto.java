package pd.tp.cliente.ui;

import pd.tp.cliente.Utilizador;
import pd.tp.cliente.comunicacao.ComunicacaoServidor;

import java.net.Socket;
import java.util.Scanner;

public class UiTexto {
    private static final String SUCESSO = "SUCESSO";
    private static final String PASSWORD_ERRADA = "PASSWORD_ERRADA";
    private static final String UTILIZADOR_INEXISTENTE = "UTILIZADOR_INEXISTENTE";
    private static final String NOME_REPETIDO = "NOME_REPETIDO";
    private static final String USERNAME_REPETIDO = "USERNAME_REPETIDO";
    private Scanner scanner = new Scanner(System.in);
    Utilizador user;
    private Socket sCli;

    public UiTexto(Socket sCli) {
        this.sCli = sCli;
    }

    public void menuInicial() { //Menu chamado quando o User se connecta ao servidor
        System.out.println("---- MENU INICIAL ----");
        System.out.println("1 - Registo");
        System.out.println("2 - Login");
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

    private boolean LoginComSucesso(String mensagem) {
        if(mensagem.equals(SUCESSO)){
            System.out.println("Login efetuado com sucesso!");
            user.setLogged(true);
            return true;
        }
        else {
            if (mensagem.equals(PASSWORD_ERRADA)) {
                System.out.println("ERRO! Password Errada! Login não efetuado!");
            }
            if (mensagem.equals(UTILIZADOR_INEXISTENTE)) {
                System.out.println("ERRO! Utilizador Inexistente! Login não efetuado!");
            }
            return false;
        }
    }

    private boolean RegistoComSucesso(String mensagem) {
        if(mensagem.equals(SUCESSO)){
            System.out.println("Registo efetuado com sucesso!");
            user.setLogged(true);
            return true;
        }
        else {
            if (mensagem.equals(NOME_REPETIDO)) {
                System.out.println("ERRO! O nome inserido já existe! Registo não efetuado!");
            }
            if (mensagem.equals(USERNAME_REPETIDO)) {
                System.out.println("ERRO! O username inserido já existe! Registo não efetuado!");
            }
            return false;
        }
    }

    public void run() {
        int op;
        boolean exit = false;
        ComunicacaoServidor cs = new ComunicacaoServidor(sCli);
        while(!exit) {
            menuInicial();
            op = scanner.nextInt();
            scanner.nextLine();
                switch (op) {
                    case 1:
                        uiRegisto();
                        RegistoComSucesso(cs.efetuaRegisto(user));
                        System.out.println(user);
                        //REGISTO
                        break;
                    case 2:
                        uiLogin();
                        LoginComSucesso(cs.efetuaLogin(user));
                        System.out.println(user);
                        //LOGIN
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
