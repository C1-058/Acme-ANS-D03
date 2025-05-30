<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="customer.booking.form.label.customer.identity.fullName" path="customer.identity.fullName" readonly="true"/>
	
	<acme:input-textbox code="customer.booking.form.label.locatorCode" path="locatorCode"/>
	
	<acme:input-select path="flight" code="customer.booking.form.label.flight" choices="${flights}" />
	
	<acme:input-select path="travelClass" code="customer.booking.form.label.travelClass" choices="${travelClasses}" />
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish')}">
			<acme:input-moment code="customer.booking.form.label.purchaseMoment" path="purchaseMoment" readonly="true"/>
		</jstl:when>
	</jstl:choose>
	
	<acme:input-money code="customer.booking.form.label.price" path="price"/>
	<acme:input-textbox code="customer.booking.form.label.lastNibble" path="lastNibble"/>
		
	<jstl:choose>	 
		<jstl:when test="${_command == 'show' && draftMode == false}">
			<acme:button code="customer.booking.form.button.passengers" action="/customer/booking-record/list?masterId=${id}"/>			
		</jstl:when>
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish') && draftMode == true}">
			<acme:button code="customer.booking.form.button.passengers" action="/customer/booking-record/list?masterId=${id}"/>
			<acme:submit code="customer.booking.form.button.update" action="/customer/booking/update"/>			
			<acme:submit code="customer.booking.form.button.publish" action="/customer/booking/publish"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="customer.booking.form.button.create" action="/customer/booking/create"/>
		</jstl:when>		
	</jstl:choose>
</acme:form>