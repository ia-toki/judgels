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
        <td>Additional note</td><td>${problem.additionalNote}</td>
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
</@template.layout>
