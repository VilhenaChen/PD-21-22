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
    private List<InterfaceAppRMI> list;

    public GestaoRMI(Servidores servidores) throws RemoteException {
        this.servidores = servidores;
        this.list = new ArrayList<InterfaceAppRMI>();
    }

    @Override
    public void pedirInformacaoServidores(InterfaceAppRMI interfaceAppRMI) throws RemoteException {
        interfaceAppRMI.imprimeServidores(servidores.getServidoresRMI());
    }

    @Override
    public void addNovoListener(InterfaceAppRMI listener) throws RemoteException {
        System.out.println("A adicionar listener - " + listener);
        list.add(listener);
    }

    @Override
    public void removeNovoListener(InterfaceAppRMI listener) throws RemoteException {
        System.out.println("A remover listener - " + listener);
        list.remove(listener);
    }

    public synchronized void novoCliente(){
        notifyListenersClientes();
    }

    public synchronized void notifyListenersClientes(){
        for(int i = 0; i<list.size(); i++){
            try{
                list.get(i).novoCliente();
            }catch (RemoteException e){
                System.out.println("A remover listener - " + list.get(i));
                list.remove(i--);
            }
        }
    }

    public synchronized void novoServidor(int idServidor){
        this.idServidorInserido = idServidor;
        notifyListenersNovoServidor();
    }

    public synchronized void notifyListenersNovoServidor(){
        for(int i = 0; i<list.size(); i++){
            try{
                list.get(i).novoServidor(idServidorInserido);
            }catch (RemoteException e){
                System.out.println("A remover listener - " + list.get(i));
                list.remove(i--);
            }
        }
    }

    public synchronized void eliminacaoServidor(int idServidor){
        this.idServidorRemovido = idServidor;
        notifyListenersEliminacaoServidor();
    }

    public synchronized void notifyListenersEliminacaoServidor(){
        for(int i = 0; i<list.size(); i++){
            try{
                list.get(i).eliminacaoServidor(idServidorRemovido);
            }catch (RemoteException e){
                System.out.println("A remover listener - " + list.get(i));
                list.remove(i--);
            }
        }
    }



}
