package pd.tp.grds.servidor;

import java.util.ArrayList;

public class Servidores {

    private static final int MAX_INATIVIDADE = 30;
    private static final int MAX_INATIVIDADE_ATRIBUICAO = 10;
    private Integer indiceUltimoServidorAtribuido;
    private ArrayList<Servidor> servidores = new ArrayList<>();

    public Servidores(){
        this.indiceUltimoServidorAtribuido = -1;
        this.servidores = new ArrayList<>();
    }

    public Integer getIndiceUltimoServidorAtribuido() {
        return indiceUltimoServidorAtribuido;
    }

    public void setIndiceUltimoServidorAtribuido(Integer indiceUltimoServidorAtribuido) {
        this.indiceUltimoServidorAtribuido = indiceUltimoServidorAtribuido;
    }

    public void addServidor(Servidor servidor){
        servidores.add(servidor);
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

    public void alteraPorto(int id,int porto){
        for(Servidor s : servidores){
            if(s.getId() == id){
                s.setPorto(porto);
                return;
            }
        }
    }

    public void removeServidoresInativos(){
        if(indiceUltimoServidorAtribuido>-1){
            if (verificaServidoresTodosInativos()) {
                indiceUltimoServidorAtribuido = -1;
            }
            else{
                if(servidores.get(indiceUltimoServidorAtribuido).getHeartbeat()>MAX_INATIVIDADE){
                    getNovoIndiceDepoisDeEliminar();
                }
            }
        }
        servidores.removeIf(servidor -> servidor.getHeartbeat()>MAX_INATIVIDADE);
    }

    private boolean verificaServidoresTodosInativos(){
        for (Servidor serv : servidores){
            if(serv.getHeartbeat()<=MAX_INATIVIDADE)
                return false;
        }
        return true;
    }

    private void getNovoIndiceDepoisDeEliminar(){
        while (servidores.get(indiceUltimoServidorAtribuido).getHeartbeat()>MAX_INATIVIDADE){
            if(indiceUltimoServidorAtribuido == 0){
                indiceUltimoServidorAtribuido = (servidores.size() - 1);
            }
            else {
                indiceUltimoServidorAtribuido--;
            }
        }
    }

    public int getNovoindiceUltimoServidorAtribuido(){
        if(indiceUltimoServidorAtribuido==(servidores.size()-1)){
            indiceUltimoServidorAtribuido = 0;
        }
        else {
            indiceUltimoServidorAtribuido++;
        }
        return indiceUltimoServidorAtribuido;
    }

    public boolean verificaVazio(){
        return servidores.isEmpty();
    }

    public void aumentaHeartbeat(){
        for (Servidor serv : servidores) {
            System.out.println("Aumentei o tempo de inatividade do servidor -> " + serv.getId() + " para " + (serv.getHeartbeat() + 1));
            serv.setHeartbeat(serv.getHeartbeat() + 1);
        }
    }

    public boolean verificaServidorAtivoParaAtribuir(int indice){
        return servidores.get(indice).getHeartbeat()<=MAX_INATIVIDADE_ATRIBUICAO;
    }

    public String getIPServidor(Integer indice) {
        return servidores.get(indice).getIp();
    }

    public int getPortoServidor(Integer indice) {
        return servidores.get(indice).getPorto();
    }

    public int getIDServidor(int indice){
        return servidores.get(indice).getId();
    }

    public int getHeartbeatServidor(int indice){
        return servidores.get(indice).getHeartbeat();
    }

    public void resetHeartbeat(int id) {
        for(int i = 0; i < servidores.size(); i++) {
            if(servidores.get(i).getId() == id) {
                servidores.get(i).setHeartbeat(0);
                return;
            }
        }
    }

}
