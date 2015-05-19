package de.shelp.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ UserIntegrationTest.class, StateIntegrationTest.class, TourIntegrationTest.class })
public class AllTests {

}
