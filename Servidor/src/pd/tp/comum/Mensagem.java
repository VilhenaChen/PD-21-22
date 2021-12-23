package pd.tp.comum;

import java.io.Serializable;

public class Mensagem implements Serializable {

    private final static long serialVersionUID = 2L;

    String assunto;
    String corpo;
    String sender;
    String receveiver;
    String date;

    public Mensagem(String assunto, String corpo, String sender, String receveiver, String date) {
        this.assunto = assunto;
        this.corpo = corpo;
        this.sender = sender;
        this.receveiver = receveiver;
        this.date = date;
    }

    public String getAssunto() {
        return assunto;
    }

    public String getCorpo() {
        return corpo;
    }

    public String getSender() {
        return sender;
    }

    public String getReceveiver() {
        return receveiver;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Mensagem de " + sender + " para " + receveiver + " data: " + date +"\n" + assunto + "\n\t" + corpo;
    }
}
