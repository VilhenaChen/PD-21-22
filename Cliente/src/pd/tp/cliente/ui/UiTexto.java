package pd.tp.cliente.ui;

import pd.tp.cliente.threads.ThreadHeartbeat;
import pd.tp.cliente.threads.ThreadRecebeInformacoesServidor;
import pd.tp.comum.Ficheiro;
import pd.tp.comum.Mensagem;
import pd.tp.cliente.Utilizador;
import pd.tp.cliente.comunicacao.ComunicacaoServidor;
import pd.tp.comum.Utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Timer;

public class UiTexto implements Utils {

    private Scanner scanner = new Scanner(System.in);
    private Utilizador user;
    private Socket sCli;
    private ComunicacaoServidor cs;
    private String ipServidor;



    public UiTexto(Socket sCli, String ipServidor) {
        this.sCli = sCli;
        this.ipServidor = ipServidor;
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
            if (username.isEmpty()) {
                System.out.println("Erro! O username não pode estar vazio");
            }
        } while (username.isEmpty());
        do {
            System.out.println("Insira a Password: ");
            password = scanner.nextLine();
            if (password.isEmpty()) {
                System.out.println("Erro! A password não pode estar vazia");
            }
        } while (password.isEmpty());
        user = new Utilizador(username, password);
    }

    public void uiRegisto() { //Ui de Registo de Utilizador

        String nome = null, username = null, password = null;
        boolean igual = false;
        System.out.println("---- Registo de Utilizador ----");
        do {
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
            if (username.isEmpty() || nome.isEmpty()) {
                System.out.println("ERRO!!!! O nome e o username nao podem estar vazios.");
            }
        } while (username.isEmpty() || nome.isEmpty());

        user = new Utilizador(username, password, nome);
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

    private void menuGruposAdministrados(String nomeGrupo) { //Menu com as Opcoes dos Contactos
        System.out.println("---- ADMINISTRACAO DO GRUPO: " + nomeGrupo + " ----");
        System.out.println("1 - Excluir Membro");
        System.out.println("2 - Eliminar Grupo");
        System.out.println("3 - Listar Membros");
        System.out.println("4 - Alterar nome do grupo");
        System.out.println("5 - Aceitar ou Rejeitar membros");
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
        System.out.println("6 - Aceitar ou Rejeitar Contactos");
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
        if (mensagem.startsWith(SUCESSO)) {
            System.out.println("Login efetuado com sucesso!");
            String[] array = mensagem.split(",");
            user.setNome(array[1]);
            user.setLogged(true);
            return true;
        } else {
            if (mensagem.equals(PASSWORD_ERRADA)) {
                System.out.println("ERRO! Password Errada! Login não efetuado!");
            }
            if (mensagem.equals(UTILIZADOR_INEXISTENTE)) {
                System.out.println("ERRO! Utilizador Inexistente! Login não efetuado!");
            }
            if (mensagem.equals(UTILIZADOR_JA_LOGADO)){
                System.out.println("ERRO! Já foi efetuado um login com esta conta!");
            }
            return false;
        }
    }

    private boolean RegistoComSucesso(String mensagem) {
        if (mensagem.equals(SUCESSO)) {
            System.out.println("Registo efetuado com sucesso!");
            user.setLogged(false);
            return true;
        } else {
            if (mensagem.equals(NOME_REPETIDO)) {
                System.out.println("ERRO! O nome inserido já existe! Registo não efetuado!");
            }
            if (mensagem.equals(USERNAME_REPETIDO)) {
                System.out.println("ERRO! O username inserido já existe! Registo não efetuado!");
            }
            return false;
        }
    }

    private void trataMenuMensagens() {
        int op = 0;
        String resultado;
        while (true) {
            menuMensagens();
            op = scanner.nextInt();
            scanner.nextLine();
            switch (op) {
                case 1: //Enviar Msg
                    Date date = new Date();
                    Timestamp timestamp = new Timestamp(date.getTime());
                    String strDate = timestamp.toString();
                    String assunto = "";
                    do{
                        System.out.println("Assunto (Max 50 carateres): ");
                        assunto = scanner.nextLine();
                        if(assunto.length()>50)
                            System.out.println("Erro! Excedeu o máximo de carateres do Assunto!");
                    }while(assunto.length()>50);
                    String corpo = "";
                    do{
                        System.out.println("Corpo  (Max 250 carateres): ");
                        corpo = scanner.nextLine();
                        if(corpo.length()>250)
                            System.out.println("Erro! Excedeu o máximo de carateres do Corpo!");
                    }while(corpo.length()>250);
                    System.out.println("Destinatario (Username ou ID do grupo): ");
                    String destino = scanner.nextLine();
                    Mensagem msg = new Mensagem(assunto, corpo, user.getUsername(), destino, strDate);
                    resultado = cs.enviaMensagem(msg);
                    if(resultado.equals(GRUPO_INEXISTENTE)) {
                        System.out.println("O Grupo " + msg.getReceiver() + " nao existe" );
                    }
                    else if(resultado.equals(NOT_MEMBRO)) {
                        System.out.println("Nao faz parte do grupo " + msg.getReceiver());
                    }
                    else if(resultado.equals(UTILIZADOR_INEXISTENTE)) {
                        System.out.println("O Utilizador " + msg.getReceiver() + " nao existe" );
                    }
                    else {
                        System.out.println("Mensagem enviada com sucesso" );
                    }
                    System.out.println(msg);
                    break;
                case 2: //Listar msg
                    resultado = cs.listaMensagens();
                    if(resultado.length() == 0)
                        System.out.println("Nao existem mensagens!");
                    else {
                        System.out.println("---- LISTA DE MENSAGENS----");
                        System.out.println(resultado);
                        System.out.println("Escolha uma mensagem: ");
                        int escolha = scanner.nextInt();
                        scanner.nextLine();
                        resultado = cs.getCorpoMensagem(escolha);
                        if(resultado.length() == 0)
                            System.out.println("A Mensagem " + escolha + " nao existe ou nao e sua");
                        else {
                            System.out.println(resultado);
                        }
                    }
                    break;
                case 3: //Eliminar msg historico
                    resultado = cs.listaMensagensParaEliminar();
                    if(resultado.length()==0){
                        System.out.println("ERRO! Não possui mensagens");
                        break;
                    }
                    System.out.println(resultado);
                    System.out.println("Insira os ids das mensagens que pretende eliminar (separados por virgulas): ");
                    String mensagens = scanner.nextLine();

                    resultado = cs.eliminaMensagens(mensagens);
                    if(resultado.startsWith("ERRO")){
                        System.out.println("Erro! As mensagens seguintes não foram eliminadas por não estarem na sua lista de mensagens: ");
                        String[] arrayFalhas = resultado.split(",");
                        for(int j = 0; j< arrayFalhas.length; j++){
                            System.out.println(arrayFalhas[j]);
                        }
                    }
                    else{
                        System.out.println("As mensagens (" + mensagens + ") foram eliminadas com sucesso");
                    }
                    break;
                case 0: //Back
                    return;
                default:
                    System.out.println("Opcao Invalida!! Insira uma opcao valida");
                    break;
            }
        }
    }

    private void trataMenuFicheiros() {
        int op = 0;
        String resultado;
        while (true) {
            menuFicheiros();
            op = scanner.nextInt();
            scanner.nextLine();
            switch (op) {
                case 1: //Enviar ficheiro
                    Date date = new Date();
                    Timestamp timestamp = new Timestamp(date.getTime());
                    String strDate = timestamp.toString();
                    System.out.println("Insira o diretorio da pasta onde se encontra o ficheiro:");
                    String dirFicheiro = scanner.nextLine();
                    System.out.println("Insira o nome do ficheiro:");
                    String nomeFicheiro = scanner.nextLine();
                    System.out.println("Insira o destinatário: (Username ou ID do grupo): ");
                    String destinatario = scanner.nextLine();
                    Ficheiro ficheiro = new Ficheiro(nomeFicheiro,user.getUsername(),destinatario,strDate);
                    resultado = cs.enviaFicheiro(ficheiro, dirFicheiro);
                    if(resultado.equals(GRUPO_INEXISTENTE)) {
                        System.out.println("O Grupo " + ficheiro.getReceiver() + " nao existe" );
                    }
                    else if(resultado.equals(NOT_MEMBRO)) {
                        System.out.println("Nao faz parte do grupo " + ficheiro.getReceiver());
                    }
                    else if(resultado.equals(UTILIZADOR_INEXISTENTE)) {
                        System.out.println("O Utilizador " + ficheiro.getReceiver() + " nao existe" );
                    }
                    else {
                        System.out.println("Ficheiro enviado com sucesso" );
                    }
                    break;
                case 2: //Listar Ficheiros
                    break;
                case 3: //Elininar ficheiro historico
                    break;
                case 0: //Back
                    return;
                default:
                    System.out.println("Opcao Invalida!! Insira uma opcao valida");
                    break;
            }
        }
    }


    private void trataMenuGrupos() {
        int op = 0;
        String resultado = "";
        int idGrupo;
        while (true) {
            menuGrupos();
            op = scanner.nextInt();
            scanner.nextLine();
            switch (op) {
                case 1: //Aderir grupo
                    System.out.println("Insira o ID do grupo ao qual pretende aderir: ");
                    idGrupo = scanner.nextInt();
                    scanner.nextLine();
                    resultado = cs.adereAGrupo(idGrupo);
                    if(resultado.equals(GRUPO_INEXISTENTE)){
                        System.out.println("Erro! Não existe nenhum grupo com o id inserido");
                    }
                    if(resultado.equals(JA_PERTENCE)){
                        System.out.println("Erro! Já pertence ao grupo ao qual está a tentar aderir");
                    }
                    else
                        System.out.println("Aderiu com sucesso ao Grupo " + idGrupo);
                    break;
                case 2: //Sair grupo
                    System.out.println("Insira o ID do grupo do qual pretende sair: ");
                    idGrupo = scanner.nextInt();
                    scanner.nextLine();
                    resultado = cs.saiDeGrupo(idGrupo);
                    if(resultado.equals(GRUPO_INEXISTENTE)){
                        System.out.println("ERRO!! Nao existe nenhum grupo com o ID inserido");
                    }
                    if(resultado.equals(NOT_MEMBRO)){
                        System.out.println("ERRO!! Nao pertence a este grupo");
                    }
                    else
                        System.out.println("Saiu com sucesso do Grupo " + idGrupo);
                    break;
                case 3: //Listar grupos
                    resultado = cs.listaGrupos();
                    if(resultado.isEmpty())
                        System.out.println("Nao existem grupos criados!");
                    else {
                        System.out.println("---- GRUPOS EXISTENTES ----");
                        System.out.println(resultado);
                        System.out.println("Escolha um grupo: ");
                        int escolha = scanner.nextInt();
                        scanner.nextLine();
                        resultado = cs.getListaMembrosGrupo(escolha);
                        if(resultado == EMPTY)
                            System.out.println("A Grupo " + escolha + " nao tem membros");
                        else {
                            System.out.println(resultado);
                        }

                    }
                    break;
                case 4: //Criar um grupo
                    System.out.println("Insira nome do Grupo: ");
                    String nome = scanner.nextLine();
                    resultado = cs.criaGrupo(nome);
                    if(resultado.equals(SUCESSO)) {
                        System.out.println("Grupo criado com sucesso");
                    }
                    else{
                        System.out.println("Erro! O já criou um grupo com este nome!");
                    }

                    break;
                case 5:
                    trataMenuGruposAdmin();
                    break;
                case 0: //back
                    return;
                default:
                    System.out.println("Opcao Invalida!! Insira uma opcao valida");
                    break;
            }
        }
    }
    private void trataMenuGruposAdmin() {
        int op = 0;
        int idGrupo = -1;
        String resultado = cs.listaGruposAdmin();
        if(resultado.isEmpty()) {
            System.out.println("O Utilizador nao tem grupos administrados");
            return;
        }
        String[] array = resultado.split(",");
        int numero = Integer.parseInt(array[0]);
        int[] ids = new int[numero];
        String[] nomes = new String[numero];
        for(int i = 0, j = 0; j < numero; i++, j++) {
            ids[j] = Integer.parseInt(array[i + 1]);
            nomes[j] = array[i + 2];
            i++;
        }
        System.out.println("---- GRUPOS DO ADMIN ----");
        for(int i = 0; i < numero; i++) {
            System.out.println(ids[i] + " - " + nomes[i]);
        }
        System.out.println("-1 - Voltar Atras");
        while(op != -1) {
            System.out.println("> ");
            op = scanner.nextInt();
            scanner.nextLine();
            for(int i = 0; i < numero; i++) {
                if(ids[i] == op) {
                    idGrupo = op;
                    String nomeGrupo = nomes[i];
                    while (true) {
                        menuGruposAdministrados(nomeGrupo);
                        op = scanner.nextInt();
                        scanner.nextLine();
                        switch (op) {
                            case 1: //Excluir membros
                                resultado = cs.listaMembrosGrupos(idGrupo);
                                if(resultado.length()==0){
                                    System.out.println("ERRO! Não existem membros do grupo para além de si mesmo");
                                    break;
                                }
                                System.out.println(resultado);
                                System.out.println("Insira o username dos membros que pretende excluir (separados por virgulas): ");
                                String usernames_excluir = scanner.nextLine();

                                resultado = cs.excluiMembros(usernames_excluir,idGrupo);
                                if(resultado.startsWith("ERRO")){
                                    System.out.println("Erro! Os usernames seguintes não foram excluidos por não estarem na lista de membros atuais do grupo: ");
                                    String[] arrayFalhas = resultado.split(",");
                                    for(int j = 0; j< arrayFalhas.length; j++){
                                        System.out.println(arrayFalhas[j]);
                                    }
                                }
                                else{
                                    System.out.println("Os membros (" + usernames_excluir + ") Foram excluidos com sucesso");
                                }

                                break;
                            case 2: //Eliminar grupo
                                resultado = cs.eliminaGrupo(idGrupo);
                                if(resultado.equals(SUCESSO)){
                                    System.out.println("O grupo " + idGrupo + " foi eliminado com sucesso!");
                                    return;
                                }
                                break;
                            case 3: //Listar membros

                                resultado = cs.listaMembrosGrupos(idGrupo);
                                if(resultado.equals(EMPTY)){
                                    System.out.println("Erro! E o unico membro deste grupo!");
                                    break;
                                }
                                System.out.println(resultado);
                                break;
                            case 4: //Alterar nome grupo
                                System.out.println("Insira o novo nome para o grupo: ");
                                String novo_nome = scanner.nextLine();
                                resultado = cs.trocaNomeGroup(novo_nome, idGrupo);
                                if(resultado.equals(SUCESSO)) {
                                    System.out.println("Nome do grupo " + idGrupo + " trocado para '" + novo_nome + "'");
                                    nomeGrupo = novo_nome;
                                }
                                else{
                                    if(resultado.equals(NOME_E_ADMIN_JA_EXISTENTES)){
                                        System.out.println("Erro! Já possui um grupo com o nome: " + novo_nome);
                                    }
                                }
                                break;
                            case 5: //Aceitar ou Rejeitar membros

                                resultado = cs.listaMembrosGrupoPorAceitar(idGrupo);
                                if(resultado.equals(EMPTY)){
                                    System.out.println("Não existem membros por aceitar");
                                    break;
                                }
                                System.out.println(resultado);
                                int opcao;
                                do {
                                    System.out.println("--AÇÕES POSSIVEIS--");
                                    System.out.println("0 - Aceitar");
                                    System.out.println("1 - Rejeitar");
                                    System.out.println("Insira a ação que pretende realizar:");
                                    opcao = scanner.nextInt();
                                    scanner.nextLine();
                                    if (opcao != 0 && opcao != 1){
                                        System.out.println("Erro! Opção Inserida Inválida!");
                                    }
                                }while (opcao != 0 && opcao != 1);
                                if(opcao == 0) {
                                    System.out.println("Insira o username dos membros que pretende aceitar (separados por virgulas): ");
                                    String usernames = scanner.nextLine();
                                    resultado = cs.aceitaMembros(usernames, idGrupo);
                                    if (resultado.startsWith("ERRO")) {
                                        System.out.println("Erro! Os usernames seguintes não foram aceites por não estarem na lista de membros a aceitar: ");
                                        String[] arrayFalhas = resultado.split(",");
                                        for (int j = 0; j < arrayFalhas.length; j++) {
                                            System.out.println(arrayFalhas[j]);
                                        }
                                    } else {
                                        System.out.println("Os membros (" + usernames + ") Foram aceites com sucesso");
                                    }
                                }
                                else {
                                    System.out.println("Insira o username dos membros que pretende rejeitar (separados por virgulas): ");
                                    String usernames = scanner.nextLine();
                                    resultado = cs.rejeitaMembros(usernames, idGrupo);
                                    if (resultado.startsWith("ERRO")) {
                                        System.out.println("Erro! Os usernames seguintes não foram rejeitados por não estarem na lista de membros a aceitar: ");
                                        String[] arrayFalhas = resultado.split(",");
                                        for (int j = 0; j < arrayFalhas.length; j++) {
                                            System.out.println(arrayFalhas[j]);
                                        }
                                    } else {
                                        System.out.println("Os membros (" + usernames + ") Foram rejeitados com sucesso");
                                    }
                                }
                                break;
                            case 0: //back
                                return;
                            default:
                                System.out.println("Opcao Invalida!! Insira uma opcao valida");
                                break;
                        }
                    }
                }
            }
            if(op != -1)
                System.out.println("Erro! Por favor insira uma opcao valida");

        }
    }

    private void trataMenuContactos() {
        int op = 0;
        String resultado;
        while (true) {
            menuContactos();
            op = scanner.nextInt();
            scanner.nextLine();
            switch (op) {
                case 1: //Listar Contactos
                    resultado = cs.listaContactos();
                    if(resultado.length() == 0)
                        System.out.println("Nao tem contactos");
                    else {
                        System.out.println("---- LISTA DE CONTACTOS ----");
                        System.out.println(resultado);
                    }
                    break;
                case 2: //Eliminar contacto
                    resultado = cs.listaContactos();
                    if(resultado.length()==0) {
                        System.out.println("Não possui contactos");
                        break;
                    }
                    System.out.println("---- LISTA DE CONTACTOS ----");
                    System.out.println(resultado);
                    System.out.println("Insira o username dos contactos que pretende eliminar (separados por virgulas): ");
                    String contactos = scanner.nextLine();
                    resultado = cs.eliminaContactos(contactos);
                    if(resultado.startsWith("ERRO")){
                        System.out.println("Erro! Os usernames seguintes não foram eliminados por não estarem na lista de contactos: ");
                        String[] arrayFalhas = resultado.split(",");
                        for (int i = 1; i < arrayFalhas.length; i++) {
                            System.out.println(arrayFalhas[i]);
                        }
                    }
                    else{
                        System.out.println("Os contactos (" + contactos + ") foram eliminados com sucesso");
                    }

                    break;
                case 3: //Pesquisar Utilizadores
                    System.out.println("Insira o username do user que pretende pesquisar: ");
                    String pesquisa = scanner.nextLine();
                    resultado = cs.pesquisaUsers(pesquisa);
                    if(resultado.length() == 0)
                        System.out.println("O utilizador '" + pesquisa + "' nao existe na Base de Dados");
                    else {
                        System.out.println("---- LISTA DE USERS ----");
                        System.out.println(resultado);
                    }
                    break;
                case 4: //Listar Utilizadores
                    resultado = cs.listaUsers();
                    if(resultado.length() == 0)
                        System.out.println("Nao existem utilizadores na Base de dados");
                    else {
                        System.out.println("---- LISTA DE USERS ----");
                        System.out.println(resultado);
                    }
                    break;
                case 5: //Adicionar Contacto
                    System.out.println("Insira o username do user que pretende adicionar aos contacto: ");
                    String friend = scanner.nextLine();
                    resultado = cs.adicionaContacto(friend);
                    if(resultado.equals(SUCESSO))
                        System.out.println("Pedido de contacto enviado com sucesso para o user '" + friend +"'");
                    else if(resultado.equals(UTILIZADOR_INEXISTENTE))
                        System.out.println("O Utilizador '" + friend + "' nao existe na base de dados");
                    else if(resultado.equals(USERNAME_REPETIDO))
                        System.out.println("O Utilizador '" + friend + "' ja faz parte da sua lista de contactos ou já tem um pedido de amizade seu");
                    break;
                case 6: //Aceitar ou rejeitar contactos

                    resultado = cs.listaContactosPorAceitar();
                    if(resultado.length()==0) {
                        System.out.println("Não existem contactos por aceitar");
                        break;
                    }
                    System.out.println("---- LISTA DE CONTACTOS POR ACEITAR ----");
                    System.out.println(resultado);
                    int opcao;
                    do {
                        System.out.println("--AÇÕES POSSIVEIS--");
                        System.out.println("0 - Aceitar");
                        System.out.println("1 - Rejeitar");
                        System.out.println("Insira a ação que pretende realizar:");
                        opcao = scanner.nextInt();
                        scanner.nextLine();
                        if (opcao != 0 && opcao != 1){
                            System.out.println("Erro! Opção Inserida Inválida!");
                        }
                    }while (opcao != 0 && opcao != 1);
                    if (opcao == 0){

                        System.out.println("Insira o username dos contactos que pretende aceitar (separados por virgulas): ");
                        String usernames = scanner.nextLine();
                        resultado = cs.aceitaContactos(usernames);
                        if(resultado.startsWith("ERRO")){
                            System.out.println("Erro! Os usernames seguintes não foram aceites por não estarem na lista de contactos a aceitar: ");
                            String[] arrayFalhas = resultado.split(",");
                            for (int i = 1; i < arrayFalhas.length; i++) {
                                System.out.println(arrayFalhas[i]);
                            }
                        }
                        else{
                            System.out.println("Os contactos (" + usernames + ") foram aceites com sucesso");
                        }
                    }
                    else {
                        System.out.println("Insira o username dos contactos que pretende rejeitar (separados por virgulas): ");
                        String usernames = scanner.nextLine();
                        resultado = cs.rejeitaContactos(usernames);
                        if(resultado.startsWith("ERRO")){
                            System.out.println("Erro! Os usernames seguintes não foram rejeitados por não estarem na lista de contactos a rejeitar: ");
                            String[] arrayFalhas = resultado.split(",");
                            for (int i = 1; i < arrayFalhas.length; i++) {
                                System.out.println(arrayFalhas[i]);
                            }
                        }
                        else{
                            System.out.println("Os contactos (" + usernames + ") foram rejeitados com sucesso");
                        }
                    }
                    break;
                case 0: //Back
                    return;
                default:
                    System.out.println("Opcao Invalida!! Insira uma opcao valida");
                    break;
            }
        }
    }

    private void trataMenuInformacoes() {
        int op = 0;
        String resultado;
        while (true) {
            menuInformacoes();
            op = scanner.nextInt();
            scanner.nextLine();
            switch (op) {
                case 1: //Ver as minhas infos
                    System.out.println("Os meus dados: ");
                    System.out.println("\tNome: " + user.getNome());
                    System.out.println("\tUsername: " + user.getUsername());
                    System.out.println("\tPassword: " + user.getPassword());
                    break;
                case 2: //Modificar nome
                    System.out.println("Insira o novo nome: ");
                    String nome = scanner.nextLine();
                    resultado = cs.trocaNome(nome);
                    if(resultado.equals(SUCESSO)){
                        user.setNome(nome);
                        System.out.println("Nome modificado com sucesso");
                    }
                    else {
                        System.out.println("O nome escolhido ja se encontra em uso");
                    }
                    break;
                case 3: //Modificar username
                    System.out.println("Insira o novo username: ");
                    String username = scanner.nextLine();
                    resultado = cs.trocaUsername(username);
                    if(resultado.equals(SUCESSO)){
                        user.setUsername(username);
                        System.out.println("Username modificado com sucesso");
                    }
                    else {
                        System.out.println("O username escolhido ja se encontra em uso");
                    }
                    break;
                case 4: //Modificar Password
                    System.out.println("Insira a nova password: ");
                    String password = scanner.nextLine();
                    resultado = cs.trocaPassword(password);
                    if(resultado.equals(SUCESSO)){
                        user.setPassword(password);
                        System.out.println("Password modificada com sucesso");
                    }
                    break;
                case 0: //back
                    return;
                default:
                    System.out.println("Opcao Invalida!! Insira uma opcao valida");
                    break;
            }
        }
    }
    
    private boolean menuSecundario() {
        int op = 0;
        boolean exit = false;
        while(!exit){
            menuPrincipal();
            op = scanner.nextInt();
            scanner.nextLine();
            switch (op) {
                case 1:
                    trataMenuMensagens();
                    break;
                case 2:
                    trataMenuFicheiros();
                    break;
                case 3:
                    trataMenuGrupos();
                    break;
                case 4:
                    trataMenuContactos();
                    break;
                case 5:
                    trataMenuInformacoes();
                    break;
                case 0:
                    exit = true;
                    break;
                default:
                    System.out.println("Opcao Invalida!! Insira uma opcao valida");
                    break;
            }
        }
        return true;
    }

    public void start() throws IOException {
        int op;
        boolean exit = false;
        ObjectOutputStream out = new ObjectOutputStream(sCli.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(sCli.getInputStream());
        cs = new ComunicacaoServidor(sCli, in, out, ipServidor);
        while(!exit) {
            menuInicial();
            op = scanner.nextInt();
            scanner.nextLine();
                switch (op) {
                    case 1: //Registo do User
                        uiRegisto();
                        RegistoComSucesso(cs.efetuaRegisto(user));
                        System.out.println(user);
                        break;
                    case 2: //Login do User
                        uiLogin();
                        if(LoginComSucesso(cs.efetuaLogin(user))) {
                            System.out.println("Bem-vindo " + user.getUsername());
                            ThreadRecebeInformacoesServidor threadRecebeInformacoesServidor = new ThreadRecebeInformacoesServidor(in,user);
                            threadRecebeInformacoesServidor.start();
                            ThreadHeartbeat threadHeartbeat = new ThreadHeartbeat(cs);
                            Timer timer = new Timer("Heartbeat");
                            timer.schedule(threadHeartbeat,0,1000);
                            cs.setUser(user);
                            exit = menuSecundario();
                            threadHeartbeat.cancel();
                            timer.cancel();
                            cs.logout();
                        }
                        break;
                    case 0:
                        exit = true;
                        break;
                    default:
                        System.out.println("Escolha um opcao Valida");
                        break;
                }
        }
        out.close();
        in.close();
        sCli.close();
        System.out.println("Cliente desligado com sucesso!!");
    }
}
