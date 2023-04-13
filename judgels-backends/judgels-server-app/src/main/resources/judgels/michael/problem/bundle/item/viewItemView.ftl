<#import "Statement.ftl" as statement>
<#import "MultipleChoice.ftl" as multipleChoice>
<#import "ShortAnswer.ftl" as shortAnswer>
<#import "Essay.ftl" as essay>

<#macro view item type config>
  <div class="panel panel-default">
    <div class="panel-body">
      <#if type == "STATEMENT">
        <@statement.view item=item config=config/>
      <#elseif type == "MULTIPLE_CHOICE">
        <@multipleChoice.view item=item config=config/>
      <#elseif type == "SHORT_ANSWER">
        <@shortAnswer.view item=item config=config/>
      <#elseif type == "ESSAY">
        <@essay.view item=item config=config/>
      </#if>
    </div>
  </div>
</#macro>
