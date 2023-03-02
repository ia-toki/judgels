<#-- @ftlvariable type="judgels.michael.lesson.ViewLessonView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>

<@template.layout>
  <table class="table">
    <tbody>
      <tr>
        <td style="width: 200px">JID</td><td>${lesson.jid}</td>
      </tr>
      <tr>
        <td>Slug</td><td>${lesson.slug}</td>
      </tr>
      <tr>
        <td>Created by</td><td>${profile.username}</td>
      </tr>
      <tr>
        <td>Additional note</td>
        <td>
          <#noautoesc>
            ${lesson.additionalNote?esc?markup_string?replace("\r\n", "<br>")?replace("\n", "<br>")}
          </#noautoesc>
        </td>
      </tr>
    </tbody>
  </table>
</@template.layout>
