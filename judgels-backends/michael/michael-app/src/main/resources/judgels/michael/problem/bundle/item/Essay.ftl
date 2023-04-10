<#import "/judgels/michael/forms.ftl" as forms>

<#macro view item config>
  <div class="clearfix">
    <div style="display: table-cell">
      ${item.number.get()}.&nbsp;
    </div>
    <div style="display: table-cell">
      <div class="content-text">
        ${config.statement?no_esc}
      </div>
    </div>
  </div>
  <hr />
  <textarea
    rows="16"
    cols="80"
    wrap="off"
    style="font-family: Consolas, Monaco, DejaVu Sans Mono, Bitstream Vera Sans Mono, Courier New, monospace;"
    name="${item.jid}"
    onkeydown="
      // Code to disable tab navigation in the textarea and enter 2 spaces instead
      if (event.keyCode === 9) {
        var v = this.value, s = this.selectionStart, e = this.selectionEnd;
        this.value = v.substring(0, s) + '  ' + v.substring(e);
        this.selectionStart = this.selectionEnd = s + 2;
        return false;
      }
    "
  ></textarea>
</#macro>

<#macro edit>
  <@forms.input type="number" name="score" label="Score" required=true help="Points for correct answer" disabled=!canEdit/>
</#macro>
