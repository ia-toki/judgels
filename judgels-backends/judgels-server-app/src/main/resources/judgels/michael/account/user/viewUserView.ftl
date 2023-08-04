<#-- @ftlvariable type="judgels.michael.account.user.ViewUserView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>

<@template.layout>
  <h3>#${user.id}: ${user.username}</h3>
  <table class="table">
    <tbody>
      <tr>
        <td style="width: 200px">JID</td><td>${user.jid}</td>
      </tr>
      <tr>
        <td>Email</td><td>${user.email}</td>
      </tr>
      <tr>
        <td>Name</td><td>${info.name.isPresent()?then(info.name.get(), "")}</td>
      </tr>
      <tr>
        <td>Country</td><td>${info.country.isPresent()?then(info.country.get(), "")}</td>
      </tr>
    </tbody>
  </table>
</@template.layout>
