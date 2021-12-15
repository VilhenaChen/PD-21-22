package pd.tp.cliente.ui;

import pd.tp.cliente.Utilizador;
import pd.tp.cliente.comunicacao.ComunicacaoServidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class UiTexto {
    private static final String SUCESSO = "SUCESSO";
    private static final String PASSWORD_ERRADA = "PASSWORD_ERRADA";
    private static final String UTILIZADOR_INEXISTENTE = "UTILIZADOR_INEXISTENTE";
    private static final String NOME_REPETIDO = "NOME_REPETIDO";
    private static final String USERNAME_REPETIDO = "USERNAME_REPETIDO";
    private static final String NOME_E_ADMIN_JA_EXISTENTES = "NOME_E_ADMIN_JA_EXISTENTES";
    private static final String ADMIN_INEXISTENTE = "ADMIN_INEXISTENTE";
    private static final String NOT_ADMIN = "NOT_ADMIN";
    private static final String GRUPO_INEXISTENTE = "GRUPO_INEXISTENTE";

    private Scanner scanner = new Scanner(System.in);
    Utilizador user;
    private Socket sCli;

    public UiTexto(Socket sCli) {
        this.sCli = sCli;
        if (sCli.isClosed())
            System.out.println("Fechado aqui!");
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

    private void menuPrincipal() { //Menu Principal da aplicacao
        System.out.println("---- MENU PRINCIPAL ----");
        System.out.println("1 - Mensagens");
        System.out.println("2 - Ficheiros");
        System.out.println("3 - Grupos");
        System.out.println("4 - Contactos");
        System.out.println("5 - Minhas Informações");
        System.out.println("0 - Sair");
        System.out.println("> ");
    }

    private void menuMensagens() { //Menu com as Opcoes das Mensagens
        System.out.println("---- MENU MENSAGENS ----");
        System.out.println("1 - Enviar Mensagem");
        System.out.println("2 - Listar Mensagens");
        System.out.println("3 - Eliminar Mensagens do Historico");
        System.out.println("0 - Voltar atras");
        System.out.println("> ");
    }

    private void menuFicheiros() { //Menu com as Opcoes das Mensagens
        System.out.println("---- MENU FICHEIROS ----");
        System.out.println("1 - Enviar Ficheiro");
        System.out.println("2 - Listar Ficheiros");
        System.out.println("3 - Eliminar Ficheiros do Historico");
        System.out.println("0 - Voltar atras");
        System.out.println("> ");
    }

    private void menuGrupos() { //Menu com as Opcoes dos grupos
        System.out.println("---- MENU GRUPOS ----");
        System.out.println("1 - Aderir a um grupo");
        System.out.println("2 - Sair de um grupo");
        System.out.println("3 - Listar grupos");
        System.out.println("4 - Criar um grupo");
        System.out.println("5 - Grupos Administrados");
        System.out.println("0 - Voltar atras");
        System.out.println("> ");
    }

    private void menuGruposAdministrados() { //Menu com as Opcoes dos Contactos
        System.out.println("---- MENU GRUPOS ADMINISTRADOS ----");
        System.out.println("1 - Excluir Membro");
        System.out.println("2 - Eliminar Grupo");
        System.out.println("3 - Listar Membros");
        System.out.println("4 - Alterar nome do grupo");
        System.out.println("5 - Aceitar membros");
        System.out.println("6 - Listar grupos administrados");
        System.out.println("0 - Voltar atras");
        System.out.println("> ");
    }

    private void menuContactos() { //Menu com as Opcoes dos Contactos
        System.out.println("---- MENU CONTACTOS ----");
        System.out.println("1 - Listar Contactos");
        System.out.println("2 - Eliminar Contacto");
        System.out.println("3 - Pesquisar Utilizadores");
        System.out.println("4 - Listar Utilizadores");
        System.out.println("5 - Adicionar Contacto");
        System.out.println("6 - Aceitar Contactos");
        System.out.println("0 - Voltar atras");
        System.out.println("> ");
    }

    private void menuInformacoes() { //Menu com as infos do user
        System.out.println("---- MENU INFORMACOES ----");
        System.out.println("1 - Ver as minhas Informacoes");
        System.out.println("2 - Modificar Nome");
        System.out.println("3 - Modificar Username");
        System.out.println("4 - Modificar Password");
        System.out.println("0 - Voltar atras");
        System.out.println("> ");
    }

    private boolean LoginComSucesso(String mensagem) {
        if (sCli.isClosed())
            System.out.println("Fechado aqui dentro LoginSucesso!");
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

    public void run() throws IOException {
        int op;
        boolean exit = false;
        ObjectOutputStream out = new ObjectOutputStream(sCli.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(sCli.getInputStream());
        ComunicacaoServidor cs = new ComunicacaoServidor(sCli, in, out);

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
                        break;
                    default:
                        System.out.println("Escolha um opcao Valida");
                        break;
                }
        }
        cs.logout();
        out.close();
        in.close();
        sCli.close();

    }
}
