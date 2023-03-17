<#-- @ftlvariable type="judgels.michael.resource.RebaseVersionLocalChangesView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>

<@template.layout>
  <h3>Rebase local changes</h3>
  <p>${localChangesError}</p>
  <div class="form-group">
    <a type="button" href="${backUrl}" class="btn btn-primary">OK</a>
  </div>
</@template.layout>
