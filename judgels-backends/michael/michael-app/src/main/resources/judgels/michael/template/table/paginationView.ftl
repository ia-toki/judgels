<#function newPageLink newPageIndex filterString="" tags=[]>
  <#local res = []>
  <#if (newPageIndex > 1)>
    <#local res += ["page=${newPageIndex}"]>
  </#if>
  <#if filterString?has_content>
    <#local res += ["filter=${filterString}"]>
  </#if>
  <#list tags as tag>
    <#local res += ["tags=${tag}"]>
  </#list>
  <#return "?" + res?join("&")>
</#function>

<#macro view page filterString="" tags=[]>
  <div>
    <small>
      Showing ${(page.pageIndex - 1) * page.pageSize + 1} - ${[page.totalCount, page.pageIndex * page.pageSize]?min} out of ${page.totalCount} data.
    </small>
  </div>

  <ul class="pagination pagination-sm">
    <#if (page.pageIndex > 1)>
      <li><a href="${newPageLink(1, filterString, tags)}">&laquo;</a></li>
      <li><a href="${newPageLink(page.pageIndex - 1, filterString, tags)}">&larr;</a></li>
    <#else>
      <li class="disabled"><a>&laquo;</a></li>
      <li class="disabled"><a>&larr;</a></li>
    </#if>

    <#if ([1, page.pageIndex - 9]?max <= (page.pageIndex - 1))>
      <#list [1, page.pageIndex - 9]?max..(page.pageIndex - 1) as i>
        <li><a href="${newPageLink(i, filterString, tags)}">${i}</a></li>
      </#list>
    </#if>

    <li class="active"><a>${page.pageIndex}</a></li>

    <#if ((page.pageIndex + 1) <= [(page.totalCount + page.pageSize - 1) / page.pageSize, page.pageIndex + 9]?min)>
      <#list (page.pageIndex + 1)..[(page.totalCount + page.pageSize - 1) / page.pageSize, page.pageIndex + 9]?min as i>
        <li><a href="${newPageLink(i, filterString, tags)}">${i}</a></li>
      </#list>
    </#if>

    <#if (page.pageIndex + 1 <= (page.totalCount + page.pageSize - 1) / page.pageSize)>
      <li><a href="${newPageLink(page.pageIndex + 1, filterString, tags)}">&rarr;</a></li>
      <li><a href="${newPageLink((page.totalCount + page.pageSize - 1) / page.pageSize, filterString, tags)}">&raquo;</a></li>
    <#else>
      <li class="disabled"><a>&rarr;</a></li>
      <li class="disabled"><a>&raquo;</a></li>
    </#if>
  </ul>
</#macro>
