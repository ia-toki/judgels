<#-- @ftlvariable type="judgels.michael.lesson.EditLessonView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/forms.ftl" as forms>

<@template.layout>
  <h3>Info</h3>
  <@forms.form>
    <@forms.input name="slug" label="Slug" required=true pattern="[a-z0-9]+(-[a-z0-9]+)*" title="Slug can only consist of alphanumerics and dashes"/>
    <@forms.textarea name="additionalNote" label="Additional note"/>
    <@forms.submit>Update</@forms.submit>
  </@forms.form>
</@template.layout>
