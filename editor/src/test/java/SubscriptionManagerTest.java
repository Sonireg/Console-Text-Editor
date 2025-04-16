import org.junit.jupiter.api.Test;

import editor.User.Subscriprions.SubscriptionManager;

import static org.junit.jupiter.api.Assertions.*;

public class SubscriptionManagerTest {
    @Test
    void testSubscribe() {
        SubscriptionManager sm = new SubscriptionManager();
        sm.subscribe("user1", "file1.txt");
        
        assertTrue(sm.getSubscribers("file1.txt").contains("user1"));
    }

    @Test
    void testUnsubscribe() {
        SubscriptionManager sm = new SubscriptionManager();
        sm.subscribe("user1", "file1.txt");
        sm.unsubscribe("user1", "file1.txt");
        
        assertFalse(sm.getSubscribers("file1.txt").contains("user1"));
    }
}