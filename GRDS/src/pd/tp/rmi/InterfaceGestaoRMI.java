package pd.tp.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceGestaoRMI extends Remote {
    //métodos publicos e que dao throw da RemoteExeption

    public void pedirInformacaoServidores (InterfaceAppRMI interfaceAppRMI) throws RemoteException;
    public  void addNovoListenerClientes(InterfaceAppRMI listener) throws RemoteException;
    public  void removeNovoListenerClientes(InterfaceAppRMI listener) throws RemoteException;
    public  void addNovoListenerServidores(InterfaceAppRMI listener) throws RemoteException;
    public  void removeNovoListenerServidores(InterfaceAppRMI listener) throws RemoteException;
    public  void addNovoListenerNotificacoes(InterfaceAppRMI listener) throws RemoteException;
    public  void removeNovoListenerNotificacoes(InterfaceAppRMI listener) throws RemoteException;
}
