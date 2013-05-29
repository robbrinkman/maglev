package maglev

import info.magnolia.cms.core.MgnlNodeType
import info.magnolia.cms.util.ContentUtil
import info.magnolia.jcr.util.NodeUtil
import info.magnolia.jcr.util.PropertyUtil
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

import javax.jcr.Node

class SiteService {

    LinkGenerator grailsLinkGenerator

    //TODO read from magnolia configuration
    private final static MAGNOLIA_EXTENSION = ".html"

    def getMainNavigation(currentContent) {
        getNavigation(ContentUtil.getContent("website", "/ckv").getJCRNode(), currentContent.getJCRNode())
    }

    def getSubNavigation(currentContent) {
        //TODO implement more iteligent, should be based on level
        getNavigation(currentContent.getJCRNode(), currentContent.getJCRNode())
    }

    private def getNavigation(fromNode, currentNode) {
        def navigation = []

        for (Node node : NodeUtil.getNodes(fromNode, MgnlNodeType.NT_PAGE)) {
            if (!PropertyUtil.getBoolean(node, "hideInNavigation", false)) {
                navigation << [ path: node.getPath(), url: buildUrl(node.getPath()), title: PropertyUtil.getString(node, "title", ""), active: (node.path == currentNode.path) ]
            }
        }
        navigation
    }

    def buildUrl(String path) {
        "${grailsLinkGenerator.contextPath}${path}${MAGNOLIA_EXTENSION}"
    }
}
