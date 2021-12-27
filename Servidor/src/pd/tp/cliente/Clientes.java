package pd.tp.cliente;

import java.util.ArrayList;

public class Clientes {
    ArrayList<Cliente> clientes;

    public Clientes(){
        this.clientes = new ArrayList<>();
    }

    public void addCli(String username){
        clientes.add(new Cliente(username));
    }

    public void addNovidadeCli(String username, String novidade){
        for (Cliente cli : clientes){
            if(cli.getUsername().equals(username))
                cli.addNovidade(novidade);
        }
    }

    public void removeNovidadesCli(String username){
        for (Cliente cli : clientes){
            if(cli.getUsername().equals(username))
                cli.clearNovidades();
        }
    }

    public ArrayList<String> getNovidadesCli(String username){
        for (Cliente cli : clientes){
            if(cli.getUsername().equals(username))
                return cli.getNovidades();
        }
        return null;
    }

    public void updateUsernameCli(String username){
        for (Cliente cli : clientes){
            if(cli.getUsername().equals(username))
                cli.setUsername(username);
        }
    }
}
