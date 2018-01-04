package net.teamfruit.discord.keeprole;

import java.io.File;

import lombok.Data;
import sx.blah.discord.Discord4J;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.modules.IModule;

@Data
public class DiscordModule implements IModule {
	private final String name = "DiscordKeepRole";
	private final String author = "Kamesuta";
	private final String version = "1.0.0";
	private final String minimumDiscord4JVersion = Discord4J.VERSION;

	private final EventHandler handler = new EventHandler(new File("./role_cache"));

	@Override
	public boolean enable(final IDiscordClient client) {
		this.handler.setEnabled(true);
		final EventDispatcher dispatcher = client.getDispatcher(); // Gets the EventDispatcher instance for this client instance
		dispatcher.registerListener(this.handler); // Registers the @EventSubscriber example class from above
		return true;
	}

	@Override
	public void disable() {
		this.handler.setEnabled(false);
	}
}
