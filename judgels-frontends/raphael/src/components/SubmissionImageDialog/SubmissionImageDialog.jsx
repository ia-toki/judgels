import { Button, Classes, Dialog } from '@blueprintjs/core';

import { ContentCard } from '../ContentCard/ContentCard';

import './SubmissionImageDialog.css';

export function SubmissionImageDialog({ isOpen, onClose, title, imageUrl }) {
  return (
    <Dialog
      className="submission-image-dialog"
      isOpen={isOpen}
      onClose={onClose}
      title={title}
      canOutsideClickClose={true}
      enforceFocus={true}
    >
      <div className={Classes.DIALOG_BODY}>
        <ContentCard>
          <div className="submission-image">
            <img src={imageUrl} />
          </div>
        </ContentCard>
      </div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Close" onClick={onClose} />
        </div>
      </div>
    </Dialog>
  );
}
