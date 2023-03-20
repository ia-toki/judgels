<#-- @ftlvariable type="judgels.michael.problem.programming.grading.EditGradingLanguageRestrictionView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/form/horizontalForms.ftl" as forms>

<@template.layout>
  <@forms.form>
    <div class="form-group">
      <label class="control-label col-md-3">Allowed languages</label>
      <div class="col-md-9">
        <div class="checkbox">
          <label>
            <input
              type="checkbox"
              id="isAllowedAll"
              name="isAllowedAll"
              value="true"
              <#if form.isAllowedAll>checked</#if>
              <#if !canEdit>disabled</#if>
            > Allow all
          </label>
        </div>

        <#list languages as lang, name>
          <div class="checkbox">
            <label>
              <input type="checkbox"
                class="allowedLanguage"
                name="allowedLanguages"
                value="${lang}"
                <#if form.allowedLanguages?seq_contains(lang)>checked</#if>
                <#if !canEdit>disabled</#if>
              > ${name}
            </label>
          </div>
        </#list>
      </div>
    </div>

    <#if canEdit>
      <@forms.submit>Update</@forms.submit>
      <script><#include "languageRestriction.js"></script>
    </#if>
  </@forms.form>
</@template.layout>
