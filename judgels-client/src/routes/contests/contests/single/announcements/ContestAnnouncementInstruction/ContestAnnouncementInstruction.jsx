import { Callout } from "@blueprintjs/core";

export function ContestAnnouncementInstruction() {
  return (
    <Callout className="bp5-icon-info-sign heading-with-button-action">
      <h5 className="">Add an Image to Your Announcement</h5>
      <p>
        Want to make your announcement pop? Try adding an image! Here's how:
        <ul>
          <li>First, go to Settings and turn on the 'Files' module.</li>
          <li>Then, in the 'Files' area, click 'Upload File' to upload your picture.</li>
          <li>Now, just write <code>download/[your-image-name].jpg</code> in the announcement where you want the image. Make sure to change <code>[your-image-name]</code> to the name of the file you uploaded.</li>
        </ul>
        That's it! A little picture can say a lot.
      </p>
    </Callout>
  );
}
