package pd.tp.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceGestaoRMI extends Remote {
    //métodos publicos e que dao throw da RemoteExeption

    public void pedirInformacaoServidores (InterfaceAppRMI interfaceAppRMI) throws RemoteException;
    public  void addNovoListener(InterfaceAppRMI listener) throws RemoteException;
    public  void removeNovoListener(InterfaceAppRMI listener) throws RemoteException;
}
