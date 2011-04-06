package org.oaky;

import org.springframework.security.authentication.jaas.AuthorityGranter;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class GroupNameAuthorityGranter implements AuthorityGranter {

    private Logger log = Logger.getLogger(this.getClass().getName());

    public Set<String> grant(Principal principal) {
        HashSet<String> grantedAuthorities = new HashSet<String>();

        if (principal instanceof Group) {
            Group group = (Group) principal;
            Enumeration<? extends Principal> members = group.members();
            while(members.hasMoreElements()) {
                String principalName = members.nextElement().getName();
                log.info("adding authority for group " + principalName + "[" + principal.getClass().toString() + "]");
                grantedAuthorities.add(principalName);
            }
            return grantedAuthorities;
        }

        log.info("not granting any authorities for unknown principal type " + principal.getName() + "[" + principal.getClass().toString() + "]");
        return null;
    }
}
