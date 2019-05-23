package hellocucumber.jsch;

import hellocucumber.jsch.configuration.ConfigurationHandler;

import javax.naming.ConfigurationException;
import java.io.IOException;

public class JschData {
	private String PATH_CFG;
	private String SSH_USER_IP;
	private String SSH_SERVER_IP;
	private String SSH_USER_PASSWORD;
	private String SSH_USER_USER;

	public JschData() throws IOException, ConfigurationException {
		ConfigurationHandler handler = new ConfigurationHandler(this);
		handler.load();
	}

	public String getPATH_CFG() {
		return PATH_CFG;
	}

	public void setPATH_CFG(String PATH_CFG) {
		this.PATH_CFG = PATH_CFG;
	}

	public String getSSH_USER_IP() {
		return SSH_USER_IP;
	}

	public void setSSH_USER_IP(String SSH_USER_IP) {
		this.SSH_USER_IP = SSH_USER_IP;
	}

	public String getSSH_SERVER_IP() {
		return SSH_SERVER_IP;
	}

	public void setSSH_SERVER_IP(String SSH_SERVER_IP) {
		this.SSH_SERVER_IP = SSH_SERVER_IP;
	}

	public String getSSH_USER_PASSWORD() {
		return SSH_USER_PASSWORD;
	}

	public void setSSH_USER_PASSWORD(String SSH_USER_PASSWORD) {
		this.SSH_USER_PASSWORD = SSH_USER_PASSWORD;
	}

	public String getSSH_USER_USER() {
		return SSH_USER_USER;
	}

	public void setSSH_USER_USER(String SSH_USER_USER) {
		this.SSH_USER_USER = SSH_USER_USER;
	}
}