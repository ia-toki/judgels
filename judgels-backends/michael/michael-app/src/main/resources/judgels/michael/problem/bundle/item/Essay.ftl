<#import "/judgels/michael/template/form/compactHorizontalForms.ftl" as forms>

<#macro edit>
  <@forms.text form=form name="score" label="Score" required=true help="Points for correct answer" disabled=!canEdit/>
</#macro>
