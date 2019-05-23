import cucumber.api.CucumberOptions;
import cucumber.api.java.Before;
import cucumber.api.junit.Cucumber;
import hellocucumber.jsch.JschData;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = {"src/test/resources/hellocucumber"},
		plugin = {"pretty", "html:target/site/cucumber-pretty", "json:target/cucumber.json"})
public class RunCucumberTest {
}