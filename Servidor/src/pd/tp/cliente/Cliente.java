package pd.tp.cliente;

import pd.tp.comum.AtualizacaoServidor;
import pd.tp.comum.NovidadeGRDS;

import java.util.ArrayList;
import java.util.List;

public class Cliente {
    String username;
    ArrayList<AtualizacaoServidor> novidades;

    public Cliente(String username){
        this.username = username;
        this.novidades = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void addNovidade(NovidadeGRDS novidade){
        AtualizacaoServidor atualizacaoServidor = new AtualizacaoServidor();
        atualizacaoServidor.setIdGrupo(novidade.getIdGrupo());
        atualizacaoServidor.setFriend(novidade.getFriend());
        atualizacaoServidor.setNovoNomeGrupo(novidade.getNovoNomeGrupo());
        atualizacaoServidor.setTipoMsg(novidade.getTipoMsg());
        atualizacaoServidor.setUsernameUser(novidade.getUsernameUser());
        atualizacaoServidor.setIdMsg(novidade.getIdMsg());
        atualizacaoServidor.setNovoUsernameUser(novidade.getNovoUsernameUser());
        atualizacaoServidor.setReceiver(novidade.getReceiver());
        atualizacaoServidor.setNomeGrupo(novidade.getNomeGrupo());
        atualizacaoServidor.setEnviada(false);
        atualizacaoServidor.setIdFicheiro(novidade.getIdFicheiro());
        atualizacaoServidor.setNomeFicheiro(novidade.getNomeFicheiro());

        novidades.add(atualizacaoServidor);
    }

    public void clearNovidades(){
        novidades.removeIf(AtualizacaoServidor::isEnviada);
    }

    public ArrayList<AtualizacaoServidor> getNovidades(){
        for (AtualizacaoServidor at : novidades){
            at.setEnviada(true);
        }
        return novidades;
    }
}
