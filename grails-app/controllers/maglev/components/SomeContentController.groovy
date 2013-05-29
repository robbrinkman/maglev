package maglev.components

import info.magnolia.module.blossom.annotation.Template
import info.magnolia.module.blossom.dialog.TabBuilder
import info.magnolia.module.blossom.annotation.TabFactory
import info.magnolia.module.blossom.annotation.TemplateDescription

@Template(id = "grailsModule:cms.components/someContent", title = "Some content")
@TemplateDescription("Most basic of cms.components. Just text")
class SomeContentController {

    def index() {
        render view: "someContent"
    }

    @TabFactory("Content")
    void content(TabBuilder builder){
        builder.addTextArea("text","Text","",20)
    }

}
