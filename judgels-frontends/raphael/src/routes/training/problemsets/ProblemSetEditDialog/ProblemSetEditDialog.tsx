import { Classes, Button, Dialog } from '@blueprintjs/core';
import * as React from 'react';

import { ProblemSetUpdateData, ProblemSet } from '../../../../modules/api/jerahmeel/problemSet';
import ProblemSetEditForm, { ProblemSetEditFormData } from '../ProblemSetEditForm/ProblemSetEditForm';

interface ProblemSetEditDialogProps {
  isOpen: boolean;
  problemSet?: ProblemSet;
  archiveSlug?: string;
  onCloseDialog: () => void;
  onUpdateProblemSet: (problemSetJid: string, data: ProblemSetUpdateData) => Promise<void>;
}

export class ProblemSetEditDialog extends React.Component<ProblemSetEditDialogProps> {
  render() {
    const { problemSet, archiveSlug, isOpen, onCloseDialog } = this.props;
    const initialValues: ProblemSetEditFormData = problemSet && {
      slug: problemSet.slug,
      name: problemSet.name,
      archiveSlug: archiveSlug,
      description: problemSet.description,
    };
    const props: any = {
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.updateProblemSet,
      initialValues,
    };

    return (
      <div className="content-card__section">
        <Dialog isOpen={isOpen} onClose={onCloseDialog} title="Edit problemset" canOutsideClickClose={false}>
          <ProblemSetEditForm {...props} />
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

  private updateProblemSet = async (data: ProblemSetUpdateData) => {
    await this.props.onUpdateProblemSet(this.props.problemSet.jid, data);
  };
}
