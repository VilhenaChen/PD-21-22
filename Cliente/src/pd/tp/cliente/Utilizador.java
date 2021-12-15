package pd.tp.cliente;

import java.io.Serializable;

public class Utilizador implements Serializable {
    private String username;
    private String nome;
    private String password;
    private Boolean logged;

    public Utilizador(String username, String password, String nome) {
        this.username = username;
        this.password = password;
        this.nome = nome;
        this.logged = false;
    }

    public Utilizador(String username, String password) {
        this.username = username;
        this.password = password;
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

    @Override
    public String toString() {
        return "Utilizador{" +
                "username='" + username + '\'' +
                ", nome='" + nome + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
