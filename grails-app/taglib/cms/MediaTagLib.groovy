package cms

import info.magnolia.jcr.util.NodeUtil
import info.magnolia.jcr.util.PropertyUtil
import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

class MediaTagLib {

    static namespace = "media"

    MediaService mediaService

    def image = { attrs ->
        def image = mediaService.getImage(attrs.item, attrs.width, attrs.height, attrs.fit)
        out << "<img src=\"${image.src}\" width=\"${image.size.width}\" height=\"${image.size.height}\" title=\"${attrs.title ?: image.title}\"/ alt=\"${attrs.alt}\" style=\"${attrs.style}\" class=\"${attrs.class}\">"
    }
}
