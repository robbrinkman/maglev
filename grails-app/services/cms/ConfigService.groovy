package cms

import info.magnolia.context.MgnlContext
import info.magnolia.jcr.util.PropertyUtil

import javax.jcr.Session

class ConfigService {

    static transactional = false

    String getDefaultExtension() {
        Session session = MgnlContext.getJCRSession("config")
        javax.jcr.Node node = session.getRootNode().getNode("server")
        PropertyUtil.getString(node, "defaultExtension")
    }
}

