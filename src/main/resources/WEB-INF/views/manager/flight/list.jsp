<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="manager.flight.label.tag" path="tag" width="30%"/>
	<acme:list-column code="manager.flight.label.requiresSelfTransfer" path="requiresSelfTransfer" width="20%"/>
	<acme:list-column code="manager.flight.label.cost" path="cost" width="50%"/>
	<acme:list-payload path="payload"/>
</acme:list>
