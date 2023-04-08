<#-- @ftlvariable type="judgels.michael.resource.ListStatementLanguagesView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/ui/buttons.ftl" as buttons>
<#import "/judgels/michael/template/ui/tables.ftl" as tables>
<#import "/judgels/michael/template/form/inlineForms.ftl" as forms>

<@template.layout>
  <#if canEdit>
    <@forms.form>
      <@forms.select name="language" label="Add language" options=languages/>
      <@forms.submit>Add</@forms.submit>
    </@forms.form>
    <hr>
  </#if>
  <h3>Languages</h3>
  <@tables.table>
    <thead>
      <tr>
        <th>Language</th>
        <th>Status</th>
        <th class="col-fit"></th>
      </tr>
    </thead>

    <tbody>
      <#list availableLanguages as lang, status>
        <tr>
          <td>${languages[lang]}</td>
          <td>${status} <#if lang == defaultLanguage>(DEFAULT)</#if></td>
          <#if status == "ENABLED">
            <td class="col-fit">
              <#if canEdit && lang != defaultLanguage>
                <@buttons.link size="xs" to="languages/${lang}/disable">Disable</@buttons.link>
                <@buttons.link size="xs" to="languages/${lang}/makeDefault">Make default</@buttons.link>
              </#if>
            </td>
          <#else>
            <td class="col-fit">
              <#if canEdit>
                <@buttons.link size="xs" to="languages/${lang}/enable">Enable</@buttons.link>
              </#if>
            </td>
          </#if>
        </tr>
      </#list>
    </tbody>
  </@tables.table>
</@template.layout>
