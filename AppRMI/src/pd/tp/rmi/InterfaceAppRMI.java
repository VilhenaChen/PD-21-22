package pd.tp.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface InterfaceAppRMI extends Remote {
    public void imprimeServidores (ArrayList<String> servidores) throws RemoteException;
}
