package net.teamfruit.discord.keeprole;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

public class DiscordBot {
	public static IDiscordClient createClient(final String token, final boolean login) { // Returns a new instance of the Discord client
		final ClientBuilder clientBuilder = new ClientBuilder(); // Creates the ClientBuilder instance
		clientBuilder.withToken(token); // Adds the login info to the builder
		try {
			if (login)
				return clientBuilder.login(); // Creates the client instance and logs the client in
			else
				return clientBuilder.build(); // Creates the client instance but it doesn't log the client in yet, you would have to call client.login() yourself
		} catch (final DiscordException e) { // This is thrown if there was a problem building the client
			e.printStackTrace();
			return null;
		}
	}

	public static IDiscordClient initClient() {
		// Gets the client object (from the first example)
		final IDiscordClient client = createClient(System.getProperty("net.teamfruit.discord.key"), true);
		if (client!=null)
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				System.out.println("logout");
				RetryRunner.retry(() -> {
					client.logout();
					return true;
				});
			}));
		return client;
	}

	public static void main(final String[] args) {
		initClient();
	}
}
