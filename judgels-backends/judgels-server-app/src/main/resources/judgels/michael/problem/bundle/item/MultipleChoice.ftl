<#import "/judgels/michael/forms.ftl" as forms>
<#import "/judgels/michael/ui.ftl" as ui>

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
  <#list config.choices as choice>
    <div class="radio" style="margin-top: -5px">
      <label>
        <div class="pull-left">
          <input type="radio" name="${item.jid}" value="${choice.alias}">
        </div>
        <div class="pull-left" style="margin-right: 5px;">
          <span class="badge">${choice.alias}</span>
        </div>
        <div class="pull-left content-text">
          ${choice.content?no_esc}
        </div>
       </label>
    </div>
  </#list>
  <@ui.button intent="default" size="xs" style="margin-top: 10px" onclick="clearChoices(this)">
    Clear answer
  </@ui.button>
</#macro>

<#macro edit>
  <@forms.input type="number" name="score" label="Score" required=true help="Points for correct answer" disabled=!canEdit/>
  <@forms.input type="number" name="penalty" label="Penalty" required=true help="Points for wrong answer" disabled=!canEdit/>

  <@forms.formGroup>
    <@forms.formLabel value="Choices"/>
    <@forms.formField>
      <div class="panel panel-default">
        <div class="panel-body">
          <table class="table table-condensed">
            <thead>
              <tr>
                <th style="width: 95px">Alias</th>
                <th>Content</th>
                <th style="width: 65px">Correct?</th>
                <#if canEdit><th></th></#if>
              </tr>
            </thead>
            <tbody id="choices">
              <tr class="choice choice-template hidden">
                <td>
                  <input type="text" name="choiceAliases" class="form-control input-sm" required disabled>
                </td>
                <td>
                  <input type="text" name="choiceContents" class="form-control input-sm" disabled>
                </td>
                <td class="text-center">
                  <input type="checkbox" name="choiceIsCorrects" style="margin-top: 8px" disabled>
                </td>
                <#if canEdit>
                  <td class="col-fit">
                    <@ui.button intent="danger" size="xs" class="choice-remove-button" style="margin-top: 4px">
                      <span class="glyphicon glyphicon-trash"></span>
                    </@ui.button>
                  </td>
                </#if>
              </tr>
              <#list 0..<formValues.choiceAliases?size as i>
                <tr class="choice">
                  <td>
                    <input type="text" name="choiceAliases" class="form-control input-sm" value="${formValues.choiceAliases[i]}" required <#if !canEdit>disabled</#if>>
                  </td>
                  <td>
                    <input type="text" name="choiceContents" class="form-control input-sm" value="${formValues.choiceContents[i]}" <#if !canEdit>disabled</#if>>
                  </td>
                  <td class="text-center">
                    <input type="checkbox" name="choiceIsCorrects" style="margin-top: 8px" value="${i}" <#if formValues.choiceIsCorrects?seq_contains(i)> checked</#if> <#if !canEdit>disabled</#if>>
                  </td>
                  <#if canEdit>
                    <td class="col-fit">
                      <@ui.button intent="danger" size="xs" class="choice-remove-button" style="margin-top: 4px">
                        <span class="glyphicon glyphicon-trash"></span>
                      </@ui.button>
                    </td>
                  </#if>
                <tr>
              </#list>
              <#if canEdit>
                <tr class="active choice-add-form">
                  <td class="text-center">
                    <@ui.button size="xs" class="choice-add-button" class="choice-add-button">
                      <span class="glyphicon glyphicon-plus"></span> New choice
                    </@ui.button>
                  </td>
                  <td></td>
                  <td></td>
                  <td></td>
                </tr>
              </#if>
            </tbody>
          </table>
        </div>
      </div>
    </@forms.formField>
  </@forms.formGroup>

  <#if canEdit>
    <script>
      <#include "MultipleChoice.js">
    </script>
  </#if>
</#macro>
