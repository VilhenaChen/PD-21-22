package pd.tp.cliente;

import java.io.Serializable;

public class Utilizador implements Serializable {
    private String username;
    private String nome;
    private String password;

    public Utilizador(String username, String nome, String password) {
        this.username = username;
        this.nome = nome;
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
}
