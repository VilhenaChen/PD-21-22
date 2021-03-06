package pd.tp.servidor.threads;

import pd.tp.cliente.Clientes;
import pd.tp.cliente.Ficheiros;
import pd.tp.comum.Ficheiro;
import pd.tp.comum.NovidadeGRDS;
import pd.tp.comum.Utils;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

public class ThreadRecebeAtualizacoesGRDS extends Thread implements Utils {

    private DatagramSocket ds;
    private DatagramPacket dp;
    private NovidadeGRDS novidadeGRDS;
    private Clientes clientes;
    private int idServ;
    private Ficheiros ficheiros;


    public ThreadRecebeAtualizacoesGRDS(DatagramSocket ds, Clientes clientes, Ficheiros ficheiros, int idServ){
        this.ds=ds;
        this.clientes = clientes;
        this.ficheiros = ficheiros;
        this.idServ = idServ;
    }

    @Override
    public void run() {
        while(true){
            try {
                dp = new DatagramPacket(new byte[1024], 1024);
                ds.receive(dp);
                ByteArrayInputStream bais = new ByteArrayInputStream(dp.getData());
                ObjectInputStream in = new ObjectInputStream(bais);
                Object objeto = in.readObject();
                if(objeto instanceof String) {
                    System.out.println("O GRDS vai encerrar!");
                }
                novidadeGRDS = (NovidadeGRDS) objeto;
                StringBuilder stringBuilder = new StringBuilder();
                if(novidadeGRDS.getTipoMsg().equals(LOGIN)){
                    System.out.println("INFORMAÇÃO GRDS: O utilizador " + novidadeGRDS.getUsernameUser() + " efetuou login!");
                }
                else {
                    if(novidadeGRDS.getTipoMsg().equals(REGISTO)){
                        System.out.println("INFORMAÇÃO GRDS: O utilizador " + novidadeGRDS.getUsernameUser() + " efetuou registo!");
                    }
                    else {
                        if(novidadeGRDS.getTipoMsg().equals(UPDATE_NAME)){
                            System.out.println("INFORMAÇÃO GRDS: O utilizador " + novidadeGRDS.getUsernameUser() + " alterou o seu nome!");
                        }
                        else {
                            if(novidadeGRDS.getTipoMsg().equals(UPDATE_USERNAME)){
                                System.out.println("INFORMAÇÃO GRDS: O utilizador " + novidadeGRDS.getUsernameUser() + " alterou o seu username para " + novidadeGRDS.getNovoUsernameUser() + "!");
                            }
                            else {
                                if(novidadeGRDS.getTipoMsg().equals(UPDATE_PASSWORD)){
                                    System.out.println("INFORMAÇÃO GRDS: O utilizador " + novidadeGRDS.getUsernameUser() + "  a sua password!");
                                }
                                else {
                                    if(novidadeGRDS.getTipoMsg().equals(LOGOUT)){
                                        System.out.println("INFORMAÇÃO GRDS: O utilizador " + novidadeGRDS.getUsernameUser() + " efetuou logout!");
                                    }
                                    else {
                                        if(novidadeGRDS.getTipoMsg().equals(NOVO_GRUPO)){
                                            System.out.println("INFORMAÇÃO GRDS: O utilizador " + novidadeGRDS.getUsernameUser() + "!");
                                            //stringBuilder.append(NOVO_GRUPO).append(",").append(novidadeGRDS.getUsernameUser()).append(",").append());
                                        }
                                        else {
                                            if(novidadeGRDS.getTipoMsg().equals(UPDATE_NOME_GRUPO)){
                                                stringBuilder.append("INFORMAÇÃO GRDS: O administrador do grupo ").append(novidadeGRDS.getIdGrupo()).append(" alterou o nome do mesmo para ").append(novidadeGRDS.getNovoNomeGrupo()).append("!");
                                                System.out.println(stringBuilder.toString());
                                            }
                                            else {
                                                if(novidadeGRDS.getTipoMsg().equals(ADERE_A_GRUPO)){
                                                    stringBuilder.append("INFORMAÇÃO GRDS: O user ").append(novidadeGRDS.getUsernameUser()).append(" pediu para aderir ao grupo ").append(novidadeGRDS.getIdGrupo()).append("!");
                                                    System.out.println(stringBuilder.toString());
                                                }
                                                else {
                                                    if(novidadeGRDS.getTipoMsg().equals(SAI_DE_GRUPO)){
                                                        stringBuilder.append("INFORMAÇÃO GRDS: O user ").append(novidadeGRDS.getUsernameUser()).append(" saiu do grupo ").append(novidadeGRDS.getIdGrupo()).append("!");
                                                        System.out.println(stringBuilder.toString());
                                                    }
                                                    else {
                                                        if(novidadeGRDS.getTipoMsg().equals(ACEITA_MEMBRO)){
                                                            stringBuilder.append("INFORMAÇÃO GRDS: O administrador do grupo ").append(novidadeGRDS.getIdGrupo()).append(" aceitou o user ").append(novidadeGRDS.getUsernameUser()).append("!");
                                                            System.out.println(stringBuilder.toString());
                                                        }
                                                        else {
                                                            if(novidadeGRDS.getTipoMsg().equals(REJEITA_MEMBRO)){
                                                                stringBuilder.append("INFORMAÇÃO GRDS: O administrador do grupo ").append(novidadeGRDS.getIdGrupo()).append(" rejeitou o user ").append(novidadeGRDS.getUsernameUser()).append("!");
                                                                System.out.println(stringBuilder.toString());
                                                            }
                                                            else {
                                                                if(novidadeGRDS.getTipoMsg().equals(KICK_MEMBRO_GRUPO)){
                                                                    stringBuilder.append("INFORMAÇÃO GRDS: O administrador do grupo ").append(novidadeGRDS.getIdGrupo()).append(" expulsou o user ").append(novidadeGRDS.getUsernameUser()).append("!");
                                                                    System.out.println(stringBuilder.toString());
                                                                }
                                                                else {
                                                                    if(novidadeGRDS.getTipoMsg().equals(ELIMINA_GRUPO)){
                                                                        stringBuilder.append("INFORMAÇÃO GRDS: O grupo ").append(novidadeGRDS.getIdGrupo()).append(" foi eliminado pelo administrador!");
                                                                        System.out.println(stringBuilder.toString());
                                                                    }
                                                                    else {
                                                                        if(novidadeGRDS.getTipoMsg().equals(NOVO_CONTACTO)){
                                                                            stringBuilder.append("INFORMAÇÃO GRDS: O user ").append(novidadeGRDS.getUsernameUser()).append(" enviou um pedido de contacto ao user ").append(novidadeGRDS.getFriend()).append("!");
                                                                            System.out.println(stringBuilder.toString());
                                                                        }
                                                                        else {
                                                                            if(novidadeGRDS.getTipoMsg().equals(ACEITA_CONTACTO)){
                                                                                stringBuilder.append("INFORMAÇÃO GRDS: O user ").append(novidadeGRDS.getUsernameUser()).append(" aceitou o pedido de contacto do user ").append(novidadeGRDS.getFriend()).append("!");
                                                                                System.out.println(stringBuilder.toString());
                                                                            }
                                                                            else {
                                                                                if(novidadeGRDS.getTipoMsg().equals(REJEITA_CONTACTO)){
                                                                                    stringBuilder.append("INFORMAÇÃO GRDS: O user ").append(novidadeGRDS.getUsernameUser()).append(" rejeitou o pedido de contacto do user ").append(novidadeGRDS.getFriend()).append("!");
                                                                                    System.out.println(stringBuilder.toString());
                                                                                }
                                                                                else {
                                                                                    if(novidadeGRDS.getTipoMsg().equals(ELIMINA_CONTACTO)){
                                                                                        stringBuilder.append("INFORMAÇÃO GRDS: O user ").append(novidadeGRDS.getUsernameUser()).append(" eliminou o contacto ").append(novidadeGRDS.getFriend()).append("!");
                                                                                        System.out.println(stringBuilder.toString());
                                                                                    }
                                                                                    else {
                                                                                        if(novidadeGRDS.getTipoMsg().equals(MENSAGEM)){
                                                                                            try{
                                                                                                Integer idGrupo = Integer.parseInt(novidadeGRDS.getReceiver());
                                                                                                stringBuilder.append("INFORMAÇÃO GRDS: O user ").append(novidadeGRDS.getUsernameUser()).append(" enviou a mensagem ").append(novidadeGRDS.getIdMsg()).append(" para o grupo ").append(novidadeGRDS.getReceiver()).append("!");
                                                                                                System.out.println(stringBuilder.toString());
                                                                                            }catch (NumberFormatException e){
                                                                                                stringBuilder.append("INFORMAÇÃO GRDS: O user ").append(novidadeGRDS.getUsernameUser()).append(" enviou a mensagem ").append(novidadeGRDS.getIdMsg()).append(" para o user ").append(novidadeGRDS.getReceiver()).append("!");
                                                                                                System.out.println(stringBuilder.toString());
                                                                                            }
                                                                                        }
                                                                                        else {
                                                                                            if(novidadeGRDS.getTipoMsg().equals(ELIMINA_MENSAGEM)){
                                                                                                stringBuilder.append("INFORMAÇÃO GRDS: O user ").append(novidadeGRDS.getUsernameUser()).append(" apagou a mensagem ").append(novidadeGRDS.getIdMsg()).append("!");
                                                                                                System.out.println(stringBuilder.toString());
                                                                                            }
                                                                                            else{
                                                                                                if (novidadeGRDS.getTipoMsg().equals(UTILIZADORES_INATIVOS)){
                                                                                                    if(novidadeGRDS.getUsersAfetados()!=null){
                                                                                                        stringBuilder.append("INFORMAÇÃO GRDS: Os users, ");
                                                                                                        for (String userAfetado : novidadeGRDS.getUsersAfetados()){
                                                                                                            stringBuilder.append(userAfetado).append(", ");
                                                                                                        }
                                                                                                        stringBuilder.append("foram desconectados a nível da base de dados por inatividade!");
                                                                                                        System.out.println(stringBuilder);
                                                                                                    }
                                                                                                }
                                                                                                else{
                                                                                                    if(novidadeGRDS.getTipoMsg().equals(FICHEIRO)) {
                                                                                                        try{
                                                                                                            Integer idGrupo = Integer.parseInt(novidadeGRDS.getReceiver());
                                                                                                            stringBuilder.append("INFORMAÇÃO GRDS: O user ").append(novidadeGRDS.getUsernameUser()).append(" enviou o ficheiro ").append(novidadeGRDS.getIdMsg()).append("-").append(novidadeGRDS.getNomeFicheiro()).append(" para o grupo ").append(novidadeGRDS.getReceiver()).append("!");
                                                                                                            System.out.println(stringBuilder.toString());
                                                                                                        }catch (NumberFormatException e){
                                                                                                            stringBuilder.append("INFORMAÇÃO GRDS: O user ").append(novidadeGRDS.getUsernameUser()).append(" enviou o ficheiro ").append(novidadeGRDS.getIdMsg()).append("-").append(novidadeGRDS.getNomeFicheiro()).append(" para o user ").append(novidadeGRDS.getReceiver()).append("!");
                                                                                                            System.out.println(stringBuilder.toString());
                                                                                                        }
                                                                                                        if(!ficheiros.verificaExistenciaFicheiro(novidadeGRDS.getIdFicheiro())){
                                                                                                            ThreadRecebeFicheiroServ threadRecebeFicheiroServ = new ThreadRecebeFicheiroServ(novidadeGRDS.getIpFicheiro(),novidadeGRDS.getPortoFicheiro(),novidadeGRDS.getIdFicheiro(),novidadeGRDS.getNomeFicheiro(),ficheiros, idServ);
                                                                                                            threadRecebeFicheiroServ.start();
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                ArrayList<String> afetados = novidadeGRDS.getUsersAfetados();
                if(afetados!=null){
                    for (String afetado : afetados){
                        clientes.addNovidadeCli(afetado,novidadeGRDS);
                    }
                }
            }catch (IOException | ClassNotFoundException e) {
                return;
            }
        }
    }

}
