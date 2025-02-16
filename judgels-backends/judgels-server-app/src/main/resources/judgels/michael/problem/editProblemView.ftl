<#-- @ftlvariable type="judgels.michael.problem.EditProblemView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/forms.ftl" as forms>

<@template.layout>
  <@forms.form>
    <h3>Info</h3>
    <@forms.input name="slug" label="Slug" required=true pattern="[a-z0-9]+(-[a-z0-9]+)*" title="Slug can only consist of alphanumerics and dashes"/>
    <div class="form-group">
      <div class="col-md-3"></div>
      <div class="col-md-9">
        <#include "problemSlugGuide.html">
      </div>
    </div>
    <@forms.textarea name="additionalNote" label="Additional note"/>

    <h3>Metadata</h3>
    <@forms.input name="writerUsernames" label="Writers" help="List of comma-separated usernames."/>
    <@forms.input name="developerUsernames" label="Developers" help="List of comma-separated usernames. Fill only if different from writers."/>
    <@forms.input name="testerUsernames" label="Testers" help="List of comma-separated usernames."/>
    <@forms.input name="editorialistUsernames" label="Editorialists" help="List of comma-separated usernames."/>

    <h3>Tags</h3>
    <#include "problemTaggingGuide.html">
    <#list topicTags as tag>
      <#assign tagName=tag[("topic-"?length)..]>
      <div class="checkbox" <#if tagName?contains(": ")>style="margin-left: 20px"</#if>>
        <label>
          <input
            type="checkbox"
            class="problemTag"
            name="tags"
            value="${tag}"
            <#if formValues.tags?seq_contains(tag)>checked</#if>
          >
          <#if tagName?contains(": ")>${tagName?split(": ")[1]}<#else>${tagName}</#if>
        </label>
      </div>
    </#list>

    <@forms.submit>Update</@forms.submit>
  </@forms.form>

  <script><#include "/judgels/michael/template/base/problemTags.js"></script>
</@template.layout>
