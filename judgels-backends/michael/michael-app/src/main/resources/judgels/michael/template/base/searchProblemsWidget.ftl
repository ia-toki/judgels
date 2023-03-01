<#import "/judgels/michael/template/form/forms.ftl" as forms>

<#macro checkboxView tag text tags tagCounts>
  <div class="checkbox">
    <label>
      <input
        type="checkbox"
        name="tags"
        value="${tag}"
        <#if tags?seq_contains(tag)>checked</#if>
      />
      ${text}
    </label>
  </div>
</#macro>

<#macro widget data>
  <div class="widget clearfix">
    <div class="col-md-12">
      <form method="GET" action="/problems">
        <div class="form-group">
          <label>Slug / additional note</label>
          <input type="search" class="form-control" name="filter" value="${data.filterString}">
        </div>

        <hr class="tag-separator" />

        <div class="form-group">
          <label>Visibility</label>
          <@checkboxView tag="visibility-private" text="private" tags=data.tags tagCounts=data.tagCounts/>
          <@checkboxView tag="visibility-public" text="public" tags=data.tags tagCounts=data.tagCounts/>
        </div>

        <hr class="tag-separator" />

        <div class="form-group">
          <label>Statement</label>
          <@checkboxView tag="statement-en" text="has English statement" tags=data.tags tagCounts=data.tagCounts/>
        </div>

        <hr class="tag-separator" />

        <div class="form-group">
          <label>Editorial</label>
          <@checkboxView tag="editorial-no" text="has no editorial" tags=data.tags tagCounts=data.tagCounts/>
          <@checkboxView tag="editorial-yes" text="has editorial" tags=data.tags tagCounts=data.tagCounts/>
          <@checkboxView tag="editorial-en" text="has English editorial" tags=data.tags tagCounts=data.tagCounts/>
        </div>

        <hr class="tag-separator" />

        <#if (data.tagCounts?size > 0)>
          <div class="form-group">
            <label>Tag</label>
            <#list data.topicTags as tag>
              <#if data.tagCounts[tag]??>
                <#local tagName=tag[("topic-"?length)..]>
                <div class="checkbox" <#if tagName?contains(": ")>style="margin-left: 20px"</#if>>
                  <label>
                    <input
                      type="checkbox"
                      class="problemTag"
                      name="tags"
                      value="${tag}"
                      <#if data.tags?seq_contains(tag)>checked</#if>
                    >
                    <#if tagName?contains(": ")>${tagName?split(": ")[1]}<#else>${tagName}</#if>
                    (${data.tagCounts[tag]})
                  </label>
                </div>
              </#if>
            </#list>
          </div>

          <hr class="tag-separator" />
        </#if>

        <@forms.submit>Search</@forms.submit>
      </form>
    </div>

    <script><#include "/judgels/michael/problem/base/problemTags.js"></script>
  </div>
</#macro>
