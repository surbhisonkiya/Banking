package upm.ssa.bank;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class MainBank {

	private static final int SESSION_TIMEOUT = 5000;

	private ZooKeeper zk = null;
	private static Bank bank = null;

	String[] hosts = {"138.4.31.89:2181", "138.4.31.90:2182"};

	public static void main(String[] args) throws KeeperException, InterruptedException {

		new MainBank();

	}

	public MainBank() throws KeeperException, InterruptedException {

		int i = new Random().nextInt(hosts.length);
		try {
			this.zk = new ZooKeeper(hosts[i], SESSION_TIMEOUT, null);
		} catch (IOException e) {
			e.printStackTrace();
		}

        bank = new Bank(this.zk);
        initMembers(bank);

		boolean correct = false;
		int     menuKey = 0;
		boolean exit    = false;
		Scanner sc      = new Scanner(System.in);
		int accNumber   = 0;
		int balance     = 0;
		Client client   = null;
		boolean status  = false;

		while (!exit) {
			try {
				correct = false;
				menuKey = 0;
				while (!correct) {
					System. out .println(">>> Enter desired op:\n"
							+ "1) Create -> to create a client.\n "
							+ "2) Read -> to show balance of a client.\n "
							+ "3) Update -> to update balance of a client.\n"
							+ "4) Delete -> to delete a client from database.\n"
							+ "5) BankDB -> to show list of client in bank's databse0.\n "
							+ "6) Exit -> to exit application");
					if (sc.hasNextInt()) {
						menuKey = sc.nextInt();
						correct = true;
					} else {
						sc.next();
						System.out.println("The input inserted is not an integer");
					}
				}

				switch (menuKey) {
					case 1: // Create client
						bank.createClient(this.createClient(sc));
						break;
					case 2: // Read client
						
						
						System. out .print(">>> Enter account number (int) = ");
						if (sc.hasNextInt()) {
							accNumber = sc.nextInt();
							client = bank.readClient(accNumber);
							System.out.println("Client: " + client);
						} else {
							System.out.println("The input inserted is not an integer");
							sc.next();
						}
						break;
					case 3: // Update client
						System. out .print(">>> Enter account number (int) = ");
						if (sc.hasNextInt()) {
							accNumber = sc.nextInt();
						} else {
							System.out.println("The input inserted is not an integer");
							sc.next();
						}
						System. out .print(">>> Enter balance (int) = ");
						if (sc.hasNextInt()) {
							balance = sc.nextInt();
						} else {
							System.out.println("The input inserted is not an integer");
							sc.next();
						}
						bank.updateClient(accNumber, balance);
						break;
					case 4: // Delete client
						System. out .print(">>> Enter account number (int) = ");
						if (sc.hasNextInt()) {
							accNumber = sc.nextInt();
							bank.deleteClient(accNumber);
						} else {
							System.out.println("The input inserted is not an integer");
							sc.next();
						}
						break;
					case 5: // Get bank DB
						System.out.println(bank.toString());
//						bank.sendCreateBank();
						break;
					case 6:
						exit = true;
						bank.close();
					default:
						break;
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		sc.close();
	}

	public Client createClient(Scanner sc) {
		int accNumber = 0;
		String name   = null;
		int balance   = 0;

		System.out.print(">>> Enter account number (int) = ");
		if (sc.hasNextInt()) {
			accNumber = sc.nextInt();
		} else {
			System.out.println("The input inserted is not an integer");
			sc.next();
			return null;
		}

		System.out.print(">>> Enter name (String) = ");
		if (!sc.hasNextInt()) {
			name =sc.next();
		} else {
			System.out.println("The input must be a string");
			sc.next();
			return null;
		}

		System.out.print(">>> Enter balance (int) = ");
		if (sc.hasNextInt()) {
			balance = sc.nextInt();
		} else {
			System.out.println("The input inserted is not an integer");
			sc.next();
			return null;
		}
		return new Client(accNumber, name, balance);
	}

	public void initMembers(Bank bank) {

		bank.handleReceiverMsg(new OpsBank(OpsEnum.CREATE_CLIENT, new Client(1, "Surbhi Sonkiya", 1000)));
		bank.handleReceiverMsg(new OpsBank(OpsEnum.CREATE_CLIENT, new Client(2, "Giovanni Vuolo", 2500)));
		bank.handleReceiverMsg(new OpsBank(OpsEnum.CREATE_CLIENT, new Client(3, "Raffaele Perini", 6500)));
		bank.handleReceiverMsg(new OpsBank(OpsEnum.CREATE_CLIENT, new Client(4, "Zsolt Dargo", 4000)));
		bank.handleReceiverMsg(new OpsBank(OpsEnum.CREATE_CLIENT, new Client(5, "Carol Martina", 499)));
		bank.handleReceiverMsg(new OpsBank(OpsEnum.CREATE_CLIENT, new Client(6, "Christopher James", 299)));
	}

}

