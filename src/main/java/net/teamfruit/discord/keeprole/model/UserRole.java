package net.teamfruit.discord.keeprole.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;

import lombok.Data;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UserRole {
	private List<Long> roleIds;

	@JsonIgnore
	public List<IRole> getRoles(final IGuild guild) {
		final List<Long> ids = getRoleIds();
		if (ids==null)
			return null;
		final List<IRole> roles = Lists.newArrayList();
		for (final Long id : ids)
			if (id!=null) {
				final IRole role = guild.getRoleByID(id);
				if (role!=null)
					roles.add(role);
			}
		return roles;
	}

	@JsonIgnore
	public void setRoles(final IGuild guild, final List<IRole> roles) {
		if (this.roleIds!=null)
			this.roleIds.clear();
		else
			this.roleIds = Lists.newArrayList();
		if (roles!=null)
			for (final IRole role : roles)
				this.roleIds.add(role.getLongID());
	}
}
