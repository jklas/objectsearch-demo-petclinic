<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ include file="/WEB-INF/jsp/header.jsp" %>
<P>
<H2>Find Owners:</H2>
<spring:bind path="command">
  <FONT color="red">
    <B><c:out value="${status.errorMessage}"/></B>
  </FONT>
</spring:bind>

<FORM method="POST">
  <jsp:include page="/WEB-INF/jsp/fields/lastName.jsp"/>
  <INPUT type = "submit" value="Find Owners"  />
</FORM>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>