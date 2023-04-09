<#-- @ftlvariable type="judgels.michael.resource.ListStatementLanguagesView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/form/inlineForms.ftl" as forms>
<#import "/judgels/michael/ui.ftl" as ui>

<@template.layout>
  <#if canEdit>
    <@forms.form>
      <@forms.select name="language" label="Add language" options=languages/>
      <@forms.submit>Add</@forms.submit>
    </@forms.form>
    <hr>
  </#if>
  <h3>Languages</h3>
  <@ui.table>
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
                <@ui.buttonLink size="xs" to="languages/${lang}/disable">Disable</@ui.buttonLink>
                <@ui.buttonLink size="xs" to="languages/${lang}/makeDefault">Make default</@ui.buttonLink>
              </#if>
            </td>
          <#else>
            <td class="col-fit">
              <#if canEdit>
                <@ui.buttonLink size="xs" to="languages/${lang}/enable">Enable</@ui.buttonLink>
              </#if>
            </td>
          </#if>
        </tr>
      </#list>
    </tbody>
  </@ui.table>
</@template.layout>
