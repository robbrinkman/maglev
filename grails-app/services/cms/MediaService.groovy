package cms

import info.magnolia.cms.beans.runtime.FileProperties
import info.magnolia.cms.core.Content
import info.magnolia.cms.core.HierarchyManager
import info.magnolia.cms.core.NodeData
import info.magnolia.cms.core.version.MgnlVersioningNodeWrapper
import info.magnolia.context.MgnlContext
import info.magnolia.jcr.util.NodeUtil
import info.magnolia.jcr.util.PropertyUtil
import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager
import net.sourceforge.openutils.mgnlmedia.media.lifecycle.MediaModule
import net.sourceforge.openutils.mgnlmedia.media.utils.ImageUtils
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

// TODO support original images

class MediaService {

    static transactional = false

    static MEDIA_OBJECT_PREFIX = "mediaObject"

    static RESOLUTION_FOLDER_PREFIX = "resolutions"

    static RESOLUTION_PATH_PREFIX = "res-"


    static DEFAULT_IMAGE_RESOLUTION = '200x200'

    /*
    Resolution Processors

    From Config: modules > media > processors > image-resolution

    n : net.sourceforge.openutils.mgnlmedia.media.processors.ResizeNoCropImageResolutionProcessor
    l : net.sourceforge.openutils.mgnlmedia.media.processors.FitInImageResolutionProcessor
    p : net.sourceforge.openutils.mgnlmedia.media.crop.PzcImageProcessor
    o : net.sourceforge.openutils.mgnlmedia.media.processors.FitInAndFillWithBandsImageResolutionProcessor
    c : net.sourceforge.openutils.mgnlmedia.media.processors.ResizeCropCenteredImageResolutionProcessor
     */

    static IMAGE_RESOLUTION_FIT_STRATEGIES = [resize: 'r', fit: 'l', pzc: 'p', crop: 'c', fill: 'o']

    static DEFAULT_IMAGE_RESOLUTION_FIT_STRATEGY = 'crop'

    /*
    Image Post Processor

    From Config: modules > media > processors > image-post

    bw : net.sourceforge.openutils.mgnlmedia.media.processors.BlackAndWhitePostProcessor
    rc : net.sourceforge.openutils.mgnlmedia.media.processors.RoundedCornersProcessor
    logsize : net.sourceforge.openutils.mgnlmedia.media.processors.LogSizePostProcessor
     */

    static DEFAULT_IMAGE_TYPE = 'jpg'

    LinkGenerator grailsLinkGenerator

    private static MediaConfigurationManager mcm = MediaConfigurationManager.getInstance();


    def getImage(uuid, width, height, fit_strategy) {
        def resolution = "${width}x${height}"

        MgnlVersioningNodeWrapper node = NodeUtil.getNodeByIdentifier("media", uuid)
        HierarchyManager hm = MgnlContext.getHierarchyManager(MediaModule.REPO);
        Content media = hm.getContentByUUID(uuid);


        resolution = resolution ?: DEFAULT_IMAGE_RESOLUTION

        fit_strategy = fit_strategy ?: DEFAULT_IMAGE_RESOLUTION_FIT_STRATEGY

        if (!IMAGE_RESOLUTION_FIT_STRATEGIES.containsKey(fit_strategy)) {
            throw new IllegalArgumentException("Invalid fit strategy: ${fit_strategy}, should one of ${IMAGE_RESOLUTION_FIT_STRATEGIES.keySet()}")
        }

        def resolutionParam = "${IMAGE_RESOLUTION_FIT_STRATEGIES[fit_strategy]}${resolution}"




        if (ImageUtils.checkOrCreateResolution(media, resolutionParam, null, false)) {
            def path = node.getPath()
            def name = PropertyUtil.getString(node, "media_name")



            def title = media.getTitle()

            def details = getImageDetails(media, resolutionParam)

            def src = getImageSrc(name, details.extension, path, resolutionParam)

            [src: src, name: name, extension: details.extension, title: title, size: details.size]
        } else {
            throw IllegalArgumentException("Could not check or create image for uuid: ${uuid} and resolution: ${resolution}")
        }
    }

    private getImageSrc(name, extension, path, resolution) {
        "${grailsLinkGenerator.contextPath}/${MEDIA_OBJECT_PREFIX}${path}/${RESOLUTION_FOLDER_PREFIX}/${RESOLUTION_PATH_PREFIX}${resolution}/${name}.${extension}"
    }

    private getImageDetails(media, resolution) {
        NodeData res

        if (resolution == "original") {
            res = media.getNodeData("original")
        } else {
            def resolutions = media.getContent("resolutions");
            if (resolutions) {
                if (resolutions.hasNodeData(ImageUtils.getResolutionPath(RESOLUTION_PATH_PREFIX + resolution))) {
                    res = resolutions.getNodeData(ImageUtils.getResolutionPath(RESOLUTION_PATH_PREFIX + resolution));
                }
            }
        }
        if (!res) {
            throw new IllegalArgumentException("No valid resolution: ${resolution} for media: ${media}")
        }

        [size: [width: res.getAttribute(FileProperties.PROPERTY_WIDTH), height: res.getAttribute(FileProperties.PROPERTY_HEIGHT)], extension: res.getAttribute(FileProperties.PROPERTY_EXTENSION)]

    }
}