<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<jstl:if test="${_command != 'create'}">
		<acme:input-textbox code="manager.leg.form.label.flight" path="flight" readonly="true"/>
		<acme:input-textbox code="manager.leg.form.label.flightNumber" path="flightNumber" readonly="true"/>
	</jstl:if>
	<acme:input-textbox code="manager.leg.form.label.flightNumberDigits" path="flightNumberDigits"/>
	<acme:input-moment code="manager.leg.form.label.departure" path="departure"/>
	<acme:input-moment code="manager.leg.form.label.arrival" path="arrival"/>
	<jstl:if test="${_command != 'create'}">
		<acme:input-textbox code="manager.leg.form.label.duration" path="duration" readonly="true"/>
	</jstl:if>
	<acme:input-select code="manager.leg.form.label.status" path="status" choices="${statusChoices}"/>
	<acme:input-select code="manager.leg.form.label.aircraft" path="aircraft" choices="${aircrafts}"/>
	<acme:input-select code="manager.leg.form.label.departureAirport" path="departureAirport" choices="${departureAirports}"/>
	<acme:input-select code="manager.leg.form.label.arrivalAirport" path="arrivalAirport" choices="${arrivalAirports}"/>
	
	<acme:input-checkbox code="manager.leg.form.label.confirmation" path="confirmation"/>
	<jstl:choose>	 
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish')  && draftMode == true}">
			<acme:submit code="manager.leg.form.button.update" action="/manager/leg/update"/>
			<acme:submit code="manager.leg.form.button.delete" action="/manager/leg/delete"/>
			<acme:submit code="manager.leg.form.button.publish" action="/manager/leg/publish"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="manager.leg.form.button.create" action="/manager/leg/create?masterId=${masterId}"/>
		</jstl:when>
	</jstl:choose>
</acme:form>