<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="customer.booking-record.passenger.list.label.fullName" path="passenger.fullName" width="50%"/>	
	<acme:list-column code="customer.booking-record.passenger.list.label.email" path="passenger.email" width="25%"/>
	<acme:list-column code="customer.booking-record.passenger.list.label.passportNumber" path="passenger.passportNumber" width="25%"/>
	<acme:list-payload path="payload"/>
</acme:list>

<jstl:if test="${showCreate}">
	<acme:button code="customer.booking-record.passenger.list.button.create" action="/customer/booking-record/create?masterId=${masterId}"/>
</jstl:if>