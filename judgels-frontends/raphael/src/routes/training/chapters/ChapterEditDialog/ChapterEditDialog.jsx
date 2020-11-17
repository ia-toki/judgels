import { Classes, Button, Dialog } from '@blueprintjs/core';
import * as React from 'react';

import { ChapterUpdateData, Chapter } from '../../../../modules/api/jerahmeel/chapter';
import ChapterEditForm, { ChapterEditFormData } from '../ChapterEditForm/ChapterEditForm';

interface ChapterEditDialogProps {
  isOpen: boolean;
  chapter?: Chapter;
  onCloseDialog: () => void;
  onUpdateChapter: (chapterJid: string, data: ChapterUpdateData) => Promise<void>;
}

export class ChapterEditDialog extends React.Component<ChapterEditDialogProps> {
  render() {
    const { chapter, isOpen, onCloseDialog } = this.props;
    const initialValues: ChapterEditFormData = chapter && {
      name: chapter.name,
    };
    const props: any = {
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.updateChapter,
      initialValues,
    };

    return (
      <div className="content-card__section">
        <Dialog isOpen={isOpen} onClose={onCloseDialog} title="Edit chapter" canOutsideClickClose={false}>
          <ChapterEditForm {...props} />
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

  private updateChapter = async (data: ChapterUpdateData) => {
    await this.props.onUpdateChapter(this.props.chapter.jid, data);
  };
}
