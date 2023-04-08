<#-- @ftlvariable type="judgels.michael.problem.bundle.item.ListItemsView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/form/horizontalForms.ftl" as forms>
<#import "/judgels/michael/template/table/tableLayout.ftl" as table>

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
    <@table.layout>
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
              <a type="button" class="btn btn-primary btn-xs" href="items/${item.jid}">Manage</a>
              <#if canEdit>
                <a type="button" class="btn btn-default btn-xs" href="items/${item.jid}/up" <#if item?index == 0>disabled</#if>>
                  <span class="glyphicon glyphicon-arrow-up"></span>
                </a>
                <a type="button" class="btn btn-default btn-xs" href="items/${item.jid}/down" <#if item?index == items?size-1>disabled</#if>>
                  <span class="glyphicon glyphicon-arrow-down"></span>
                </a>
                <a type="button" class="btn btn-danger btn-xs" href="items/${item.jid}/remove">
                  <span class="glyphicon glyphicon-remove"></span>
                </a>
              </#if>
            </td>
          </tr>
        </#list>
      </tbody>
    </@table.layout>
  </#if>
</@template.layout>
