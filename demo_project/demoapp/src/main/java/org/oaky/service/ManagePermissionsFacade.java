package org.oaky.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ManagePermissionsFacade {
	@PreAuthorize("hasRole('ROLE_USER')")
	void doSomethingSensitive();
}
