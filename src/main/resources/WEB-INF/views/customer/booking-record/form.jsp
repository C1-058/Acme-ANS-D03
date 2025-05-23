<%--
- form.jsp
-
- Copyright (C) 2025 Rafael David Caro Medina
-
- In keeping with the traditional purpose of furthering education and research, it is
- the policy of the copyright owner to permit non-commercial use and redistribution of
- this software. It has been tested carefully, but it is not guaranteed for any particular
- purposes.  The copyright owner does not offer any warranties or representations, nor do
- they accept any liabilities with respect to them.
--%>

<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="customer.booking-record.passenger.form.label.fullName" path="booking.customer.identity.fullName" readonly="true"/>
	<acme:input-textbox code="customer.booking-record.form.label.locatorCode" path="booking.locatorCode" readonly="true"/>
	<acme:input-select code="customer.booking.form.label.travelClass" path="booking.travelClass" choices="${travelClasses}" readonly="true"/>
	<acme:input-money code="customer.booking.form.label.price" path="booking.price" readonly="true"/>
	<acme:input-select code="customer.booking-record.form.label.passenger" path="passenger" choices="${passengers}"
		readonly="${_command != 'create'}" />

	<jstl:choose>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="customer.booking-record.form.button.create" action="/customer/booking-record/create?masterId=${masterId}" />
		</jstl:when>
	</jstl:choose>
</acme:form>