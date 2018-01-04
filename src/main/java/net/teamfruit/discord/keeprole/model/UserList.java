package net.teamfruit.discord.keeprole.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UserList {
	private Map<Long, Boolean> userIds;

	/*
	@JsonIgnore
	public Map<IUser, Boolean> getUsers(final IDiscordClient client) {
		final Map<Long, Boolean> ids = getUserIds();
		if (ids==null)
			return null;
		final Map<IUser, Boolean> users = Maps.newHashMap();
		for (final Entry<Long, Boolean> entry : ids.entrySet()) {
			final Long userId = entry.getKey();
			final Boolean quit = entry.getValue();
			if (userId!=null) {
				final IUser user = client.getUserByID(userId);
				if (user!=null)
					users.put(user, quit);
			}
		}
		return users;
	}
	
	@JsonIgnore
	public void setUsers(final IGuild guild, final Map<IUser, Boolean> users) {
		if (this.userIds!=null)
			this.userIds.clear();
		else
			this.userIds = Maps.newHashMap();
		if (users!=null)
			for (final Entry<IUser, Boolean> entry : users.entrySet()) {
				final IUser user = entry.getKey();
				final Boolean quit = entry.getValue();
				this.userIds.put(user.getLongID(), quit);
			}
	}
	*/
}
