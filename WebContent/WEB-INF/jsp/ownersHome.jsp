<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ include file="/WEB-INF/jsp/header.jsp" %>
<P>
<H2>Owners</H2>

<div class="actionBox">
	<A href="<c:url value="/addOwner.htm"/>">Add Owner</A>
</div>
<div class="actionBox">
	<A href="<c:url value="/ownerFind.htm"/>">Find Owner (using Hibernate)</A>
</div>
<br/>

<%@ include file="/WEB-INF/jsp/footer.jsp" %>