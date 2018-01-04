package net.teamfruit.discord.keeprole;

import java.io.File;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

@RequiredArgsConstructor
public class UserRoleManager {
	private final File roleDir;

	public File getGuildDir(@NonNull final IGuild guild) {
		return new File(this.roleDir, guild.getStringID());
	}

	public File getGuildMemberListFile(@NonNull final IGuild guild) {
		return new File(getGuildDir(guild), "members.json");
	}

	public File getUserDir(@NonNull final IGuild guild, @NonNull final IUser user) {
		return new File(getGuildDir(guild), user.getStringID());
	}

	public File getUserRoleFile(@NonNull final IGuild guild, @NonNull final IUser user) {
		return new File(getUserDir(guild, user), "roles.json");
	}
}
