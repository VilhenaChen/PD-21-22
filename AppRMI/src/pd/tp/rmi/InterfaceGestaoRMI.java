package pd.tp.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceGestaoRMI extends Remote {
    //métodos publicos e que dao throw da RemoteExeption

    public void pedirInformacaoServidores (InterfaceAppRMI interfaceAppRMI) throws RemoteException;
}
