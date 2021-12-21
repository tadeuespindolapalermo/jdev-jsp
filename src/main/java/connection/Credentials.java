package connection;

import java.util.HashMap;
import java.util.Map;

public class Credentials {
	
	private Map<String, String> authentication = new HashMap<>();
	
	private void setLocalCredentials() {
		authentication.putAll(Map.of(
			"url", "jdbc:postgresql://localhost:5432/projeto-jsp-novo?autoReconnect=true",
			"password", "postgresqlroot",
			"user", "postgres"
		));
	}
	
	private void setHerokuCredentials() {
		authentication.putAll(Map.of(
			"url", "jdbc:postgresql://ec2-3-95-130-249.compute-1.amazonaws.com:5432/dbbv7mf3n6p0q0?sslmode=require&autoReconnect=true",
			"password", "5d3933ecc93cc40170edcd4308d63b8126f4d33b9b5289dee3bd50f1b1457317",
			"user", "ngskqvnkribgse"
		));
	}
	
	public Map<String, String> getCredentials(Server server) {
		if (server.equals(Server.LOCAL)) {
			setLocalCredentials();
		}
		if (server.equals(Server.HEROKU)) {
			setHerokuCredentials();
		}
		return authentication;
	}

}
