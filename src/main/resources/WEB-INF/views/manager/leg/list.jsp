<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="manager.leg.label.flightNumber" path="flightNumber" width="20%" sortable="false"/>
	<acme:list-column code="manager.leg.label.duration" path="duration" width="10%" sortable="false"/>
	<acme:list-column code="manager.leg.label.departure" path="departure" width="20%" sortable="true"/>
	<acme:list-column code="manager.leg.label.arrival" path="arrival" width="20%" sortable="true"/>
	<acme:list-column code="manager.leg.label.status" path="status" width="15%" sortable="false"/>
	<acme:list-column code="manager.leg.label.published" path="published" width="15%" sortable="false"/>
	<acme:list-payload path="payload"/>
</acme:list>

<jstl:if test="${showCreate}">
	<acme:button code="manager.leg.create" action="/manager/leg/create?masterId=${masterId}"/>
</jstl:if>