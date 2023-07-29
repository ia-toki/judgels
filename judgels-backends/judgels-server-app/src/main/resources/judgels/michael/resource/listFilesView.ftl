<#-- @ftlvariable type="judgels.michael.resource.ListFilesView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/forms.ftl" as forms>
<#import "/judgels/michael/ui.ftl" as ui>

<@template.layout>
  <#if canEdit>
    <@forms.form multipart=true>
      <@forms.file name="file" label="Upload new file"/>
      <@forms.submit size="sm">Upload</@forms.submit>
    </@forms.form>
    <@forms.form multipart=true>
      <@forms.file name="fileZipped" label="Upload new files as zip"/>
      <@forms.submit size="sm">Upload</@forms.submit>
    </@forms.form>
    <hr>
  </#if>

  <#if files?size == 0>
    <p>No files.</p>
  <#else>
    <@ui.table>
      <thead>
        <tr>
          <th>Filename</th>
          <th>Last modified at</th>
          <th>Size</th>
          <th class="col-fit"></th>
        </tr>
      </thead>

      <tbody>
        <#list files as file>
          <tr>
            <td>${file.name}</td>
            <td>${getFormattedDurationFromNow(file.lastModifiedTime)}</td>
            <td>${getFormattedFileSize(file.size)}</td>
            <td class="col-fit">
              <@ui.buttonLink size="xs" to="${currentPath}/${file.name}">
                <span class="glyphicon glyphicon-download"></span>
              </@ui.buttonLink>
            </td>
          </tr>
        </#list>
      </tbody>
    </@ui.table>
  </#if>
</@template.layout>
