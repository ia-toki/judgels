<#import "/judgels/michael/forms.ftl" as forms>
<#import "/judgels/michael/ui.ftl" as ui>

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
  <div class="widget">
    <form method="GET" action="/problems">
      <@forms.formGroup>
        <label>Slug / additional note</label>
        <input type="search" class="form-control" name="term" value="${data.termFilter}">
      </@forms.formGroup>

      <hr class="tag-separator" />

      <@forms.formGroup>
        <label>Visibility</label>
        <@checkboxView tag="visibility-private" text="private" tags=data.tagsFilter tagCounts=data.tagCounts/>
        <@checkboxView tag="visibility-public" text="public" tags=data.tagsFilter tagCounts=data.tagCounts/>
      </@forms.formGroup>

      <hr class="tag-separator" />

      <@forms.formGroup>
        <label>Statement</label>
        <@checkboxView tag="statement-en" text="has English statement" tags=data.tagsFilter tagCounts=data.tagCounts/>
      </@forms.formGroup>

      <hr class="tag-separator" />

      <@forms.formGroup>
        <label>Editorial</label>
        <@checkboxView tag="editorial-no" text="has no editorial" tags=data.tagsFilter tagCounts=data.tagCounts/>
        <@checkboxView tag="editorial-yes" text="has editorial" tags=data.tagsFilter tagCounts=data.tagCounts/>
        <@checkboxView tag="editorial-en" text="has English editorial" tags=data.tagsFilter tagCounts=data.tagCounts/>
      </@forms.formGroup>

      <hr class="tag-separator" />

      <#if (data.tagCounts?size > 0)>
        <@forms.formGroup>
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
                    <#if data.tagsFilter?seq_contains(tag)>checked</#if>
                  >
                  <#if tagName?contains(": ")>${tagName?split(": ")[1]}<#else>${tagName}</#if>
                  (${data.tagCounts[tag]})
                </label>
              </div>
            </#if>
          </#list>
        </@forms.formGroup>

        <hr class="tag-separator" />
      </#if>

      <div class="text-center">
        <@ui.button type="submit">Search</@ui.button>
      </div>
    </form>

    <script><#include "problemTags.js"></script>
  </div>
</#macro>
