package pd.tp.comum;

import java.io.Serializable;

public class Mensagem implements Serializable {

    private final static long serialVersionUID = 2L;

    String assunto;
    String corpo;
    String sender;
    String receiver;
    String date;

    public Mensagem(String assunto, String corpo, String sender, String receiver, String date) {
        this.assunto = assunto;
        this.corpo = corpo;
        this.sender = sender;
        this.receiver = receiver;
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

    public String getReceiver() {
        return receiver;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Mensagem de " + sender + " para " + receiver + " data: " + date +"\n" + assunto + "\n\t" + corpo;
    }
}
