package pd.tp.rmi;

import pd.tp.grds.servidor.Servidor;
import pd.tp.grds.servidor.Servidores;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class GestaoRMI extends UnicastRemoteObject implements InterfaceGestaoRMI {

    Servidores servidores;
    public GestaoRMI(Servidores servidores) throws RemoteException {
        this.servidores = servidores;
    }

    @Override
    public void pedirInformacaoServidores(InterfaceAppRMI interfaceAppRMI) throws RemoteException {
        interfaceAppRMI.imprimeServidores(servidores.getServidoresRMI());
    }
}
