<#macro view languages language>
  <div class="small pull-left">
    Current language: <span class="label label-default">${languages[language]}</span>
  </div>

  <form class="form-inline pull-right">
    <label for="language"><span class="small">Switch to</span></label>
    <div class="form-group">
      <select id="language" name="language">
        <#list languages as k, v>
          <option value="${k}" ${(k == language)?then("selected", "")}>${v}</option>
        </#list>
      </select>
    </div>
    <button type="submit" class="btn btn-primary btn-xs">Switch</button>
  </form>

  <div class="clearfix"></div>

  <hr>
</#macro>
