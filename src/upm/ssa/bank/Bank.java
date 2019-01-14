package upm.ssa.bank;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class Bank {

	private ClientDB clientDB;
	public SendMessagesBank sendMessages;
	public ZooKeeper zk;
	private String leader;


	public ClientDB getClientDB() {
		return clientDB;
	}

	public void setClientDB(ClientDB clientDB) {
		this.clientDB = clientDB;
	}

	// Operations
	private OpsManager opsManager;
	public String opNodeName; // /ops/0000000064

	// Election
	private ElectionManager electionManager;
	private String electionNodeName; // /elections/n_0000000096
	private boolean isLeader = false;

	private MembersManager membersManager;
	private String membersNodeName;

	public Bank(ZooKeeper zk) throws KeeperException, InterruptedException {
		this.zk = zk;

		electionManager = new ElectionManager(zk, this);
		this.electionNodeName = electionManager.createElectionNode();

		opsManager = new OpsManager(zk);
		this.opNodeName = opsManager.createOperationsNode();

		membersManager = new MembersManager(zk, this);
		this.membersNodeName = membersManager.createBaseNodes();
		Stat stat = new Stat();

		zk.setData(membersNodeName, this.opNodeName.getBytes(), stat.getVersion());

		Thread.sleep(1000);

		this.clientDB = new ClientDB();

		electionManager.leaderElection();
		membersManager.listenForFollowingNode(membersNodeName);
		// Set a watcher for ops
		opsManager.listenForOperationUpdates(this, this.opNodeName);

		// We set as data for the electionNodeName the opNodeName.
		// In this way we know who is the leader and its opNodeName, so that followers
		// can forward the ops to the leader (which will then dispatch them to everyone).
		stat = new Stat();
		zk.setData(this.electionNodeName, this.opNodeName.getBytes(), stat.getVersion());

		this.sendMessages = new SendMessagesBank(zk, this);
	}

	public synchronized void handleReceiverMsg(OpsBank op) {
		switch (op.getOperation()) {
			case CREATE_CLIENT:
				clientDB.createClient(op.getClient());
				break;
			case READ_CLIENT:
				clientDB.readClient(op.getAccountNumber());
				break;
			case UPDATE_CLIENT:
				clientDB.updateClient(op.getClient().getAccountNumber(),
									  op.getClient().getBalance());
				break;
			case DELETE_CLIENT:
				clientDB.deleteClient(op.getAccountNumber());
				break;
			case CREATE_BANK:
				clientDB.createBank(op.getClientDB()); 
				break;
		}
	}

	public void createClient(Client client) {
		sendMessages.sendAdd(client, isLeader);
	}

	public Client readClient(Integer accountNumber) {
		// Handled locally. No need for distributing
		return clientDB.readClient(accountNumber);
	}

	public void updateClient (int accNumber, int balance) {
		Client client = clientDB.readClient(accNumber);
		client.setBalance(balance);
		sendMessages.sendUpdate(client, isLeader);
	}

	public void deleteClient(Integer accountNumber) {
		sendMessages.sendDelete(accountNumber, isLeader);
	}

	public void sendCreateBank(){
		sendMessages.sendCreateBank(clientDB, isLeader);
	}

	public String toString() {
		return clientDB.toString();
	}


	public void close() {
		System.out.println("Session finished");
	}

	public boolean getIsLeader(){
		return isLeader;
	}

	public void setIsLeader(boolean isLeader){
		this.isLeader = isLeader;
	}

	public String getElectionNodeName(){
		return this.electionNodeName;
	}

	public void setElectionNodeName(String electionNodeName){
		this.electionNodeName = electionNodeName;
	}

	public String getLeader() {
		return leader;
	}

	public void setLeader(String leader) {
		this.leader = leader;
	}
}
