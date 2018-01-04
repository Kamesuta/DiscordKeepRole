package net.teamfruit.discord.keeprole;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import net.teamfruit.discord.keeprole.model.UserList;
import net.teamfruit.discord.keeprole.model.UserRole;
import net.teamfruit.persistence.Persistence;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserRoleUpdateEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

@Log
public class EventHandler {
	private final UserRoleManager manager;
	private @Setter @Getter boolean enabled = true;

	public EventHandler(final File cacheDir) {
		this.manager = new UserRoleManager(cacheDir);
		log.info("discord keep role feature loaded");
	}

	private void updateMember(final IGuild guild, final IUser user, final boolean isMember) {
		try {
			final File listFile = this.manager.getGuildMemberListFile(guild);
			final UserList userlist = Persistence.load(listFile, UserList.class);
			Map<Long, Boolean> savedUsers = userlist.getUserIds();
			if (isMember)
				loadAndAddRole(guild, user);
			else
				log.info(String.format("left user tracked (guild: %s, user: %s)", guild.getName(), user.getName()+"#"+user.getDiscriminator()));
			if (savedUsers==null)
				savedUsers = Maps.newHashMap();
			savedUsers.put(user.getLongID(), !isMember);
			userlist.setUserIds(savedUsers);
			Persistence.write(listFile, userlist, Persistence.L2F_LIST_PRETTY_PRINTER);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void updateMembers(final IGuild guild) {
		try {
			final IDiscordClient client = guild.getClient();
			final File listFile = this.manager.getGuildMemberListFile(guild);
			final List<IUser> currentUsers = guild.getUsers();
			final UserList userlist = Persistence.load(listFile, UserList.class);
			Map<Long, Boolean> savedUsers = userlist.getUserIds();
			if (savedUsers!=null)
				for (final Entry<Long, Boolean> entry : savedUsers.entrySet()) {
					final Long userId = entry.getKey();
					final IUser user = client.getUserByID(userId);
					Boolean quit = entry.getValue();
					if (user!=null&&currentUsers.contains(user)) {
						if (quit!=null&&quit) {
							loadAndAddRole(guild, user);
							log.info(String.format("left user resumed (guild: %s, user: %s)", guild.getName(), user.getName()+"#"+user.getDiscriminator()));
						}
						quit = false;
					} else {
						quit = true;
						if (user!=null)
							log.info(String.format("left user tracked (guild: %s, user: %s)", guild.getName(), user.getName()+"#"+user.getDiscriminator()));
						else
							log.info(String.format("left user tracked (guild: %s, user id: %s)", guild.getName(), userId));
					}
					entry.setValue(quit);
				}
			else
				savedUsers = Maps.newHashMap();
			for (final IUser user : currentUsers) {
				final long userId = user.getLongID();
				if (!savedUsers.containsKey(userId))
					savedUsers.put(userId, false);
			}
			userlist.setUserIds(savedUsers);
			Persistence.write(listFile, userlist, Persistence.L2F_LIST_PRETTY_PRINTER);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void saveRole(final IGuild guild, final IUser user) {
		try {
			final UserRole userrole = new UserRole();
			userrole.setRoles(guild, user.getRolesForGuild(guild));
			Persistence.write(this.manager.getUserRoleFile(guild, user), userrole, Persistence.L2F_LIST_PRETTY_PRINTER);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void loadAndAddRole(final IGuild guild, final IUser user) {
		final UserRole userrole = Persistence.load(this.manager.getUserRoleFile(guild, user), UserRole.class);
		final List<IRole> roles = userrole.getRoles(guild);
		if (roles!=null)
			for (final IRole role : roles)
				RetryRunner.retry(() -> {
					user.addRole(role);
					return true;
				});
	}

	@EventSubscriber
	public void onGuildReadyEvent(final GuildCreateEvent event) {
		if (!isEnabled())
			return;
		final IGuild guild = event.getGuild();
		updateMembers(guild);
		for (final IUser user : guild.getUsers()) {
			saveRole(guild, user);
			log.info(String.format("save roles (guild: %s, user: %s)", guild.getName(), user.getName()+"#"+user.getDiscriminator()));
		}
	}

	@EventSubscriber
	public void onUserRoleUpdateEvent(final UserRoleUpdateEvent event) {
		if (!isEnabled())
			return;
		final IGuild guild = event.getGuild();
		final IUser user = event.getUser();
		saveRole(guild, user);
		log.info(String.format("save roles (guild: %s, user: %s)", guild.getName(), user.getName()+"#"+user.getDiscriminator()));
	}

	@EventSubscriber
	public void onUserJoinEvent(final UserJoinEvent event) {
		if (!isEnabled())
			return;
		final IGuild guild = event.getGuild();
		final IUser user = event.getUser();
		updateMember(guild, user, true);
		log.info(String.format("load roles (guild: %s, user: %s)", guild.getName(), user.getName()+"#"+user.getDiscriminator()));
	}

	@EventSubscriber
	public void onUserLeaveEvent(final UserLeaveEvent event) {
		if (!isEnabled())
			return;
		final IGuild guild = event.getGuild();
		final IUser user = event.getUser();
		updateMember(guild, user, false);
	}
}
