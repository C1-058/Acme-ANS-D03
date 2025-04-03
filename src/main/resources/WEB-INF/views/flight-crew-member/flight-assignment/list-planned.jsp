<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="flight-crew-member.flight-assignment.list.label.duty" path="flightCrewDuty" width="30%"/>
	<acme:list-column code="flight-crew-member.flight-assignment.list.label.lastUpdate" path="lastUpdate" width="30%"/>
	<acme:list-column code="flight-crew-member.flight-assignment.list.label.status" path="assignmentStatus" width="40%"/>
	<acme:list-payload path="payload"/>
</acme:list>

<jstl:if test="${_command == 'list-planned'}">
	<acme:button code="flight-crew-member.flight-assignment.form.button.create" action="/flight-crew-member/flight-assignment/create"/>
</jstl:if>	