<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib prefix="cms" uri="http://magnolia-cms.com/taglib/templating-cms.components/cms" %>
<%@ taglib prefix="cmsfn" uri="http://magnolia-cms.com/taglib/templating-cms.components/cmsfn" %>

<div class="row">
    <g:each in="${cms.components}" var="component">
        <cms:component content="${component}"/>
    </g:each>
</div>