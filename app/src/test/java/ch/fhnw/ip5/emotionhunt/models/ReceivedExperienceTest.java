package ch.fhnw.ip5.emotionhunt.models;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for ReceivedExperience
 */
public class ReceivedExperienceTest {
    ReceivedExperience receivedExperience;

    @Before
    public void init() {
        receivedExperience = new ReceivedExperience();
    }

    @Test(expected = IllegalStateException.class)
    public void showNotificationTestPublic() throws Exception {
        receivedExperience.isPublic = true;
        receivedExperience.showNotification(null);
    }

}