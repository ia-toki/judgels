import { Callout } from "@blueprintjs/core";

export function ContestAnnouncementInstruction() {
  return (
    <Callout className="bp5-icon-info-sign heading-with-button-action">
      <h5>Add Image to Your Contest Announcement</h5>
      <ol>
        <li>
          Navigate to <strong>Settings</strong> and enable the <strong>Files</strong> module.
        </li>
        <li>
          In the <strong>Contest Menu</strong> &gt; <strong>Files</strong> section, click on <strong>Upload File</strong> to upload your desired image.
        </li>
        <li>
          Insert the uploaded image in the Content form below using the following format: <code>download/&lt;your-image-file-name.jpg&gt;</code>
        </li>
      </ol>
    </Callout>
  );
}
