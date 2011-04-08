package org.oaky.service;


import entities.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

public class ForbiddenForGuestsFacadeImpl implements ForbiddenForGuestsFacade {

	private final RoleRepository roleRepository;

	@Autowired
	public ForbiddenForGuestsFacadeImpl(RoleRepository roleRepository) {
		Assert.notNull(roleRepository);
		this.roleRepository = roleRepository;
	}

	public void doSomethingSensitive() {
		Assert.notNull(roleRepository.getRole(3), "expected rolename");
	}
}
