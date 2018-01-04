package net.teamfruit.discord.keeprole;

import java.io.File;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;

public class DiscordTest {
	public static void main(final String[] args) {
		final IDiscordClient client = DiscordBot.initClient();
		final EventDispatcher dispatcher = client.getDispatcher(); // Gets the EventDispatcher instance for this client instance
		dispatcher.registerListener(new EventHandler(new File("./role_cache"))); // Registers the @EventSubscriber example class from above
	}
}
