package userinterface;

import core.model.Account;
import core.model.StaffInformation;

import java.util.Vector;

public class LoginHandle {
    private static final LoginHandle instance = new LoginHandle();

    public static LoginHandle getInstance(){ return instance; }

    private Vector<Listener> listeners = new Vector<>();

    private Account currentAccount;

    public Account getCurrentAccount() {
        return currentAccount;
    }

    private boolean hasCurrentAccount(){ return currentAccount != null; }

    private LoginHandle(){
        currentAccount = null;
    }

    public void logAccount(Account account){
        currentAccount = account;
        Listener[] temp = new Listener[listeners.size()];
        listeners.copyInto(temp);
        for(Listener listener : temp) listener.onLogin(account);
        System.out.println("logAccount called.");
    }

    public void logOut(){
        currentAccount = null;
        Listener[] temp = new Listener[listeners.size()];
        listeners.copyInto(temp);
        for(Listener listener : temp) listener.onLogout();
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
        System.out.println("LoginHandle addListener called");
    }

    public void removeListener(Listener listener){
        listeners.remove(listener);
    }

    public interface Listener{

        void onLogin(Account account);

        void onLogout();

    }
}
