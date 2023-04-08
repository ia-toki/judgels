<#-- @ftlvariable type="judgels.michael.lesson.ListLessonsView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/ui/buttons.ftl" as buttons>
<#import "/judgels/michael/template/ui/tables.ftl" as tables>

<@template.layout>
  <@tables.table>
    <thead>
      <tr>
        <th style="min-width: 50px">ID</th>
        <th>Slug</th>
        <th>Created by</th>
        <th>Last updated at</th>
        <th class="col-fit"></th>
      </tr>
    </thead>
    <tbody>
      <#list lessons.page as lesson>
        <tr>
          <td class="col-fit">${lesson.id}</td>
          <td>${lesson.slug}</td>
          <td>${profilesMap[lesson.authorJid].username}</td>
          <td>${getFormattedDurationFromNow(lesson.lastUpdateTime)}</td>
          <td class="col-fit">
            <@buttons.link size="xs" to="/lessons/${lesson.id}/statements">Manage</@buttons.link>
          </td>
        </tr>
      </#list>
    </tbody>
  </@tables.table>
  <@tables.pagination page=lessons filterString=filterString/>
</@template.layout>
