package pd.tp.grds.servidor;

import java.io.Serializable;

public class Servidor implements Serializable {

    int id;
    String ip;
    int porto;

    public Servidor(int id,String ip, int porto) {
        this.id = id;
        this.ip = ip;
        this.porto = porto;
    }

    public String getIp() {
        return ip;
    }

    public int getPorto() {
        return porto;
    }

    public void setPorto(int porto) {
        this.porto = porto;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ip='" + ip + '\'' +
                ", porto=" + porto;
    }
}
