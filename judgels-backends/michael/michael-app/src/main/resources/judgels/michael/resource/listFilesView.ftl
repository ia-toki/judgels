<#-- @ftlvariable type="judgels.michael.resource.ListFilesView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/ui/tables.ftl" as tables>
<#import "/judgels/michael/template/form/horizontalForms.ftl" as forms>

<@template.layout>
  <#if canEdit>
    <@forms.multipartForm>
      <@forms.file name="file" label="Upload new file"/>
      <@forms.submit small=true>Upload</@forms.submit>
    </@forms.multipartForm>
    <@forms.multipartForm>
      <@forms.file name="fileZipped" label="Upload new files as zip"/>
      <@forms.submit small=true>Upload</@forms.submit>
    </@forms.multipartForm>
    <hr>
  </#if>

  <h3>Files</h3>
  <#if files?size == 0>
    <p>No files.</p>
  <#else>
    <@tables.table>
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
            <td class="col-fit"><a href="${currentPath}/${file.name}"><span class="glyphicon glyphicon-download" aria-hidden="true"></span></a></td>
          </tr>
        </#list>
      </tbody>
    </@tables.table>
  </#if>
</@template.layout>
