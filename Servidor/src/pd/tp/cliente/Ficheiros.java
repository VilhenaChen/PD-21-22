package pd.tp.cliente;

import pd.tp.comum.AtualizacaoServidor;
import pd.tp.comum.Ficheiro;
import pd.tp.comum.NovidadeGRDS;

import java.util.ArrayList;

public class Ficheiros {

    ArrayList<Ficheiro> ficheiros;
    int PortoFiles;

    public Ficheiros(int portoFiles){
        this.ficheiros = new ArrayList<>();
        this.PortoFiles = portoFiles;

    }

    public int getPortoFiles() {
        return PortoFiles;
    }

    public void setPortoFiles(int portoFiles) {
        PortoFiles = portoFiles;
    }

    public void addFicheiro(Ficheiro ficheiro){
        synchronized (this.ficheiros) {
            ficheiros.add(ficheiro);
        }
    }

    public boolean verificaExistenciaFicheiro(int id){
        synchronized (this.ficheiros){
            if(ficheiros.isEmpty())
                return false;
            for (Ficheiro f : ficheiros){
                if(f.getIdFicheiro() == id){
                    return true;
                }
            }
        }
        return false;
    }

    public String getNomeFicheiro(int id){
        synchronized (this.ficheiros){
            if(ficheiros.isEmpty())
                return "";
            for (Ficheiro f : ficheiros){
                if(f.getIdFicheiro() == id){
                    return f.getName();
                }
            }
        }
        return "";
    }

}
