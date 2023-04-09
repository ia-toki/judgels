<#-- @ftlvariable type="judgels.michael.problem.bundle.item.ListItemsView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/form/horizontalForms.ftl" as forms>
<#import "/judgels/michael/ui.ftl" as ui>

<@template.layout>
  <#if canEdit>
    <@forms.form>
      <@forms.select form=form name="type" label="Add new item" options=itemTypes/>
      <@forms.submit small=true>Add</@forms.submit>
    </@forms.form>
    <hr>
  </#if>

  <h3>Items</h3>
  <#if items?size == 0>
    <p>No items.</p>
  <#else>
    <@ui.table>
      <thead>
        <tr>
          <th style="min-width: 50px">No.</th>
          <th>Type</th>
          <th>Internal note</th>
          <th class="col-fit"></th>
        </tr>
      </thead>
      <tbody>
        <#list items as item>
          <tr>
            <td class="col-fit">${item.number.isPresent()?then(item.number.get(), "")}</td>
            <td>${itemTypes[item.type]}</td>
            <td>${item.meta}</td>
            <td class="col-fit">
              <@ui.buttonLink size="xs" to="items/${item.jid}">Manage</@ui.buttonLink>
              <#if canEdit>
                <@ui.buttonLink intent="default" size="xs" to="items/${item.jid}/up" disabled=(item?index == 0)>
                  <span class="glyphicon glyphicon-arrow-up"></span>
                </@ui.buttonLink>
                <@ui.buttonLink intent="default" size="xs" to="items/${item.jid}/down" disabled=(item?index == items?size-1)>
                  <span class="glyphicon glyphicon-arrow-down"></span>
                </@ui.buttonLink>
                <@ui.buttonLink intent="danger" size="xs" to="items/${item.jid}/remove">
                  <span class="glyphicon glyphicon-remove"></span>
                </@ui.buttonLink>
              </#if>
            </td>
          </tr>
        </#list>
      </tbody>
    </@ui.table>
  </#if>
</@template.layout>
