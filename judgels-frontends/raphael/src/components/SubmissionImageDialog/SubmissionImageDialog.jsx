import { Button, Classes, Dialog } from '@blueprintjs/core';
import classNames from 'classnames';

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
      <div className={classNames(Classes.DIALOG_BODY, 'submission-image')}>
        <img src={imageUrl} />
      </div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Close" onClick={onClose} />
        </div>
      </div>
    </Dialog>
  );
}
