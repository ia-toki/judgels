<#macro button intent="primary" size="" disabled=false class="" clazz="" style="" onclick="">
  <button
    type="button"
    class="btn btn-${intent} <#if size?has_content>btn-${size}</#if> ${class}"
    <#if disabled>disabled</#if>
    <#if style?has_content>style="${style}"</#if>
    <#if onclick?has_content>onclick="${onclick}"</#if>
  >
    <#nested>
  </button>
</#macro>

<#macro link to intent="primary" size="" disabled=false class="" style="" onclick="">
  <a
    type="button" href="${to}"
    class="btn btn-${intent} <#if size?has_content>btn-${size}</#if> ${class}"
    <#if disabled>disabled</#if>
    <#if style?has_content>style="${style}"</#if>
    <#if onclick?has_content>onclick="${onclick}"</#if>
  >
    <#nested>
  </a>
</#macro>

<#macro submit intent="primary" size="" disabled=false class="" style="">
  <button
    type="button"
    class="btn btn-${intent} <#if size?has_content>btn-${size}</#if> ${class}"
    <#if disabled>disabled</#if>
    <#if style?has_content>style="${style}"</#if>
  >
    <#nested>
  </button>
</#macro>
