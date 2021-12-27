package pd.tp.comum;

import java.util.ArrayList;

public class NovidadeGRDS {

    private String tipoMsg;
    private String usernameUser;
    private String novoUsernameUser;
    private int idGrupo;
    private String nomeGrupo;
    private String novoNomeGrupo;
    private String friend;
    private String receiver;
    private int idMsg;
    private ArrayList<String> usersAfetados;

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
        for (String user : usersAfetados){
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
}
