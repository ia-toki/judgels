<#-- @ftlvariable type="judgels.michael.problem.bundle.item.ListItemsView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/forms.ftl" as forms>
<#import "/judgels/michael/ui.ftl" as ui>

<@template.layout>
  <#if canEdit>
    <@forms.form type="inline">
      <@forms.select name="type" label="Add new item" options=itemTypes/>
      <@forms.submit>Add</@forms.submit>
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
          <th>Pts</th>
          <th>Format</th>
          <th>Answer</th>
          <th class="col-fit"></th>
        </tr>
      </thead>
      <tbody>
        <#list items as item>
          <tr>
            <td class="col-fit">${item.number.isPresent()?then(item.number.get(), "")}</td>
            <td>${itemTypes[item.type]}</td>
            <td>${item.meta}</td>
            <td>
              <#if item.type != "STATEMENT">
                ${itemConfigs[item.jid].score}
                <#if item.type != "ESSAY" && itemConfigs[item.jid].penalty != 0>
                  (${itemConfigs[item.jid].penalty})
                </#if>
              </#if>
            </td>
            <td>
              <#if item.type == "MULTIPLE_CHOICE">
                <#list itemConfigs[item.jid].choices as choice>
                  ${choice.alias}
                </#list>
              <#elseif item.type == "SHORT_ANSWER">
                ${itemConfigs[item.jid].inputValidationRegex}
              </#if>
            </td>
            <td>
              <#if item.type == "MULTIPLE_CHOICE">
                <#list itemConfigs[item.jid].choices as choice>
                  <#if choice.isCorrect.present>
                    <#if choice.isCorrect.get()>
                      ${choice.alias}
                    </#if>
                  </#if>
                </#list>
              <#elseif item.type == "SHORT_ANSWER">
                <#if itemConfigs[item.jid].gradingRegex.present>
                  ${itemConfigs[item.jid].gradingRegex.get()}
                </#if>
              </#if>
            </td>
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
                  <span class="glyphicon glyphicon-trash"></span>
                </@ui.buttonLink>
              </#if>
            </td>
          </tr>
        </#list>
      </tbody>
    </@ui.table>
  </#if>
</@template.layout>
