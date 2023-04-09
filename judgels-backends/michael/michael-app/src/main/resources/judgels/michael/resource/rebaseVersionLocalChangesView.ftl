<#-- @ftlvariable type="judgels.michael.resource.RebaseVersionLocalChangesView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/ui.ftl" as ui>

<@template.layout>
  <h3>Rebase local changes</h3>
  <p>${localChangesError}</p>
  <div class="form-group">
    <@ui.buttonLink to="local">OK</@ui.buttonLink>
  </div>
</@template.layout>
