package pd.tp.cliente;

import pd.tp.comum.Utils;
import java.io.Serializable;

public class Utilizador implements Serializable, Utils {
    private String username;
    private String nome;
    private String password;
    private Boolean logged;
    private String resultadoComando;
    private Boolean recebiResultado;

    public Utilizador(String username, String password, String nome) {
        this.username = username;
        this.password = password;
        this.nome = nome;
        this.logged = false;
        this.resultadoComando = "";
        this.recebiResultado = false;
    }

    public Utilizador(String username, String password) {
        this.username = username;
        this.password = password;
        this.resultadoComando = "";
        this.recebiResultado = false;
    }

    public String getUsername() {
        return username;
    }

    public String getNome() {
        return nome;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getLogged() {
        return logged;
    }

    public void setLogged(Boolean logged) {
        this.logged = logged;
    }

    public String getResultadoComando() {
        synchronized (this.resultadoComando){
            return this.resultadoComando;
        }
    }

    public void setResultadoComando(String resultadoComando) {
        synchronized (this.resultadoComando){
            this.resultadoComando = resultadoComando;
        }
    }

    public void eraseResultadoComando(){
        this.resultadoComando="";
    }

    public boolean isRecebiResultado() {
        synchronized (this.recebiResultado){
           return this.recebiResultado;
        }
    }

    public void setRecebiResultado(boolean recebiResultado) {
        synchronized (this.recebiResultado){
            this.recebiResultado = recebiResultado;
        }
    }

    @Override
    public String toString() {
        return "Utilizador{" +
                "username='" + username + '\'' +
                ", nome='" + nome + '\'' +
                ", password='" + password + '\'' +
                "'} '" + resultadoComando;
    }
}
