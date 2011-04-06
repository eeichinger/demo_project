Feature: Prevent Fraud
	In order to prevent malicious users from abusing the system
	as a responsible sysadmin
	i want to abort any unauthorized activity

Scenario: Anonymous user
	Given a anonymous user
	When a facade function is used
	Then the system must throw a security exception

Scenario: Guest user
	Given a guest user
	When a facade function is used
	Then the system must throw a security exception

Scenario: Autorized user
	Given a authorized user
	When a facade function is used
	Then the system will proceed normal