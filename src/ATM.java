import java.io.IOException;
import java.util.Scanner;

public class ATM {

    private Scanner in;
    private BankAccount activeAccount;
    private Bank bank;

    public static final int VIEW = 1;
    public static final int DEPOSIT = 2;
    public static final int WITHDRAW = 3;
    public static final int TRANSFER = 4;
    public static final int LOGOUT = 5;

    public static final int INVALID = 0;
    public static final long INVALIDTOP = 1000000000000L;
    public static final int INSUFFICIENT = 1;
    public static final int SUCCESS = 2;

    public static final int FIRST_NAME_WIDTH = 20;
    public static final int LAST_NAME_WIDTH = 20;
    int needNextLine = 0;
    int logoutAmount = 0;

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
    		if(needNextLine != 0 || logoutAmount != 0) {
    			in.nextLine();        		
    		}
    		System.out.print("Account No.: ");
    		String accountNoString = in.nextLine();

        	if(accountNoString.equals("+")) {
        		System.out.print("First Name: ");
        		String firstName = in.nextLine();

        		System.out.print("Last Name: ");
        		String lastName = in.nextLine();

        		System.out.print("Pin: ");
        		int pin = in.nextInt();
        		
        		User newUser =  new User(firstName, lastName);

        		BankAccount newAccount = bank.createAccount(pin, newUser);
        		
        		long newAccountNo = newAccount.getAccountNo();
        		System.out.println("\nThank you. Your account number is " + newAccountNo
        			+ ". Please login to access your newly created account");
        		
        		bank.update(newAccount);
        		bank.save();
        	} else {
        		
        		long accountNo = Long.valueOf(accountNoString);
        		
        		System.out.print("Pin        : ");
            	int pin = in.nextInt();

            	if(isValidLogin(accountNo, pin)) {
            		System.out.println("\nHello, again, " + activeAccount.getAccountHolder().getFirstName() + "!\n");

            		boolean validLogin = true;
            		while(validLogin) {
            			switch (getSelection()) {
            			case VIEW: showBalance(); break;
            			case DEPOSIT: deposit(); break;
            			case WITHDRAW: withdraw(); break;
            			case LOGOUT: validLogin = false; logoutAmount++; break;
            			case TRANSFER: transfer(); break;
            			default: System.out.println("\nInvalid selection. \n"); break;
            			}
            		}

            	} else {
            		if (accountNo == -1 && pin == -1) {
            			shutdown();
            		} else {
            			System.out.println("\nInvalid account number and/or PIN.\n");
            			needNextLine++;
            		}
            	}
        	}
    	}
    }


    public boolean isValidLogin(long accountNo, int pin) {
    	activeAccount = bank.login(accountNo, pin);
    	return activeAccount != null;
    }

    public int getSelection() {
    	System.out.println("[1] View balance");
    	System.out.println("[2] Deposit Money");
    	System.out.println("[3] Withdraw Money");
    	System.out.println("[4] Transfer Money");
    	System.out.println("[5] Logout");
    	return in.nextInt();
    }

    public void showBalance() {
    	System.out.println("\nCurrent balance: " + activeAccount.getBalance());
    }

    public void deposit() {
    	System.out.println("\nEnter amount: ");
    	double amount = in.nextDouble();

    	int status = activeAccount.deposit(amount);
    	if (status == ATM.INVALID) {
    		System.out.println("\nDeposit rejected. Amount must be greater than $0.00.\n");
    	}  else if (status > ATM.INVALIDTOP) {
    		System.out.println("\nDeposit rejected. Amount must be less than $999999999999.99.\n");
    	} else if (status == ATM.SUCCESS) {
    		System.out.println("\nDeposit accepted.\n");
    	}
    }

    public void withdraw() {
    	System.out.println("\nEnter amount: ");
    	double amount = in.nextDouble();

    	int status = activeAccount.withdraw(amount);
    	if (status <= ATM.INVALID) {
    		System.out.println("\nWithdraw rejected. Amount must be greater than $0.00.\n");
    	} else if (Long.valueOf(activeAccount.getBalance()) < amount) {
    		System.out.println("\nWithdraw rejected. Insufficient funds.\n");
    	}else if (status == ATM.SUCCESS) {
    		System.out.println("\nwithdraw accepted.\n");
    	}
    }
    
    public void transfer() {
    	System.out.println("Enter account: ");
    	System.out.println("Enter amount: ");
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
