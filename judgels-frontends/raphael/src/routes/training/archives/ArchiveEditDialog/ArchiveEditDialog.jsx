import { Classes, Button, Dialog } from '@blueprintjs/core';
import * as React from 'react';

import { ArchiveUpdateData, Archive } from '../../../../modules/api/jerahmeel/archive';
import ArchiveEditForm, { ArchiveEditFormData } from '../ArchiveEditForm/ArchiveEditForm';

interface ArchiveEditDialogProps {
  isOpen: boolean;
  archive?: Archive;
  onCloseDialog: () => void;
  onUpdateArchive: (archiveJid: string, data: ArchiveUpdateData) => Promise<void>;
}

export class ArchiveEditDialog extends React.Component<ArchiveEditDialogProps> {
  render() {
    const { archive, isOpen, onCloseDialog } = this.props;
    const initialValues: ArchiveEditFormData = archive && {
      slug: archive.slug,
      name: archive.name,
      category: archive.category,
      description: archive.description,
    };
    const props: any = {
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.updateArchive,
      initialValues,
    };

    return (
      <div className="content-card__section">
        <Dialog isOpen={isOpen} onClose={onCloseDialog} title="Edit archive" canOutsideClickClose={false}>
          <ArchiveEditForm {...props} />
        </Dialog>
      </div>
    );
  }

  private renderDialogForm = (fields: JSX.Element, submitButton: JSX.Element) => (
    <>
      <div className={Classes.DIALOG_BODY}>{fields}</div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={this.props.onCloseDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  private updateArchive = async (data: ArchiveUpdateData) => {
    await this.props.onUpdateArchive(this.props.archive.jid, data);
  };
}
