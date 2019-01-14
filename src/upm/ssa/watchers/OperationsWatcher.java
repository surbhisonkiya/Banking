package upm.ssa.watchers;

import upm.ssa.bank.Bank;
import upm.ssa.bank.OpsBank;
import upm.ssa.bank.OpsEnum;
import upm.ssa.bank.ElectionManager;
import upm.ssa.bank.OpsManager;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class OperationsWatcher implements Watcher {

    private ZooKeeper zk;
    private Bank bank;
    private String nodename;

    public OperationsWatcher(ZooKeeper zk, String nodename, Bank bankInstance){
        this.zk = zk;
        this.bank = bankInstance;
        this.nodename = nodename;
    }
    @Override
    public void process(WatchedEvent event) {
        if (event.getPath().equals(this.nodename)) {
            List<String> ops = null;
            try {
                ops = zk.getChildren(this.nodename, false);
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }
            
            Collections.sort(ops);
            // System.out.println("Operation List (Watcher): " + ops);

            for (String op_id : ops) {
                String nodePath = this.nodename + "/" + op_id;
                byte[] data = null;
                try {
                    data = zk.getData(nodePath, false, null);
                    Stat stat = zk.exists(nodePath, false);
                    zk.delete(nodePath, stat.getVersion());
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    OpsBank op = null;
                    try {
                        op = OpsBank.byteToObj(data);
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    bank.handleReceiverMsg(op);
                    if (this.bank.getIsLeader()) this.bank.sendMessages.forwardOpToFollowers(op);
                }
            }
        }
        try {
            zk.getChildren(this.nodename, this);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
