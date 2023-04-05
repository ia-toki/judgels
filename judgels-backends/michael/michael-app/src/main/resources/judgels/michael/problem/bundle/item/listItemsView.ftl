<#-- @ftlvariable type="judgels.michael.problem.bundle.item.ListItemsView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/template/form/horizontalForms.ftl" as forms>
<#import "/judgels/michael/template/table/tableLayout.ftl" as table>

<@template.layout>
  <#if canEdit>
    <h3>Add new item</h3>
    <@forms.form>
      <@forms.select form=form name="type" label="Type" options=itemTypes/>
      <@forms.submit>Add</@forms.submit>
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
          <th class="table-col-id">No.</th>
          <th>Type</th>
          <th>Internal note</th>
          <th class="table-col-item-actions"></th>
        </tr>
      </thead>
      <tbody>
        <#list items as item>
          <tr>
            <td>${item.number.isPresent()?then(item.number.get(), "")}</td>
            <td>${itemTypes[item.type]}</td>
            <td>${item.meta}</td>
            <td>
              <a type="button" class="btn btn-primary btn-xs" href="items/${item.jid}">Manage</a>
            </td>
          </tr>
        </#list>
      </tbody>
    </@table.layout>
  </#if>
</@template.layout>
