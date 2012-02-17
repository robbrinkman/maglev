package maglev.paragraphs

import info.magnolia.module.blossom.annotation.Paragraph
import info.magnolia.module.blossom.annotation.ParagraphDescription
import info.magnolia.module.blossom.annotation.TabFactory
import info.magnolia.module.blossom.dialog.TabBuilder

import grails.mgnl.Person
import maglev.BaseParagraph

@Paragraph("Persons")
@ParagraphDescription("List of Persons")
public class PersonsController extends BaseParagraph {

    def personService

    def index = {
        Map model = [:]

        //println "content testString: " + content?.getNodeData('testString').getString()
        //println "user: " + user
        //println "aggregationState: " + aggregationState

        def persons = Person.list()
        model.put("persons", persons);

        render(view: 'persons', model: model)
    }

    @TabFactory("Content")
    public void addDialog(TabBuilder tab) {
        tab.addEdit("testString", "Test String", "Test String");
    }

}