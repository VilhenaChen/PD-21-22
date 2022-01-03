package pd.tp.comum;

import java.io.Serializable;

public class Ficheiro implements Serializable {
    private final static long serialVersionUID = 2L;

    int idFicheiro;
    String name;
    String sender;
    String receiver;
    String date;

    public Ficheiro(String name, String sender, String receiver, String date) {
        this.name = name;
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
    }

    public Ficheiro( int id,String name){
        this.name = name;
        this.idFicheiro = id;
    }

    public int getIdFicheiro() {
        return idFicheiro;
    }

    public void setIdFicheiro(int idFicheiro) {
        this.idFicheiro = idFicheiro;
    }

    public String getName() {
        return name;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Ficheiro de " + sender + " para " + receiver + " data: " + date + "\n\t" + name;
    }
}
