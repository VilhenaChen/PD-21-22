package pd.tp.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface InterfaceAppRMI extends Remote {
    public void imprimeServidores (ArrayList<String> servidores) throws RemoteException;
    public void novoCliente() throws RemoteException;
    public void novoServidor(int id) throws RemoteException;
    public void eliminacaoServidor(int id) throws RemoteException;
    public void notificacao() throws RemoteException;
}
