<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <meta name="layout" content="main" />
  <title><g:message code="register.view.label"/></title>
</head>
<body>
  <h3 class="subnav center"><g:message code="register.header.label"/></h3>
  <g:form action="requestAccess" class="nice-form">
    <input type="hidden" name="targetUri" value="${targetUri}" />
    
    <table class="listing login">
      <tbody>
        <tr><td><label class="login-label"><g:message code="register.email.label"/></label></td></tr>
        <tr><td><input class="login-field" type="text" name="email" value="${email}" /></td></tr>
        <tr><td><label class="login-label"><g:message code="register.comment.label"/></label></td></tr>
        <tr><td><textarea class="login-field" type="text" name="comment" >${comment}</textarea></td></tr>
        <tr><td><ul><li><input type="submit" value="${message(code:'register.register.label')}" /></li><li><span class="login-label"><g:message code="register.info.text"/></span></li></td></tr>
      </tbody>
    </table>
  </g:form>
</body>
</html>
