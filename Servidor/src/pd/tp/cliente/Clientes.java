package pd.tp.cliente;

import pd.tp.comum.AtualizacaoServidor;
import pd.tp.comum.NovidadeGRDS;

import java.util.ArrayList;

public class Clientes {
    ArrayList<Cliente> clientes;

    public Clientes(){
        this.clientes = new ArrayList<>();
    }

    public void addCli(String username){
        synchronized (this.clientes) {
            clientes.add(new Cliente(username));
        }
    }

    public void addNovidadeCli(String username, NovidadeGRDS novidade){
        synchronized (this.clientes) {
            for (Cliente cli : clientes) {
                if (cli.getUsername().equals(username))
                    cli.addNovidade(novidade);
            }
        }
    }

    public void removeNovidadesCli(String username){
        synchronized (this.clientes) {
            for (Cliente cli : clientes) {
                if (cli.getUsername().equals(username)){
                    cli.clearNovidades();
                    break;
                }
            }
        }
    }

    public ArrayList<AtualizacaoServidor> getNovidadesCli(String username){
        synchronized (this.clientes) {
            for (Cliente cli : clientes) {
                if (cli.getUsername().equals(username))
                    return cli.getNovidades();
            }
        }
        return null;
    }

    public void updateUsernameCli(String username){
        synchronized (this.clientes){
            for (Cliente cli : clientes){
                if(cli.getUsername().equals(username))
                    cli.setUsername(username);
            }
        }
    }
}
