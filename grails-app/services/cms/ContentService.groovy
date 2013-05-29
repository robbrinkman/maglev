package cms

import info.magnolia.cms.core.MgnlNodeType
import info.magnolia.context.MgnlContext
import info.magnolia.jcr.util.ContentMap
import info.magnolia.jcr.util.NodeUtil
import info.magnolia.jcr.util.PropertyUtil
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

import javax.jcr.Node

class ContentService {

    static transactional = false


    LinkGenerator grailsLinkGenerator

    ConfigService configService

    // TODO implement maxDepth, verify startLevel
    def getNavigation(ContentMap activeContent, int startLevel = 0, int maxDepth = 0) {

        List<Node> activeNodes = getActivePathNodes(activeContent)

        if (activeNodes.size() > (startLevel + 1)) {
            getActivatedNavigation(activeNodes[startLevel], activeNodes)
        } else {
            []
        }
    }

    def getActivePath(ContentMap activeContent) {
        getActivePathNodes(activeContent).collect { node ->
            [
                    path: node.getPath(),
                    url: buildUrl(node.getPath()),
                    title: PropertyUtil.getString(node, "title", ""),
                    active: true
            ]
        }
    }

    def getChildPages(String uuid, ContentMap activeContent) {
        def page = NodeUtil.getNodeByIdentifier("website", uuid)
        getActivatedNavigation(page, getActivePathNodes(activeContent))
    }

    def getActivePathNodes(ContentMap activeContent) {
        getActivePathNodes(activeContent.getJCRNode())
    }

    def getActivePathNodes(Node activeNode) {
        def activeNodes = []

        activeNodes << activeNode

        while (activeNode.depth > 0) {
            activeNode = activeNode.parent
            activeNodes << activeNode
        }
        return activeNodes.reverse()
    }

    private def getActivatedNavigation(Node fromNode, List<Node> activeNodes) {

        def navigation = []

        if (fromNode.hasNodes()) {
            for (Node node : NodeUtil.getNodes(fromNode, MgnlNodeType.NT_PAGE)) {
                if (!PropertyUtil.getBoolean(node, "hideInNavigation", false)) {
                    navigation << [
                            path: node.getPath(),
                            url: buildUrl(node.getPath()),
                            title: PropertyUtil.getString(node, "title", ""),
                            active: isActive(activeNodes, node),
                            items: getActivatedNavigation(node, activeNodes)
                    ]
                }
            }
        }
        navigation
    }

    def getActiveArea(content) {
        MgnlContext.getAttribute("area")
    }

    def logContent(content) {
        content.keySet().each {
            log.info "${it}: ${content.get(it)}"
        }
    }

    private def getRootNode() {
        (Node) MgnlContext.getJCRSession("website").getRootNode()
    }



    private def isActive(List<Node> activeNodes, Node node) {
        activeNodes.collect { it.path }.contains(node.path)
    }

    private def buildUrl(String path) {
        String ext = configService.getDefaultExtension()
        return "${grailsLinkGenerator.contextPath}${path}" + (ext.isEmpty() ? "" : "." + ext)
    }

}
