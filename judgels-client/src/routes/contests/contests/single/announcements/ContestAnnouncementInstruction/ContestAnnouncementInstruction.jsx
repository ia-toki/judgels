import { Button, Callout, Collapse } from '@blueprintjs/core';
import { useState } from 'react';

export function ContestAnnouncementInstruction() {
  const [isOpen, setIsOpen] = useState(false);

  const handleToggle = () => setIsOpen(!isOpen);

  return (
    <div>
      <Button
        onClick={handleToggle}
        rightIcon={isOpen ? 'chevron-up' : 'chevron-down'}
        text="Add Image to Your Contest Announcement"
        style={{ marginBottom: '10px' }}
      />
      <Collapse isOpen={isOpen}>
        <Callout className="bp6-icon-info-sign content-card__section">
          <ol>
            <li>
              Navigate to <strong>Settings</strong> and enable the <strong>Files</strong> module.
            </li>
            <li>
              In the <strong>Contest Menu</strong> &gt; <strong>Files</strong> section, click on{' '}
              <strong>Upload File</strong> to upload your desired image.
            </li>
            <li>
              Insert the uploaded image in the Content form below using the following format:{' '}
              <code>download/&lt;your-image-file-name.jpg&gt;</code>
            </li>
          </ol>
        </Callout>
      </Collapse>
    </div>
  );
}
