<#import "/judgels/michael/forms.ftl" as forms>

<#macro view item config>
  <div class="clearfix">
    <div style="display: table-cell">
      ${item.number.get()}.&nbsp;
    </div>
    <div style="display: table-cell">
      <div class="content-text">
        ${config.statement?no_esc}
      </div>
    </div>
  </div>
  <hr />
  <input
    type="text"
    name="${item.jid}"
    <#if config.inputValidationRegex?has_content>
      pattern="${config.inputValidationRegex}"
      title="Answer format: ${config.inputValidationRegex}"
    </#if>
  >
</#macro>

<#macro edit>
  <@forms.input type="number" name="score" label="Score" required=true help="Points for correct answer" disabled=!canEdit/>
  <@forms.input type="number" name="penalty" label="Penalty" required=true help="Points for wrong answer" disabled=!canEdit/>
  <@forms.input name="inputValidationRegex" label="Answer format regex" required=true help="Matches whole string. Example: [0-9]+,[0-9]+" disabled=!canEdit/>
  <@forms.input name="gradingRegex" label="Correct answer regex" help="Optional. Matches whole string. Example: (1,2)|(2,1)" disabled=!canEdit/>
</#macro>
