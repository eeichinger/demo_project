package org.oaky.service;


import org.oaky.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

public class ManagePermissionsFacadeImpl implements ManagePermissionsFacade {

	private final RoleRepository roleRepository;

	@Autowired
	public ManagePermissionsFacadeImpl(RoleRepository roleRepository) {
		Assert.notNull(roleRepository);
		this.roleRepository = roleRepository;
	}

	public void doSomethingSensitive() {
		Assert.notNull(roleRepository.getRole(3), "expected rolename");
	}
}
