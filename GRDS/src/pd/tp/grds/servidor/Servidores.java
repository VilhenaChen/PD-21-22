package pd.tp.grds.servidor;

import pd.tp.comum.NovidadeGRDS;
import pd.tp.comum.Utils;
import pd.tp.rmi.GestaoRMI;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Servidores implements Utils {

    private static final int MAX_INATIVIDADE = 60;
    private static final int MAX_INATIVIDADE_ATRIBUICAO = 20;
    private Integer indiceUltimoServidorAtribuido;
    private ArrayList<Servidor> servidores = new ArrayList<>();

    public Servidores(){
        this.indiceUltimoServidorAtribuido = -1;
        this.servidores = new ArrayList<>();
    }

    public Integer getIndiceUltimoServidorAtribuido() {
        synchronized (this.indiceUltimoServidorAtribuido){
            return indiceUltimoServidorAtribuido;
        }
    }

    public void setIndiceUltimoServidorAtribuido(Integer indiceUltimoServidorAtribuido) {
        synchronized (this.indiceUltimoServidorAtribuido){
            this.indiceUltimoServidorAtribuido = indiceUltimoServidorAtribuido;
        }
    }

    public void addServidor(Servidor servidor){
        synchronized (this.servidores){
            servidores.add(servidor);
        }
    }

    public int getNovoId(){
        int cont = 0;
        boolean nenhumIgual;
        do{
            nenhumIgual = true;
            synchronized (this.servidores){
                for(Servidor s : servidores){
                    if(s.getId()==cont){
                        cont++;
                        nenhumIgual = false;
                        break;
                    }
                }
            }
        }while (nenhumIgual == false);
        return cont;
    }

    public void alteraPorto(int id,int porto){
        synchronized (this.servidores){
            for(Servidor s : servidores){
                if(s.getId() == id){
                    s.setPorto_cli(porto);
                    return;
                }
            }
        }
    }

    public void removeServidoresInativos(GestaoRMI gestaoRMI){
        synchronized (this.servidores){
            if(getIndiceUltimoServidorAtribuido()>-1){
                if (verificaServidoresTodosInativos()) {
                    setIndiceUltimoServidorAtribuido(-1);
                }
                else{
                    if(servidores.get(getIndiceUltimoServidorAtribuido()).getHeartbeat()>MAX_INATIVIDADE){
                        getNovoIndiceDepoisDeEliminar();
                    }
                }
            }
            for (Servidor serv : servidores){
                if(serv.getHeartbeat()>MAX_INATIVIDADE){
                    gestaoRMI.eliminacaoServidor(serv.getId());
                }
            }
            servidores.removeIf(servidor -> servidor.getHeartbeat()>MAX_INATIVIDADE);
        }
    }

    private boolean verificaServidoresTodosInativos(){
        for (Servidor serv : servidores){
            if(serv.getHeartbeat()<=MAX_INATIVIDADE)
                return false;
        }
        return true;
    }

    private void getNovoIndiceDepoisDeEliminar(){
        while (servidores.get(getIndiceUltimoServidorAtribuido()).getHeartbeat()>MAX_INATIVIDADE){
            if(getIndiceUltimoServidorAtribuido() == 0){
                setIndiceUltimoServidorAtribuido(servidores.size() - 1);
            }
            else {
                int indice = getIndiceUltimoServidorAtribuido();
                indice--;
                setIndiceUltimoServidorAtribuido(indice);
            }
        }
    }

    public int getNovoindiceUltimoServidorAtribuido(){
        synchronized (this.servidores){
            if(getIndiceUltimoServidorAtribuido()==(servidores.size()-1)){
                setIndiceUltimoServidorAtribuido(0);
            }
            else {
                int indice = getIndiceUltimoServidorAtribuido();
                indice++;
                setIndiceUltimoServidorAtribuido(indice);
            }
        }
        return getIndiceUltimoServidorAtribuido();
    }

    public boolean verificaVazio(){
        synchronized (this.servidores){
            return servidores.isEmpty();
        }
    }

    public void aumentaHeartbeat(){
        synchronized (this.servidores) {
            for (Servidor serv : servidores) {
                System.out.println("Aumentei o tempo de inatividade do servidor -> " + serv.getId() + " para " + (serv.getHeartbeat() + 1));
                serv.setHeartbeat(serv.getHeartbeat() + 1);
            }
        }
    }

    public boolean verificaServidorAtivoParaAtribuir(int indice){
        synchronized (this.servidores){
            return servidores.get(indice).getHeartbeat()<=MAX_INATIVIDADE_ATRIBUICAO;
        }
    }

    public String getIPServidor(Integer indice) {
        synchronized (this.servidores){
            return servidores.get(indice).getIp();
        }
    }

    public int getPortoServidor(Integer indice) {
        synchronized (this.servidores){
            return servidores.get(indice).getPorto_cli();
        }
    }

    public int getIDServidor(int indice){
        synchronized (this.servidores){
            return servidores.get(indice).getId();
        }
    }

    public int getHeartbeatServidor(int indice){
        synchronized (this.servidores){
            return servidores.get(indice).getHeartbeat();
        }
    }

    public void resetHeartbeat(int id) {
        synchronized (this.servidores){
            for(int i = 0; i < servidores.size(); i++) {
                if(servidores.get(i).getId() == id) {
                    servidores.get(i).setHeartbeat(0);
                    return;
                }
            }
        }
    }

    public void enviaNovidadeAosServidores(DatagramSocket ds, NovidadeGRDS novidadeGRDS){
        DatagramPacket dp;
        synchronized (this.servidores){
            for (Servidor serv : servidores){
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(baos);
                    out.writeUnshared(novidadeGRDS);
                    out.flush();
                    byte[] msgTipoBytes = baos.toByteArray();
                    InetAddress ip = InetAddress.getByName(serv.ip);
                    dp = new DatagramPacket(msgTipoBytes, msgTipoBytes.length, ip, serv.getPorto_serv());
                    ds.send(dp);
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void informaServidoresdoEncerramento(DatagramSocket ds) {
        DatagramPacket dp;
        synchronized (this.servidores){
            for (Servidor serv : servidores){
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(baos);
                    out.writeUnshared(DESLIGA_GRDS);
                    out.flush();
                    byte[] msgTipoBytes = baos.toByteArray();
                    InetAddress ip = InetAddress.getByName(serv.ip);
                    dp = new DatagramPacket(msgTipoBytes, msgTipoBytes.length, ip, serv.getPorto_serv());
                    ds.send(dp);
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //RMI

    public ArrayList<String> getServidoresRMI(){
        ArrayList<String> servidoresRmi = new ArrayList<>();
        if (!servidores.isEmpty()){
            for (Servidor serv : servidores){
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Servidor ").append(serv.getId()).append(": ").append(serv.getIp()).append(":").append(serv.getPorto_serv()).append("\n\tTempo desde ultimo datagram: ").append(serv.getHeartbeat());
                servidoresRmi.add(stringBuilder.toString());
            }
        }
        else{
            servidoresRmi.add("Não existem servidores");
        }
        return servidoresRmi;
    }
}


