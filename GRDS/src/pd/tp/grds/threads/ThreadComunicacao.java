package pd.tp.grds.threads;

import pd.tp.grds.servidor.Servidor;
import pd.tp.grds.servidor.Servidores;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;

public class ThreadComunicacao extends Thread{
    DatagramPacket dp;
    DatagramSocket ds;
    Servidores servidores;
    String tipo;
    int porto;

    public ThreadComunicacao(DatagramPacket dp, DatagramSocket ds, Servidores servidores, String tipo) {
        this.dp = dp;
        this.ds = ds;
        this.servidores = servidores;
        this.tipo = tipo;
    }

    @Override
    public void run() {
        if (tipo.equals("NOVO_CLI")){
            novoCliente();
            return;
        }
        if(tipo.startsWith("NOVO_SERV")){
            novoServidor();
            return;
        }
        if (tipo.startsWith("INFO_PORT")){
            infoPortoServidor();
        }
    }


    //Funções Novo Cliente

    public void novoCliente(){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);

            HashMap<String,String> IP_SERVER = new HashMap<>();
            IP_SERVER.put("IP",servidores.getIPServidor(servidores.getIndiceUltimoServidorAtribuido()));
            IP_SERVER.put("PORTO", String.valueOf(Integer.valueOf(servidores.getPortoServidor(servidores.getIndiceUltimoServidorAtribuido()))));
            out.writeUnshared(IP_SERVER);
            out.flush();
            byte[] msgBytes = baos.toByteArray();

            dp.setData(msgBytes);
            dp.setLength(msgBytes.length);
            ds.send(dp);

            System.out.println("Enviei mensagem ao novo cliente com o servidor: " + servidores.getIDServidor(servidores.getIndiceUltimoServidorAtribuido()) + " com o Porto: " + servidores.getPortoServidor(servidores.getIndiceUltimoServidorAtribuido()) + " e o IP: " + servidores.getIPServidor(servidores.getIndiceUltimoServidorAtribuido()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Funções Novo Servidor

    public void novoServidor(){
        try {
            String[] arrayAux = tipo.split(",");
            porto = Integer.parseInt(arrayAux[1]);
            int novoId = servidores.getNovoId();
            servidores.addServidor(new Servidor(novoId, dp.getAddress().getHostAddress(), porto));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeUnshared(novoId);
            out.flush();
            byte[] msgBytes = baos.toByteArray();

            dp.setData(msgBytes);
            dp.setLength(msgBytes.length);
            ds.send(dp);

            System.out.println("Criei o novo Servidor de Id: " + novoId + " e Porto: " + porto);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Funções Alteração Porto Servidor

    public void infoPortoServidor(){
        String[] arrayAux = tipo.split(",");
        int id = Integer.parseInt(arrayAux[1]);
        int porto = Integer.parseInt(arrayAux[2]);
        servidores.alteraPorto(id,porto);
        servidores.resetHeartbeat(id);
        System.out.println("Alterei o porto do Servidor: " + id + " para: " + porto);
    }


}
