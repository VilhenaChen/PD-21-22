package pd.tp.comum;

import java.io.Serializable;
import java.util.ArrayList;

public class NovidadeGRDS implements Serializable {

    private final static long serialVersionUID = 2L;

    private String tipoMsg;
    private String usernameUser;
    private String novoUsernameUser;
    private int idGrupo;
    private String nomeGrupo;
    private String novoNomeGrupo;
    private String friend;
    private String receiver;
    private int idMsg;
    private int idFicheiro;
    private ArrayList<String> usersAfetados = new ArrayList<>();
    private int idServidor;
    private String ipFicheiro;
    private int portoFicheiro;
    private String nomeFicheiro;

    public String getNomeFicheiro() {
        return nomeFicheiro;
    }

    public void setNomeFicheiro(String nomeFicheiro) {
        this.nomeFicheiro = nomeFicheiro;
    }

    public String getIpFicheiro() {
        return ipFicheiro;
    }

    public void setIpFicheiro(String ipFicheiro) {
        this.ipFicheiro = ipFicheiro;
    }

    public int getPortoFicheiro() {
        return portoFicheiro;
    }

    public void setPortoFicheiro(int portoFicheiro) {
        this.portoFicheiro = portoFicheiro;
    }

    public int getIdFicheiro() {
        return idFicheiro;
    }

    public void setIdFicheiro(int idFicheiro) {
        this.idFicheiro = idFicheiro;
    }

    public int getIdServidor() {
        return idServidor;
    }

    public void setIdServidor(int idServidor) {
        this.idServidor = idServidor;
    }

    public String getTipoMsg() {
        return tipoMsg;
    }

    public void setTipoMsg(String tipoMsg) {
        this.tipoMsg = tipoMsg;
    }

    public String getUsernameUser() {
        return usernameUser;
    }

    public void setUsernameUser(String usernameUser) {
        this.usernameUser = usernameUser;
    }

    public boolean verificaUtilizadorRepetido(String username){
        if(usersAfetados.isEmpty()) {
            return false;
        }
        for (String user : usersAfetados) {
            if (user.equals(username)){
                return true;
            }
        }
        return false;
    }

    public void addUserAfetado(String username){
        if(!verificaUtilizadorRepetido(username)){
            usersAfetados.add(username);
        }
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getNovoUsernameUser() {
        return novoUsernameUser;
    }

    public void setNovoUsernameUser(String novoUsernameUser) {
        this.novoUsernameUser = novoUsernameUser;
    }

    public String getNomeGrupo() {
        return nomeGrupo;
    }

    public void setNomeGrupo(String nomeGrupo) {
        this.nomeGrupo = nomeGrupo;
    }

    public String getNovoNomeGrupo() {
        return novoNomeGrupo;
    }

    public void setNovoNomeGrupo(String novoNomeGrupo) {
        this.novoNomeGrupo = novoNomeGrupo;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public int getIdMsg() {
        return idMsg;
    }

    public void setIdMsg(int idMsg) {
        this.idMsg = idMsg;
    }

    public ArrayList<String> getUsersAfetados() {
        return usersAfetados;
    }

    @Override
    public String toString() {
        return "Tipo: " + tipoMsg + " Username: " + usernameUser;
    }
}
