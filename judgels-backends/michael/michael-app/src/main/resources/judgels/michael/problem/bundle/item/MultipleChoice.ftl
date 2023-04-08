<#import "/judgels/michael/template/form/horizontalForms.ftl" as forms>
<#import "/judgels/michael/template/ui/buttons.ftl" as buttons>

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
  <@buttons.button intent="default" size="xs" style="margin-top: 10px" onclick="clearChoices(this)">
    Clear answer
  </@buttons.button>
</#macro>

<#macro edit>
  <@forms.text form=form compact=true number=true name="score" label="Score" required=true help="Points for correct answer" disabled=!canEdit/>
  <@forms.text form=form compact=true number=true name="penalty" label="Penalty" required=true help="Points for wrong answer" disabled=!canEdit/>

  <div class="row">
    <div class="col-md-2">
      <label class="control-label">Choices</label>
    </div>

    <div class="col-md-10">
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
                  <td class="text-center">
                    <a href="#" class="choice-remove-button"><span class="glyphicon glyphicon-remove" style="margin-top: 8px"></span></a>
                  </td>
                </#if>
              </tr>
              <#list 0..<form.choiceAliases?size as i>
                <tr class="choice">
                  <td>
                    <input type="text" name="choiceAliases" class="form-control input-sm" value="${form.choiceAliases[i]}" required <#if !canEdit>disabled</#if>>
                  </td>
                  <td>
                    <input type="text" name="choiceContents" class="form-control input-sm" value="${form.choiceContents[i]}" <#if !canEdit>disabled</#if>>
                  </td>
                  <td class="text-center">
                    <input type="checkbox" name="choiceIsCorrects" style="margin-top: 8px" value="${i}" <#if form.choiceIsCorrects?seq_contains(i)> checked</#if> <#if !canEdit>disabled</#if>>
                  </td>
                  <#if canEdit>
                    <td class="text-center">
                      <a href="#" class="choice-remove-button"><span class="glyphicon glyphicon-remove" style="margin-top: 8px"></span></a>
                    </td>
                  </#if>
                <tr>
              </#list>
              <#if canEdit>
                <tr class="active choice-add-form">
                  <td class="text-center">
                    <@buttons.button size="xs" class="choice-add-button" class="choice-add-button">
                      <span class="glyphicon glyphicon-plus"></span> New choice
                    </@buttons.button>
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
    </div>
  </div>

  <#if canEdit>
    <script>
      <#include "MultipleChoice.js">
    </script>
  </#if>
</#macro>
