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
    public static final int INVALIDADD = 4;
    public static final long INVALIDTOP = 1000000000000L;
    
    public static final int PINERROR = 0;
    public static final int PINSUCCESS = 1;

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
    		
    		String accountNoString;
    		do {
    			System.out.print("Account No.: ");
    			accountNoString = in.nextLine();
    		} while(!accountNoString.equals("+") && (accountNoString.equals("") || Long.valueOf(accountNoString) < 100000001L
    				  || Long.valueOf(accountNoString) > 999999999L) && !accountNoString.equals("-1"));
    		

        	if(accountNoString.equals("+")) {
        		
        		String firstName;
        		do {
        			System.out.print("First Name: ");
            		firstName = in.nextLine();
        		} while(firstName.length() > 20 || firstName.equals(""));
        		
        		
        		String lastName;
        		do {
        			System.out.print("Last Name: ");
        			lastName = in.nextLine();
        		} while (lastName.length() > 30 || lastName.equals(""));
        		
        		int pin;
        		Long longPin;
        		do {
        			System.out.print("Pin: ");
                	pin = in.nextInt();
                	longPin = Long.valueOf(pin);
        		} while (longPin > 9999L || longPin < 1000L);
        		
        		
        		makeNewAccount(firstName, lastName, pin);
        		
        	} else {
        		
        		long accountNo = Long.valueOf(accountNoString);
        		
        		int pin;
        		Long longPin;
        		do {
        			System.out.print("Pin: ");
                	longPin = in.nextLong();
                	pin = Math.toIntExact(longPin);
        		} while ((longPin > 9999L || longPin < 1000L) && pin != -1);

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
    	System.out.println("\nEnter amount: ");
    	double amount = in.nextDouble();
    	
    	int status = activeAccount.deposit(amount);
        if (status == ATM.INVALID) {
            System.out.println("\nDeposit rejected. Amount must be greater than $0.00.\n");
        } else if (status == ATM.INVALIDMAX) {
            System.out.println("\nDeposit rejected. Amount would cause balance to exceed $999,999,999,999.99.\n");
        } else if (status == ATM.SUCCESS){
            System.out.println("\nDeposit accepted.\n");
        } else {
        	System.out.println("there is an error");
        }
    	bank.update(activeAccount);
		bank.save();
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
    	if(currentAccount != null && currentAccount != activeAccount) {
    		depositStatus = currentAccount.transfer(amount, "deposit");
    	} else if(depositStatus == ATM.SUCCESS) {
    		activeAccount.transfer(amount, "withdraw");
    	}
    	
    	if(currentAccount == null) {
    		System.out.println("\nTransfer rejected. Destination account not found.\n");
    	} else if (currentAccount == activeAccount) {
    		System.out.println("\nCannot transfer to same account.\n");
    	} else if (depositStatus == ATM.INVALID) {
    		System.out.println("\nTransfer rejected. Amount must be greater than $0.00.\n");
    	} else if (depositStatus == ATM.INVALIDMAX) {
    		System.out.println("\nTransfer rejected. Amount would cause destination balance to exceed $999,999,999,999.99. \n");
    	} else if (depositStatus == ATM.INSUFFICIENT)  {
    		System.out.println("\nTransfer rejected. Insufficient funds.\n");
    	} else if (depositStatus == ATM.SUCCESS) {
    		System.out.println("\nTransfer accepted.\n");
    	}
    		
    	bank.update(activeAccount);
		bank.save();
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
