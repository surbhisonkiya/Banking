package upm.ssa.bank;


import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class NodeUtils {

    /*
	 * Check if a particular znode exists. Otherwise it creates it
	 */
    public static String znodeExistsOrCreate(ZooKeeper zk, String path, byte[] data, List<ACL> ACL, CreateMode createMode) throws KeeperException, InterruptedException {
        Stat stat = zk.exists(path, false);
        String nodename = null;
        if (stat == null){
            nodename = zk.create(path, data, ACL, createMode);
        }
        return nodename;
    }

    public static String getLeaderOpNodeName (ZooKeeper zk, String leaderElectionNodeName) throws KeeperException, InterruptedException, UnsupportedEncodingException {

        String leaderOpNodeName = null;
        Stat stat = zk.exists(leaderElectionNodeName, false);
        leaderOpNodeName = new String(zk.getData(leaderElectionNodeName, false, stat), "UTF-8");

        return leaderOpNodeName;
    }
}
