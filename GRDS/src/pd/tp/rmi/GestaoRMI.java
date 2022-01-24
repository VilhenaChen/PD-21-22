package pd.tp.rmi;

import pd.tp.grds.servidor.Servidor;
import pd.tp.grds.servidor.Servidores;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class GestaoRMI extends UnicastRemoteObject implements InterfaceGestaoRMI {

    Servidores servidores;
    private volatile int idServidorInserido;
    private volatile int idServidorRemovido;
    private volatile String notificacao;
    private List<InterfaceAppRMI> listCLientes;
    private List<InterfaceAppRMI> listServidores;
    private List<InterfaceAppRMI> listNotificacoes;

    public GestaoRMI(Servidores servidores) throws RemoteException {
        this.servidores = servidores;
        this.listCLientes = new ArrayList<InterfaceAppRMI>();
        this.listServidores = new ArrayList<InterfaceAppRMI>();
        this.listNotificacoes = new ArrayList<InterfaceAppRMI>();
    }

    @Override
    public void pedirInformacaoServidores(InterfaceAppRMI interfaceAppRMI) throws RemoteException {
        interfaceAppRMI.imprimeServidores(servidores.getServidoresRMI());
    }

    @Override
    public void addNovoListenerClientes(InterfaceAppRMI listener) throws RemoteException {
        System.out.println("A adicionar listener de Clientes - " + listener);
        listCLientes.add(listener);
    }

    @Override
    public void removeNovoListenerClientes(InterfaceAppRMI listener) throws RemoteException {
        System.out.println("A remover listener de Clientes - " + listener);
        listCLientes.remove(listener);
    }

    @Override
    public void addNovoListenerServidores(InterfaceAppRMI listener) throws RemoteException {
        System.out.println("A adicionar listener de servidores - " + listener);
        listServidores.add(listener);
    }

    @Override
    public void removeNovoListenerServidores(InterfaceAppRMI listener) throws RemoteException {
        System.out.println("A remover listener de Clientes - " + listener);
        listServidores.remove(listener);
    }

    @Override
    public void addNovoListenerNotificacoes(InterfaceAppRMI listener) throws RemoteException {
        System.out.println("A adicionar listener de notificações - " + listener);
        listNotificacoes.add(listener);
    }

    @Override
    public void removeNovoListenerNotificacoes(InterfaceAppRMI listener) throws RemoteException {
        System.out.println("A remover listener de notificações - " + listener);
        listNotificacoes.remove(listener);
    }

    public synchronized void novoCliente(){
        notifyListenersClientes();
    }

    public synchronized void notifyListenersClientes(){
        for(int i = 0; i<listCLientes.size(); i++){
            try{
                listCLientes.get(i).novoCliente();
            }catch (RemoteException e){
                System.out.println("A remover listener - " + listCLientes.get(i));
                listCLientes.remove(i--);
            }
        }
    }

    public synchronized void novoServidor(int idServidor){
        this.idServidorInserido = idServidor;
        notifyListenersNovoServidor();
    }

    public synchronized void notifyListenersNovoServidor(){
        for(int i = 0; i<listServidores.size(); i++){
            try{
                listServidores.get(i).novoServidor(idServidorInserido);
            }catch (RemoteException e){
                System.out.println("A remover listener - " + listServidores.get(i));
                listServidores.remove(i--);
            }
        }
    }

    public synchronized void eliminacaoServidor(int idServidor){
        this.idServidorRemovido = idServidor;
        notifyListenersEliminacaoServidor();
    }

    public synchronized void notifyListenersEliminacaoServidor(){
        for(int i = 0; i<listServidores.size(); i++){
            try{
                listServidores.get(i).eliminacaoServidor(idServidorRemovido);
            }catch (RemoteException e){
                System.out.println("A remover listener - " + listServidores.get(i));
                listServidores.remove(i--);
            }
        }
    }

    public synchronized void notificacao(String notificacao){
        this.notificacao = notificacao;
        notifyListenersNotificacao();
    }

    public synchronized void notifyListenersNotificacao(){
        for(int i = 0; i<listNotificacoes.size(); i++){
            try{
                listNotificacoes.get(i).notificacao(notificacao);
            }catch (RemoteException e){
                System.out.println("A remover listener - " + listNotificacoes.get(i));
                listNotificacoes.remove(i--);
            }
        }
    }

}
