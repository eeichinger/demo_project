package org.oaky.service;

import org.springframework.security.access.prepost.PreAuthorize;

public interface ForbiddenForGuestsFacade {
	@PreAuthorize("hasRole('ROLE_USER')")
	void doSomethingSensitive();
}
