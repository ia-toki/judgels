<#function newPageLink newPageNumber termFilter="" tagsFilter=[]>
  <#local res = []>
  <#if (newPageNumber > 1)>
    <#local res += ["page=${newPageNumber}"]>
  </#if>
  <#if termFilter?has_content>
    <#local res += ["term=${termFilter}"]>
  </#if>
  <#list tagsFilter as tag>
    <#local res += ["tags=${tag}"]>
  </#list>
  <#return "?" + res?join("&")>
</#function>

<#macro pagination page termFilter="" tagsFilter=[]>
  <div>
    <small>
      Showing ${(page.pageNumber - 1) * page.pageSize + 1} - ${[page.totalCount, page.pageNumber * page.pageSize]?min} out of ${page.totalCount} data.
    </small>
  </div>

  <ul class="pagination pagination-sm">
    <#if (page.pageNumber > 1)>
      <li><a href="${newPageLink(1, termFilter, tagsFilter)}">&laquo;</a></li>
      <li><a href="${newPageLink(page.pageNumber - 1, termFilter, tagsFilter)}">&larr;</a></li>
    <#else>
      <li class="disabled"><a>&laquo;</a></li>
      <li class="disabled"><a>&larr;</a></li>
    </#if>

    <#if ([1, page.pageNumber - 9]?max <= (page.pageNumber - 1))>
      <#list [1, page.pageNumber - 9]?max..(page.pageNumber - 1) as i>
        <li><a href="${newPageLink(i, termFilter, tagsFilter)}">${i}</a></li>
      </#list>
    </#if>

    <li class="active"><a>${page.pageNumber}</a></li>

    <#if ((page.pageNumber + 1) <= [(page.totalCount + page.pageSize - 1) / page.pageSize, page.pageNumber + 9]?min)>
      <#list (page.pageNumber + 1)..[(page.totalCount + page.pageSize - 1) / page.pageSize, page.pageNumber + 9]?min as i>
        <li><a href="${newPageLink(i, termFilter, tagsFilter)}">${i}</a></li>
      </#list>
    </#if>

    <#if (page.pageNumber + 1 <= (page.totalCount + page.pageSize - 1) / page.pageSize)>
      <li><a href="${newPageLink(page.pageNumber + 1, termFilter, tagsFilter)}">&rarr;</a></li>
      <li><a href="${newPageLink((page.totalCount + page.pageSize - 1) / page.pageSize, termFilter, tagsFilter)}">&raquo;</a></li>
    <#else>
      <li class="disabled"><a>&rarr;</a></li>
      <li class="disabled"><a>&raquo;</a></li>
    </#if>
  </ul>
</#macro>
