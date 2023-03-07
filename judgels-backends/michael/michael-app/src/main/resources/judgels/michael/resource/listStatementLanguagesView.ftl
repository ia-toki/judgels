<#-- @ftlvariable type="judgels.michael.resource.ListStatementLanguagesView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/table/tableLayout.ftl" as table>
<#import "/judgels/michael/template/form/inlineForms.ftl" as forms>
<#import "ckeditorView.ftl" as ckeditor>
<#import "switchLanguageView.ftl" as switchLanguage>

<@template.layout>
  <h3>Add language</h3>
  <@forms.form>
    <@forms.select name="language" label="Language" options=languages/>
    <@forms.submit>Add</@forms.submit>
  </@forms.form>

  <h3>Available languages</h3>

  <@table.layout>
    <thead>
      <tr>
        <th>Language</th>
        <th>Status</th>
        <th></th>
      </tr>
    </thead>

    <tbody>
      <#list availableLanguages as lang, status>
        <tr>
          <td>${languages[lang]}</td>
          <td>${status} <#if lang == defaultLanguage>(DEFAULT)</#if></td>
          <#if status == "ENABLED">
            <td>
              <#if lang != defaultLanguage>
                <a type="button" class="btn btn-primary btn-xs">Disable</a>
                <a type="button" class="btn btn-primary btn-xs">Make default</a>
              </#if>
            </td>
          <#else>
            <td><a type="button" class="btn btn-primary btn-xs">Enable</a></td>
          </#if>
        </tr>
      </#list>
    </tbody>
  </@table.layout>
</@template.layout>
