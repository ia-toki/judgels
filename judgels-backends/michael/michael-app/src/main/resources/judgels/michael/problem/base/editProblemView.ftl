<#-- @ftlvariable type="judgels.michael.problem.base.EditProblemView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>

<#import "/judgels/michael/template/form/forms.ftl" as forms>

<@template.layout>
  <@forms.form>
    <h3>Info</h3>
    <@forms.text form=form name="slug" label="Slug" required=true pattern="[a-z0-9]+(-[a-z0-9]+)*" title="Slug can only consist of alphanumerics and dashes"/>
    <@forms.textarea form=form name="additionalNote" label="Additional note"/>

    <h3>Metadata</h3>
    <@forms.text form=form name="writerUsernames" label="Writers" help="List of comma-separated usernames."/>
    <@forms.text form=form name="developerUsernames" label="Developers" help="List of comma-separated usernames. Fill only if different from writers."/>
    <@forms.text form=form name="testerUsernames" label="Testers" help="List of comma-separated usernames."/>
    <@forms.text form=form name="editorialistUsernames" label="Editorialists" help="List of comma-separated usernames."/>

    <h3>Tags</h3>
    <#list topicTags as tag>
      <#assign tagName=tag[("topic-"?length)..]>
      <div class="checkbox" <#if tagName?contains(": ")>style="margin-left: 20px"</#if>>
        <label>
          <input
            type="checkbox"
            class="problemTag"
            name="tags"
            value="${tag}"
            <#if form.tags?seq_contains(tag)>checked</#if>
          >
          <#if tagName?contains(": ")>${tagName?split(": ")[1]}<#else>${tagName}</#if>
        </label>
      </div>
    </#list>

    <@forms.submit>Update</@forms.submit>
  </@forms.form>

  <script><#include "/judgels/michael/problem/base/problemTags.js"></script>
</@template.layout>
