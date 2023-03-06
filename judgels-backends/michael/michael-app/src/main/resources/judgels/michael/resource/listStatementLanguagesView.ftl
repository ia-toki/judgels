<#-- @ftlvariable type="judgels.michael.resource.ListStatementLanguagesView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/table/tableLayout.ftl" as table>
<#import "/judgels/michael/template/form/verticalForms.ftl" as forms>
<#import "ckeditorView.ftl" as ckeditor>
<#import "switchLanguageView.ftl" as switchLanguage>

<@template.layout>
  <h3>Add language</h3>

  <form class="form-inline" method="POST">
    <label for="language"><span class="small">Language</span></label>
    <div class="form-group">
      <select id="language" name="language">
        <#list languages as k, v>
          <option value="${k}">${v}</option>
        </#list>
      </select>
    </div>

    <button type="submit" class="btn btn-primary btn-xs">Add</button>
  </form>

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
