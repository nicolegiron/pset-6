import java.io.IOException;
import java.util.Scanner;

public class ATM {
    
    private Scanner in;
    private BankAccount activeAccount;
    private Bank bank;
    
    public static final int VIEW = 1;
    public static final int DEPOSIT = 2;
    public static final int WITHDRAW = 3;
    public static final int LOGOUT = 4;
    
    ////////////////////////////////////////////////////////////////////////////
    //                                                                        //
    // Refer to the Simple ATM tutorial to fill in the details of this class. //
    // You'll need to implement the new features yourself.                    //
    //                                                                        //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a new instance of the ATM class.
     */
    
    public ATM() {
        this.in = new Scanner(System.in);
        
        activateAccount = new BankAccount(1234,12345678, 0, new User("Nicole", "Giron"))
        
        try {
			this.bank = new Bank();
		} catch (IOException e) {
			// cleanup any resources (i.e., the Scanner) and exit
		}
    }
    
    /*
     * Application execution begins here.
     */
    public void startup() {
    	System.out.println("Welcome to the AIT ATM!\n");
    	
    	while (true) {
    		System.out.print("Account No.:");
        	long accountNo = in.nextLong();
        	
        	System.out.print("Pin        :");
        	int pin = in.nextInt();
        	
        	if(isValidLogin(accountNo, pin)) {
        		System.out.println("\nHello, again, " + activateAccount.getAccountHolder().getFirstName() + "!\n");
        		
        		boolean validLogin = true;
        		while(validLogin) {
        			switch (getSelection()) {
        			case VIEW: showBalance(); break;
        			case DEPOSIT: deposit(); break;
        			case WITHDRAW: withdraw(); break;
        			case LOGOUT: validLogin = false; break;
        			default: System.out.println("\nInvalid selection. \n"); break;
        			}
        		}
        	
        	}else {
        		if (accountNo = -1 && pin == -1) {
        			shutdown();
        		} else {
        			System.out.println("\nInvalid account number and/or PIN.\n")
        		}
        	}    	
    	}
    }
    
    public boolean isValidLogin(logn accountNo, int pin) {
    	return accountNo == activateAccount.getAccountNo() && pin == activeAccount.getPin();
    }
    
    public int getSelection() {
    	System.out.println("[1] View balance");
    	System.out.println("[2] Deposit Money");
    	System.out.println("[3] Withdraw Money");
    	System.out.println("[4] Logout");
    	return in.nextInt();
    }
    
    public void showBalance() {
    	System.out.println("\nCurrent balance: " + activeAccount.getBalance());
    }
    
    public void deposit() {
    	System.out.println("\nEnter amount: ");
    	double amount = in.nextDouble();
    	
    	activeAccount.deposit(amount);
    	System.out.println();
    }
    
    public void withdraw() {
    	System.out.println("\nEnter amount: ");
    	double amount = in.nextDouble();
    	
    	activeAccount.withdraw(amount);
    	System.out.println();
    }
    
    public void shutdown() {
    	if(in != null) {
    		in.close();
    	}
    	
    	System.out.println("\nGoodbye!");
    	System.exit(0);
    }
    
    public static void main(String[] args) {
        ATM atm = new ATM();
        
        atm.startup();
    }
}
