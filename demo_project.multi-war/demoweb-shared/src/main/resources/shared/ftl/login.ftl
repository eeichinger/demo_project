<#assign c=JspTaglibs["http://java.sun.com/jsp/jstl/core"] />
<#assign spring=JspTaglibs["http://www.springframework.org/security/tags"] />
<#assign spring=JspTaglibs["http://www.springframework.org/security/tags"] />

<h1>Hello from Freemarker!</h1>

<p>This is an image from the JAR (look for demoweb-shared.xml "<mvc:resources ... />"):</p>
<img src="<@c.url value="/shared/resources/img/loginPage.jpg"/>" />

<h1>Please Log In</h1>
<p>
  Please use the form below to log in.
</p>
<form action="j_spring_security_check" method="post">
  <label for="j_username">Login</label>:
  <input id="j_username" name="j_username" size="20" maxlength="50" type="text"/>
  <br />
  <label for="j_password">Password</label>:
  <input id="j_password" name="j_password" size="20" maxlength="50" type="password"/>
  <br />
  <input name="submit" type="submit" value="Login"/>
</form>
