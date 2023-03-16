<#-- @ftlvariable type="judgels.michael.resource.ListFilesView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/table/tableLayout.ftl" as table>
<#import "/judgels/michael/template/form/horizontalForms.ftl" as forms>

<@template.layout>
  <#if canEdit>
    <h3>Upload new</h3>
    <@forms.multipartForm>
      <@forms.file name="file" label="Upload single file"/>
      <@forms.submit>Upload</@forms.submit>
    </@forms.multipartForm>
    <@forms.multipartForm>
      <@forms.file name="fileZipped" label="Upload zipped file"/>
      <@forms.submit>Upload</@forms.submit>
    </@forms.multipartForm>
    <hr>
  </#if>
  <h3>Files</h3>
  <#if files?size == 0>
    <p>No files.</p>
  <#else>
    <@table.layout>
      <thead>
        <tr>
          <th>Filename</th>
          <th>Last modified at</th>
          <th>Size</th>
          <th class="table-col-actions"></th>
        </tr>
      </thead>

      <tbody>
        <#list files as file>
          <tr>
            <td>${file.name}</td>
            <td>${getFormattedDurationFromNow(file.lastModifiedTime)}</td>
            <td>${getFormattedFileSize(file.size)}</td>
            <td class="text-center"><a href="${currentPath}/${file.name}"><span class="glyphicon glyphicon-download" aria-hidden="true"></span></a></td>
          </tr>
        </#list>
      </tbody>
    </@table.layout>
  </#if>
</@template.layout>
