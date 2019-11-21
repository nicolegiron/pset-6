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
    public static final int INSUFFICIENT = 1;
    public static final int SUCCESS = 2;    
    public static final int INVALIDMAX = 3;
    public static final int INVALIDADD= 4;
    public static final long INVALIDTOP = 1000000000000L;

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
        		
        		if(firstName.length() > 20) {
        			System.out.println("\nFirst Name is too long. Please shorten the length.\n");
        			System.out.print("First Name: ");
            		firstName = in.nextLine();
        		}

        		System.out.print("Last Name: ");
        		String lastName = in.nextLine();
        		
        		if(lastName.length() > 30) {
        			System.out.println("\nLast Name is too long. Please shorten the length.\n");
        			System.out.print("Last Name: ");
        			lastName = in.nextLine();
        		}

        		System.out.print("Pin: ");
        		int pin = in.nextInt();
        		
        		if(pin > 9999) {
        			System.out.println("\nPin is too long. Please enter a pin 1000-9999.\n");
        			System.out.print("Pin: ");
        			pin = in.nextInt();
        		}else if(pin < 1000) {
        			System.out.println("\nPin is too short. Please enter a pin 1000-9999.\n");
        			System.out.print("Pin: ");
        			pin = in.nextInt();
        		}
        		
        		makeNewAccount(firstName, lastName, pin);
        		
        	} else {
        		
        		long  accountNo = Long.valueOf(accountNoString);
        		
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
            			bank.save();
            			shutdown();
            		} else {
            			System.out.println("\nInvalid account number and/or PIN.\n");
            			needNextLine++;
            		}
            	}
        	}
    	}
    }
    
    public void makeNewAccount(String firstName, String lastName, int pin) {
    	User newUser =  new User(firstName, lastName);

		BankAccount newAccount = bank.createAccount(pin, newUser);
		
		long newAccountNo = newAccount.getAccountNo();
		System.out.println("\nThank you. Your account number is " + newAccountNo
			+ ".\nPlease login to access your newly created account\n");
		needNextLine++;
		bank.update(newAccount);
		bank.save();
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
    	System.out.println("\nCurrent balance: " + activeAccount.getBalance() + "\n");
    }

    public void deposit() {
    	System.out.println(activeAccount.toString());
    	System.out.println("\nEnter amount: ");
    	double amount = in.nextDouble();
    	
    	int status = activeAccount.deposit(amount);
        if (status == ATM.INVALID) {
            System.out.println("\nDeposit rejected. Amount must be greater than $0.00.\n");
        } else if (status == ATM.INVALIDMAX) {
            System.out.println("\nDeposit rejected. Amount would cause balance to exceed $999,999,999,999.99.\n");
        } else {
            System.out.println("\nDeposit accepted.\n");
        }
    	bank.update(activeAccount);
		bank.save();
		System.out.println(activeAccount.toString());
    }

    public void withdraw() {
    	System.out.println("\nEnter amount: ");
    	double amount = in.nextDouble();

    	int status = activeAccount.withdraw(amount);
        if (status == ATM.INVALID) {
            System.out.println("\nWithdrawal rejected. Amount must be greater than $0.00.\n");
        } else if (status == ATM.INSUFFICIENT) {
            System.out.println("\nWithdrawal rejected. Insufficient funds.\n");
        } else if (status == ATM.SUCCESS) {
            System.out.println("\nWithdrawal accepted.\n");
        }
    	
    	bank.update(activeAccount);
		bank.save();
    }
    
    
	public void transfer() {
    	System.out.println("Enter account: ");
    	long accountNo = in.nextLong();
    	System.out.println("Enter amount: ");
    	long amount = in.nextLong();
    	
    	BankAccount currentAccount = bank.getAccount(accountNo);
    	
    	int depositStatus = 0;
    	if(currentAccount != null) {
    		depositStatus = currentAccount.deposit(amount);
    	}
    	
    	if(depositStatus == ATM.SUCCESS) {
    		activeAccount.withdraw(amount);
    	}
    	
    	if(currentAccount == null) {
    		System.out.println("\nTransfer rejected. Destination account not found.\n");
    	} else if (depositStatus == ATM.INVALID) {
    		System.out.println("\nTransfer rejected. Amount must be greater than $0.00.\n");
    	} else if (depositStatus == ATM.INVALIDMAX) {
    		System.out.println("\nTransfer rejected. Amount would cause destination balance to exceed $999,999,999,999.99. \n");
    	} else if (depositStatus == ATM.SUCCESS) {
    		System.out.println("\nTransfer accepted.\n");
    	} else {
    		System.out.println("\nTransfer rejected. Insufficient funds.\n");
    	}
    		
    	bank.update(activeAccount);
		bank.save();
		System.out.println(currentAccount.toString());
		if(currentAccount != null) {
			bank.update(currentAccount);
			bank.save();
		}
    }

    public void shutdown() {
    	if(in != null) {
    		in.close();
    	}
		bank.save();
    	System.out.println("\nGoodbye!");
    	System.exit(0);
    }

    public static void main(String[] args) {
        ATM atm = new ATM();

        atm.startup();
    }
}
