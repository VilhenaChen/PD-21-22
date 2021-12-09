package pd.tp.grds.threads;

import pd.tp.grds.servidor.Servidor;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;

public class ThreadComunicacao extends Thread{
    DatagramPacket dp;
    DatagramSocket ds;
    ArrayList<Servidor> servidores;
    String tipo;
    int porto;
    int indiceUltimoServidorAtribuido;

    public ThreadComunicacao(DatagramPacket dp, DatagramSocket ds, ArrayList<Servidor> servidores, int indiceUltimoServidorAtribuido) {
        this.dp = dp;
        this.ds = ds;
        this.servidores = servidores;
        this.indiceUltimoServidorAtribuido = indiceUltimoServidorAtribuido;
    }

    @Override
    public void run() {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(dp.getData());
            ObjectInputStream in = new ObjectInputStream(bais);
            tipo = (String) in.readObject();
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
        }catch (IOException |ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    //Funções Novo Cliente

    public void novoCliente(){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            getIndiceServidorAAtribuir();
            Servidor server = servidores.get(indiceUltimoServidorAtribuido);

            HashMap<String,String> IP_SERVER = new HashMap<>();
            IP_SERVER.put("IP",server.getIp());
            IP_SERVER.put("PORTO", String.valueOf(server.getPorto()));
            out.writeUnshared(IP_SERVER);
            out.flush();
            byte[] msgBytes = baos.toByteArray();

            dp.setData(msgBytes);
            dp.setLength(msgBytes.length);
            ds.send(dp);

            System.out.println("Enviei mensagem ao novo cliente com o servidor: " + server.getId() + " com o Porto: " + server.getPorto() + " e o IP: " + server.getIp());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getIndiceServidorAAtribuir(){
        if(indiceUltimoServidorAtribuido == (servidores.size() - 1)){
            indiceUltimoServidorAtribuido = 0;
        }
        else {
            indiceUltimoServidorAtribuido++;
        }
    }

    //Funções Novo Servidor

    public void novoServidor(){
        try {
            String[] arrayAux = tipo.split(",");
            porto = Integer.parseInt(arrayAux[1]);
            int novoId = getNovoId();
            servidores.add(new Servidor(novoId, dp.getAddress().getHostAddress(), porto));

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

    public int getNovoId(){
        int cont = 0;
        boolean nenhumIgual;
        do{
            nenhumIgual = true;
            for(Servidor s : servidores){
                if(s.getId()==cont){
                    cont++;
                    nenhumIgual = false;
                    break;
                }
            }
        }while (nenhumIgual == false);
        return cont;
    }

    //Funções Alteração Porto Servidor

    public void infoPortoServidor(){
        String[] arrayAux = tipo.split(",");
        int id = Integer.parseInt(arrayAux[1]);
        int porto = Integer.parseInt(arrayAux[2]);
        alteraPorto(id,porto);
        System.out.println("Alterei o porto do Servidor: " + id + " para: " + porto);
    }

    public void alteraPorto(int id,int porto){
        for(Servidor s : servidores){
            if(s.getId() == id){
                s.setPorto(porto);
                return;
            }
        }
    }


}
