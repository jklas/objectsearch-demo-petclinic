<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<h2>Search Stats</h2>

<table>

	<thead>
		<tr>
			<th scope="col">Index Name</th>
			<th scope="col">Object Count</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="index" items="${indexStats}">
			<tr>
				<td style="border: thin;"><c:out value="${index.key}" /></td>
				<td><c:out value="${index.value}" /></td>
			</tr>
		</c:forEach>
	</tbody>
</table>


<BR>
<%@ include file="/WEB-INF/jsp/footer.jsp"%>