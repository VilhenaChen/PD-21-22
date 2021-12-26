package pd.tp.grds.servidor;

import java.io.Serializable;

public class Servidor implements Serializable {

    int id;
    String ip;
    int porto;
    int heartbeat;

    public Servidor(int id,String ip, int porto) {
        this.id = id;
        this.ip = ip;
        this.porto = porto;
        this.heartbeat = 0;
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

    public int getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(int heartbeat) {
        this.heartbeat = heartbeat;
    }

    @Override
    public String toString() {
        return "ip='" + ip + '\'' +
                ", porto=" + porto;
    }
}
