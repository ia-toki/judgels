<#-- @ftlvariable type="judgels.michael.template.lesson.ListLesonsView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/table/tableLayout.ftl" as table>
<#import "/judgels/michael/template/table/paginationView.ftl" as pagination>

<@template.layout>
  <@table.layout>
    <thead>
      <tr>
        <th class="table-col-id">ID</th>
        <th>Slug</th>
        <th>Created by</th>
        <th>Last updated at</th>
        <th class="table-col-actions"></th>
      </tr>
    </thead>
    <tbody>
      <#list lessons.page as lesson>
        <tr>
          <td>${lesson.id}</td>
          <td>${lesson.slug}</td>
          <td>${profilesMap[lesson.authorJid].username}</td>
          <td>${getDurationFromNow(lesson.lastUpdateTime)}</td>
          <td class="text-center">
            <a type="button" class="btn btn-primary btn-xs" href="/lessons/${lesson.id}/statements">Manage</a>
          </td>
        </tr>
      </#list>
    </tbody>
  </@table.layout>
  <@pagination.view page=lessons filterString=filterString/>
</@template.layout>
