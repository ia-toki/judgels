<#-- @ftlvariable type="judgels.michael.lesson.ListLessonsView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/ui.ftl" as ui>

<@template.layout>
  <@ui.table>
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
            <@ui.buttonLink size="xs" to="/lessons/${lesson.id}/statements">Manage</@ui.buttonLink>
          </td>
        </tr>
      </#list>
    </tbody>
  </@ui.table>
  <@ui.pagination page=lessons termFilter=termFilter/>
</@template.layout>
