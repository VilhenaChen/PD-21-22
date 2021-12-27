package pd.tp.cliente;

import java.util.ArrayList;
import java.util.List;

public class Cliente {
    String username;
    ArrayList<String> novidades;

    public Cliente(String username){
        this.username = username;
        this.novidades = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void addNovidade(String novidade){
        novidades.add(novidade);
    }

    public void clearNovidades(){
        novidades.clear();
    }

    public ArrayList<String> getNovidades(){
        return (ArrayList<String>) List.copyOf(novidades);
    }
}
