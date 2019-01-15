package upm.ssa.watchers;

import upm.ssa.bank.Bank;
import upm.ssa.bank.Client;
import upm.ssa.bank.OpsBank;
import upm.ssa.bank.OpsEnum;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class NewNodeWatcher implements Watcher {

    private ZooKeeper zk;
    private Bank bank;

    public NewNodeWatcher(ZooKeeper zkInstance, Bank bank){
        this.zk = zkInstance;
        this.bank = bank;
    }
    @Override
    public void process(WatchedEvent event) {
        System.out.println("New node: " + event.getPath());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String newMember = null;
        try {
			System.out.println("Event: " + event.toString());		
			List<String> list = zk.getChildren("/members",  false); 
			printListMembers(list);
			Collections.sort(list);
			newMember = list.get(list.size()-1);
			System.out.println("New member: "+newMember);

		} catch (Exception e) {
			System.out.println("Exception: wacherMember");
}
     
        // send current state of the database to the new node
        for (java.util.HashMap.Entry <Integer, Client>  entry : bank.getClientDB().clientDB.entrySet()) {
            Client c = entry.getValue();

            System.out.println("Client: " + c);

            bank.sendMessages.forwardOpToNode(new OpsBank(
                        OpsEnum.CREATE_CLIENT,
                        new Client(c.getAccountNumber(), c.getName(), c.getBalance())
                    ), newMember);
        }

        NodeDownWatcher nodeDownWatcher = new NodeDownWatcher();
        String nodeId = event.getPath();
        try {
            zk.exists(nodeId, nodeDownWatcher);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private void printListMembers (List<String> list) {
		System.out.println("Number of members:" + list.size());
		if (list.size() != 0) {
		System.out.print("list of members: ");
		Collections.sort(list);
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			System.out.print(string + ", ");			
		}
		}
		System.out.println();
}
}
