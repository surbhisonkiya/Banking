package upm.ssa.bank;


import org.apache.zookeeper.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

public class SendMessagesBank implements SendMessages {

	private ZooKeeper zk;
	private Bank bank;

	public SendMessagesBank(ZooKeeper zk, Bank bank){
		this.zk = zk;
		this.bank = bank;
	}

	private void sendMessage(OpsBank op, boolean isLeader) {
		if (isLeader){
			forwardOpToFollowers(op);
		} else {
			forwardOpToLeader(op);
		}
	}

	private void forwardOpToLeader(OpsBank op) {
		byte[] opBytes = new byte[0];
		try {
			opBytes = OpsBank.objToByte(op);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Get leader opNodeName which is stored as data in the electionNodeName of the leader
		String leaderElectionNodeName = ElectionManager.root + "/" + this.bank.getLeader();
		try {
			String leaderOpNodeName = NodeUtils.getLeaderOpNodeName(zk, leaderElectionNodeName);
			zk.create(leaderOpNodeName + "/", opBytes,
					ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		} catch (KeeperException | InterruptedException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void forwardOpToNode(OpsBank op, String nodePath) {

		System.out.println("Foward to new node: " + nodePath);

		byte[] opBytes = new byte[0];
		try {
			opBytes = OpsBank.objToByte(op);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			zk.create(nodePath + "/", opBytes,
					ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void forwardOpToFollowers(OpsBank op) {

		System.out.println("Broadcast operation to followers: " + op);

		List<String> opNodes = null;
		try {
			opNodes = zk.getChildren(OpsManager.root, false);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}

		byte[] opBytes = new byte[0];
		try {
			opBytes = OpsBank.objToByte(op);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Iterator iterator = opNodes.iterator(); iterator.hasNext(); ) {
			String op_node_id = (String) iterator.next();

			// Do not send the update to the leader (itself) again
			String leaderElectionNodeName = ElectionManager.root + "/" + this.bank.getLeader();
			try {
				String leaderOpNodeName = NodeUtils.getLeaderOpNodeName(zk, leaderElectionNodeName);
				if (!op_node_id.equals(leaderOpNodeName)) {
					zk.create(OpsManager.root + "/" + op_node_id + "/", opBytes,
							ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
				}
			} catch (KeeperException | InterruptedException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendAdd(Client client, boolean isLeader) {
		OpsBank op = new OpsBank(OpsEnum.CREATE_CLIENT, client);
		if (isLeader) this.bank.handleReceiverMsg(op);
		sendMessage(op, isLeader);
	}

	public void sendUpdate(Client client, boolean isLeader) {
		OpsBank op = new OpsBank(OpsEnum.UPDATE_CLIENT, client);
		if (isLeader) this.bank.handleReceiverMsg(op);
		sendMessage(op, isLeader);
	}

	public void sendDelete(Integer accountNumber, boolean isLeader) {
		OpsBank op = new OpsBank(OpsEnum.DELETE_CLIENT, accountNumber);
		if (isLeader) this.bank.handleReceiverMsg(op);
		sendMessage(op, isLeader);
	}

	public void sendCreateBank (ClientDB clientDB, boolean isLeader) {

		// TODO only send to new connected server

		OpsBank op = new OpsBank(OpsEnum.CREATE_BANK, clientDB);
		sendMessage(op, isLeader);
	}
}
