<#-- @ftlvariable type="judgels.michael.problem.programming.grading.EditGradingLanguageRestrictionView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/forms.ftl" as forms>

<@template.layout>
  <@forms.form>
    <@forms.formGroup>
      <@forms.formLabel value="Allowed languages"/>
      <@forms.formField>
        <div class="checkbox">
          <label>
            <input
              type="checkbox"
              id="isAllowedAll"
              name="isAllowedAll"
              value="true"
              <#if formValues.isAllowedAll>checked</#if>
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
                <#if formValues.allowedLanguages?seq_contains(lang)>checked</#if>
                <#if !canEdit>disabled</#if>
              > ${name}
            </label>
          </div>
        </#list>
      </@forms.formField>
    </@forms.formGroup>

    <#if canEdit>
      <@forms.submit>Update</@forms.submit>
      <script><#include "languageRestriction.js"></script>
    </#if>
  </@forms.form>
</@template.layout>
