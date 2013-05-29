import info.magnolia.context.MgnlContext
import info.magnolia.jcr.util.ContentMap
import info.magnolia.jcr.wrapper.I18nNodeWrapper
import info.magnolia.module.blossom.render.RenderContext

/**
 * @author Åke Argéus
 */
class ContentFilters {

    def filters = {
        contentFilter(uri: '/**') {
            after = { model ->
                if (MgnlContext.isWebContext()) {
                    if (MgnlContext.aggregationState) {
                        if (model) {
                            model.put("state", MgnlContext.aggregationState)
                        }
                        if (MgnlContext.aggregationState.currentContent) {
                            if (model) {
                                model.put("content", new ContentMap(new I18nNodeWrapper(MgnlContext.aggregationState.currentContent.getJCRNode())))
                            }
                            if (RenderContext.get()) {
                                if (model) {
                                    model.putAll(RenderContext.get().contextObjects)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
