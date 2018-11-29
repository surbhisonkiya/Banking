package upm.ssa.watchers;

import upm.ssa.bank.MainBank;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class NodeDownWatcher implements Watcher {
    @Override
    public void process(WatchedEvent event) {
        if (event.getType() == Event.EventType.NodeDeleted) {

            System.out.println("Node is down ---> " + event.getPath());

//            try {
//                new MainBank();
//            } catch (KeeperException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

//            Runtime rt = Runtime.getRuntime();
//            String[] commands = {"java", "com.remcokortekaas.app.Main"};
//            try {
//                rt.exec(commands);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        }
    }
}
