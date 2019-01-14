package upm.ssa.watchers;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class NodeDownWatcher implements Watcher {
    @Override
    public void process(WatchedEvent event) {
        if (event.getType() == Event.EventType.NodeDeleted) {

            System.out.println("Node is down ---> " + event.getPath());


        }
    }
}
