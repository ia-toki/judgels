<#-- @ftlvariable type="judgels.michael.resource.RebaseVersionLocalChangesView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/ui/buttons.ftl" as buttons>

<@template.layout>
  <h3>Rebase local changes</h3>
  <p>${localChangesError}</p>
  <div class="form-group">
    <@buttons.link to="local">OK</@buttons.link>
  </div>
</@template.layout>
