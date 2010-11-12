package team3.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class MessageTests {
    public static Test suite() {
        TestSuite suite = new TestSuite(MessageTests.class.getName());
        //$JUnit-BEGIN$
        suite.addTestSuite(ClientMessageFactoryTest.class);
        suite.addTestSuite(SimpleMessageTest.class);
        //$JUnit-END$
        return suite;
    }

}
