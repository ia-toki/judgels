import { Card, Classes, Dialog } from '@blueprintjs/core';

import './SubmissionImageDialog.scss';

export function SubmissionImageDialog({ onClose, title, imageUrl }) {
  return (
    <Dialog
      className="submission-image-dialog"
      isOpen
      onClose={onClose}
      title={title}
      canOutsideClickClose={true}
      enforceFocus={true}
    >
      <div className={Classes.DIALOG_BODY}>
        <Card className="submission-image-card">
          <div className="submission-image">
            <img src={imageUrl} alt="submission" />
          </div>
        </Card>
      </div>
    </Dialog>
  );
}
