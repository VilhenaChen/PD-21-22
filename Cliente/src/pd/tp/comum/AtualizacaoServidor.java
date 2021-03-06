package pd.tp.comum;

import java.io.Serializable;

public class AtualizacaoServidor implements Serializable, Utils{

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
    private boolean enviada;
    private String nomeFicheiro;
    private int idFicheiro;

    public String getNomeFicheiro() {
        return nomeFicheiro;
    }

    public void setNomeFicheiro(String nomeFicheiro) {
        this.nomeFicheiro = nomeFicheiro;
    }

    public int getIdFicheiro() {
        return idFicheiro;
    }

    public void setIdFicheiro(int idFicheiro) {
        this.idFicheiro = idFicheiro;
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

    public boolean isEnviada() {
        return enviada;
    }

    public void setEnviada(boolean enviada) {
        this.enviada = enviada;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if(tipoMsg.equals(LOGIN)){
            stringBuilder.append("NOTIFICA????O: O utilizador ").append(usernameUser).append(" efetuou login!");
        }
        else {
            if(tipoMsg.equals(REGISTO)){
                stringBuilder.append("NOTIFICA????O: O utilizador ").append(usernameUser).append(" efetuou registo!");
            }
            else {
                if(tipoMsg.equals(UPDATE_NAME)){
                    stringBuilder.append("NOTIFICA????O: O utilizador ").append(usernameUser).append(" alterou o seu nome!");
                }
                else {
                    if(tipoMsg.equals(UPDATE_USERNAME)){
                        stringBuilder.append("NOTIFICA????O: O utilizador ").append(usernameUser).append(" alterou o seu username para ").append(novoUsernameUser).append("!");
                    }
                    else {
                        if(tipoMsg.equals(UPDATE_PASSWORD)){
                            stringBuilder.append("NOTIFICA????O: O utilizador ").append(usernameUser).append("  a sua password!");
                        }
                        else {
                            if(tipoMsg.equals(LOGOUT)){
                                stringBuilder.append("NOTIFICA????O: O utilizador ").append(usernameUser).append(" efetuou logout!");
                            }
                            else {
                                if(tipoMsg.equals(NOVO_GRUPO)){
                                    stringBuilder.append("NOTIFICA????O: O utilizador ").append(usernameUser).append(" criou o grupo ").append(idGrupo).append("!");
                                }
                                else {
                                    if(tipoMsg.equals(UPDATE_NOME_GRUPO)){
                                        stringBuilder.append("NOTIFICA????O: O administrador do grupo ").append(usernameUser).append(" alterou o nome do mesmo para ").append(novoNomeGrupo).append("!");
                                    }
                                    else {
                                        if(tipoMsg.equals(ADERE_A_GRUPO)){
                                            stringBuilder.append("NOTIFICA????O: O user ").append(usernameUser).append(" pediu para aderir ao grupo ").append(idGrupo).append("!");
                                        }
                                        else {
                                            if(tipoMsg.equals(SAI_DE_GRUPO)){
                                                stringBuilder.append("NOTIFICA????O: O user ").append(usernameUser).append(" saiu do grupo ").append(idGrupo).append("!");;
                                            }
                                            else {
                                                if(tipoMsg.equals(ACEITA_MEMBRO)){
                                                    stringBuilder.append("NOTIFICA????O: O administrador do grupo ").append(idGrupo).append(" aceitou o user ").append(usernameUser).append("!");
                                                }
                                                else {
                                                    if(tipoMsg.equals(REJEITA_MEMBRO)){
                                                        stringBuilder.append("NOTIFICA????O: O administrador do grupo ").append(idGrupo).append(" rejeitou o seu pedido de ades??o!");
                                                    }
                                                    else {
                                                        if(tipoMsg.equals(KICK_MEMBRO_GRUPO)){
                                                            stringBuilder.append("NOTIFICA????O: O administrador do grupo ").append(idGrupo).append(" excluiu o user ").append(usernameUser).append(" do mesmo!");
                                                        }
                                                        else {
                                                            if(tipoMsg.equals(ELIMINA_GRUPO)){
                                                                stringBuilder.append("NOTIFICA????O: O grupo ").append(idGrupo).append(" foi eliminado pelo administrador!");
                                                            }
                                                            else {
                                                                if(tipoMsg.equals(NOVO_CONTACTO)){
                                                                    stringBuilder.append("NOTIFICA????O: O user ").append(usernameUser).append(" enviou-lhe um pedido de contacto!");
                                                                }
                                                                else {
                                                                    if(tipoMsg.equals(ACEITA_CONTACTO)){
                                                                        stringBuilder.append("NOTIFICA????O: O user ").append(usernameUser).append(" aceitou o seu pedido de contacto!");
                                                                    }
                                                                    else {
                                                                        if(tipoMsg.equals(REJEITA_CONTACTO)){
                                                                            stringBuilder.append("NOTIFICA????O: O user ").append(usernameUser).append(" rejeitou o seu pedido de contacto!");
                                                                        }
                                                                        else {
                                                                            if(tipoMsg.equals(ELIMINA_CONTACTO)){
                                                                                stringBuilder.append("NOTIFICA????O: O user ").append(usernameUser).append(" eliminou o seu contacto!");
                                                                            }
                                                                            else {
                                                                                if(tipoMsg.equals(MENSAGEM)){
                                                                                    try{
                                                                                        Integer idGrupo = Integer.parseInt(receiver);
                                                                                        stringBuilder.append("NOTIFICA????O: O user ").append(usernameUser).append(" enviou a mensagem ").append(idMsg).append(" para o grupo ").append(idGrupo).append("!");
                                                                                    }catch (NumberFormatException e){
                                                                                        stringBuilder.append("NOTIFICA????O: O user ").append(usernameUser).append(" enviou-lhe a mensagem ").append(idMsg).append("!");
                                                                                    }
                                                                                }
                                                                                else {
                                                                                    if(tipoMsg.equals(ELIMINA_MENSAGEM)){
                                                                                        stringBuilder.append("NOTIFICA????O: O user ").append(usernameUser).append(" apagou a mensagem ").append(idMsg).append("!");
                                                                                    }
                                                                                    else {
                                                                                        if (tipoMsg.equals(UTILIZADORES_INATIVOS)){
                                                                                            stringBuilder.append("NOTIFICA????O: Foi dado como desconectado por inatividade!");
                                                                                        }
                                                                                        else{
                                                                                            if(tipoMsg.equals(FICHEIRO)){
                                                                                                try{
                                                                                                    Integer idGrupo = Integer.parseInt(receiver);
                                                                                                    stringBuilder.append("NOTIFICA????O: O user ").append(usernameUser).append(" enviou o ficheiro ").append(idFicheiro).append("-").append(nomeFicheiro).append(" para o grupo ").append(idGrupo).append("!");
                                                                                                }catch (NumberFormatException e){
                                                                                                    stringBuilder.append("NOTIFICA????O: O user ").append(usernameUser).append(" enviou-lhe a o ficheiro ").append(idFicheiro).append("-").append(nomeFicheiro).append("!");
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return stringBuilder.toString();
    }
}
