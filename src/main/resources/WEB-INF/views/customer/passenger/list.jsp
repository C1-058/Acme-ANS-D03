<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="customer.passenger.list.label.fullName" path="fullName" width="40%"/>	
	<acme:list-column code="customer.passenger.list.label.email" path="email" width="25%"/>
	<acme:list-column code="customer.passenger.list.label.passportNumber" path="passportNumber" width="25%"/>
	<acme:list-column code="customer.passenger.list.label.draftMode" path="draftMode" width="10%"/>
	<acme:list-payload path="payload"/>
</acme:list>

<jstl:if test="${showCreate}">
	<acme:button code="customer.passenger.list.button.create" action="/customer/passenger/create?masterId=${masterId}"/>
</jstl:if>