<#-- @ftlvariable type="judgels.michael.problem.base.ViewProblemView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>

<@template.layout>
  <h3>Info</h3>
  <table class="table">
    <tbody>
      <tr>
        <td style="width: 200px">JID</td><td>${problem.jid}</td>
      </tr>
      <tr>
        <td>Slug</td><td>${problem.slug}</td>
      </tr>
      <tr>
        <td>Created by</td><td>${profile.username}</td>
      </tr>
      <tr>
        <td>Additional note</td>
        <td>
          <#noautoesc>
            ${problem.additionalNote?esc?markup_string?replace("\r\n", "<br>")?replace("\n", "<br>")}
          </#noautoesc>
        </td>
      </tr>
    </tbody>
  </table>

  <h3>Metadata</h3>
  <table class="table">
   <tbody>
     <tr>
       <td style="width: 200px">Writers</td><td>${writerUsernames}</td>
     </tr>
     <tr>
       <td>Developers</td><td>${developerUsernames}</td>
     </tr>
     <tr>
       <td>Testers</td><td>${testerUsernames}</td>
     </tr>
     <tr>
       <td>Editorialists</td><td>${editorialistUsernames}</td>
     </tr>
   </tbody>
  </table>

  <h3>Tags</h3>
  <#if tags?size == 0>
    <p>(none)</p>
  <#else>
    <#list tags as tag>
      <#assign tagName=tag[("topic-"?length)..]>
      <div class="checkbox" <#if tagName?contains(": ")>style="margin-left: 20px"</#if>>
        <label>
          <input
            type="checkbox"
            class="problemTag"
            disabled
            checked
          >
          <#if tagName?contains(": ")>${tagName?split(": ")[1]}<#else>${tagName}</#if>
        </label>
      </div>
    </#list>
  </#if>
</@template.layout>
