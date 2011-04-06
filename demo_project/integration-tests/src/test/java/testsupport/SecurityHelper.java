package testsupport;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collection;

public class SecurityHelper {

	@SuppressWarnings("unchecked")
	public static void authenticateSecurityContext(String username, String password, String... roles) {
		Collection<GrantedAuthority> grantedAuthorities =
				CollectionUtils.collect(Arrays.asList(roles), new Transformer() {
					public Object transform(Object input) {
						return new GrantedAuthorityImpl((String) input);
					}
				});

		SecurityContextHolder.getContext()
			.setAuthentication(new UsernamePasswordAuthenticationToken(username, password, grantedAuthorities));
	}
}
