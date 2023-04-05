<#import "/judgels/michael/template/form/compactHorizontalForms.ftl" as forms>

<#macro edit>
  <@forms.text form=form name="score" label="Score" required=true help="Points for correct answer" disabled=!canEdit/>
  <@forms.text form=form name="penalty" label="Penalty" required=tru help="Points for wrong answer" disabled=!canEdit/>
  <@forms.text form=form name="inputValidationRegex" label="Answer format regex" required=true help="Matches whole string. Example: [0-9]+,[0-9]+" disabled=!canEdit/>
  <@forms.text form=form name="gradingRegex" label="Correct answer regex" help="Optional. Matches whole string. Example: (1,2)|(2,1)" disabled=!canEdit/>
</#macro>
