<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<h2>Search Results</h2>

<hr width="100%" />

<p>Showing results <c:out value="${searchResults['results.start']}" />-<c:out
	value="${searchResults['results.end']}" /> (<c:out
	value="${searchResults['search.time']}" /> s.)</p>

<table width="100%">
	<tbody>
		<c:forEach var="rowData" items="${searchResults['results']}">
			<tr>
					<c:choose>
							<c:when	test="${rowData.result.class.name == \"com.jklas.sample.petclinic.Owner\"}">
								<td class="searchResultsOwnerFirstColumn">
								</td>
								<td style="border: thin;">
									<a href="<c:url value="owner.htm?ownerId=${rowData.result.id}"/>">(Owner)
										<strong>
											<c:out value="${rowData.result.firstName}" />&nbsp;<c:out value="${rowData.result.lastName}" />
										</strong>
									</a>
								</td>
							</c:when>
							<c:when	test="${rowData.result.class.name == \"com.jklas.sample.petclinic.Pet\"}">
								<td class="searchResultsPetFirstColumn"></td>
								<td>
									<a href="<c:url value="editPet.htm?petId=${rowData.result.id}"/>">
										(<c:out value="${rowData.result.type.name}" />)&nbsp;
										<strong>
											<c:out value="${rowData.result.name}" />
										</strong>
									</a>
									&gt;
									<strong>
										<a href="<c:url value="owner.htm?ownerId=${rowData.result.id}"/>">
											<strong>
												<c:out value="${rowData.result.owner.firstName}" />&nbsp;<c:out value="${rowData.result.owner.lastName}" />
											</strong>
										</a>
									</strong>
								</td>
							</c:when>
							<c:when	test="${rowData.result.class.name == \"com.jklas.sample.petclinic.Visit\"}">
								<td class="searchResultsVisitFirstColumn">																	
								</td>
								<td>
									<a href="<c:url value="owner.htm?ownerId=${rowData.result.pet.owner.id}"/>">									
										Visit #<c:out value="${rowData.result.id}" />
									</a>
									&gt;
									<a href="<c:url value="editPet.htm?petId=${rowData.result.id}"/>">
										(<c:out value="${rowData.result.pet.type.name}" />)&nbsp;
										<strong>
											<c:out value="${rowData.result.pet.name}" />
										</strong>
									</a>
									&gt;
									<strong>
										<a href="<c:url value="owner.htm?ownerId=${rowData.result.id}"/>">
											<strong>
												<c:out value="${rowData.result.pet.owner.firstName}" />&nbsp;<c:out value="${rowData.result.pet.owner.lastName}" />
											</strong>
										</a>
									</strong>
								</td>
							</c:when>
							<c:when	test="${rowData.result.class.name == \"com.jklas.sample.petclinic.PetType\"}">
								<td class="searchResultsPetTypeFirstColumn">								
								</td><td>
								(Pet Type) <c:out value="${rowData.result.name}" />
								</td>
							</c:when>
							<c:otherwise>
								<td class="searchResultsUnknownFirstColumn">
								</td><td>
								Result of type: <c:out value="${rowData.result.class.name}" /> 
								<br/> 
								ID: <c:out value="${rowData.result.id}" />
								<br/>
								(Sorry, I don't know how to display this kind of results..)
								</td>
							</c:otherwise>
					</c:choose>
					<td style="font-style: italic; font-size:small; font-family: sans-serif; text-align: right;">
						(<fmt:formatNumber type="number" maxFractionDigits="4"
            				groupingUsed="false" value="${rowData.score}" />)
					</td>				
			</tr>
		</c:forEach>
	</tbody>
</table>



<BR>
<%@ include file="/WEB-INF/jsp/footer.jsp"%>