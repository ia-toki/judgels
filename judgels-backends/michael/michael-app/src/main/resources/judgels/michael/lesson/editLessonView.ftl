<#-- @ftlvariable type="judgels.michael.lesson.EditLessonView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>

<#import "/judgels/michael/template/form/forms.ftl" as forms>

<@template.layout>
  <@forms.form>
    <@forms.text form=form name="slug" label="Slug" required=true pattern="[a-z0-9]+(-[a-z0-9]+)*" title="Slug can only consist of alphanumerics and dashes"/>
    <@forms.textarea form=form name="additionalNote" label="Additional note"/>

    <@forms.submit>Update</@forms.submit>
  </@forms.form>
</@template.layout>
