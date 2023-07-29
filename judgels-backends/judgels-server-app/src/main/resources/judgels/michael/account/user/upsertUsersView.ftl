<#-- @ftlvariable type="judgels.michael.account.user.UpsertUsersView" -->

<#import "/judgels/michael/template/templateLayout.ftl" as template>
<#import "/judgels/michael/ui.ftl" as ui>
<#import "/judgels/michael/forms.ftl" as forms>

<@template.layout>
  <p>Users can be created / updated via a CSV form below.</p>

  <hr>

  <p><b>Allowed CSV headers</b></p>
  <ul style="margin-left: 25px">
    <li><code>jid</code></li>
    <li><code>username</code> (mandatory only when creating users)</li>
    <li><code>password</code> (plaintext, mandatory only when creating users)</li>
    <li><code>email</code> (mandatory only when creating users)</li>
    <li><code>name</code></li>
    <li><code>country</code> (two-letter country code)</li>
  </ul>

  <p>
    If <code>jid</code> is provided, it will be used as the primary key.
    For example, if a user with that JID already exists, the user will be updated.
    (If not, a user with that JID will be created.)
    Otherwise, <code>username</code> will be used as the primary key.
  </p>

  <hr>

  <p>CSV rows, including header:</p>

  <@forms.form type="vertical">
    <@forms.csv name="csv"/>
    <@forms.submit>Submit</@forms.submit>
  </@forms.form>

  <hr>

  <p><b>Example 1</b></p>
  <p>Use case: basic user creation</p>
  <pre>
username,password,email,name
andi,andiandi,andi@judgels.com,Andi Smith
budi,budibudi,budi@judgels.com,Budi Doe</pre>

  <p><b>Example 2</b></p>
  <p>Use case: creating users with fixed JIDs, or updating usernames based on JIDs</p>
  <pre>
jid,username,password,email,name,country
JIDUSER11111111111111111111,andi,andiandi,andi@judgels.com,Andi Smith,ID
JIDUSER22222222222222222222,budi,budibudi,budi@judgels.com,Budi Doe,SG</pre>

  <p><b>Example 3</b></p>
  <p>Use case: resetting user passwords</p>
  <pre>
username,password
andi,newandipass
budi,newbudipass</pre>
</@template.layout>
