package pd.tp.grds.servidor;

import javax.sound.sampled.Port;
import java.io.Serializable;

public class Servidor implements Serializable {

    int id;
    String ip;
    int porto_cli;
    int porto_serv;
    int heartbeat;

    public Servidor(int id, String ip, int porto_cli, int porto_serv) {
        this.id = id;
        this.ip = ip;
        this.porto_cli = porto_cli;
        this.porto_serv = porto_serv;
        this.heartbeat = 0;
    }

    public String getIp() {
        return ip;
    }

    public int getPorto_cli() {
        return porto_cli;
    }

    public void setPorto_cli(int porto_cli) {
        this.porto_cli = porto_cli;
    }

    public int getPorto_serv() {
        return porto_serv;
    }

    public void setPorto_serv(int porto_serv) {
        this.porto_serv = porto_serv;
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
        return "id= " + id + " -> ip=" + ip + ", porto= " + porto_cli;
    }
}
