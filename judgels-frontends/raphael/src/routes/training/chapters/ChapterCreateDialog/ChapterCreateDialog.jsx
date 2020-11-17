import { Classes, Intent, Button, Dialog } from '@blueprintjs/core';
import * as React from 'react';

import { ChapterCreateData } from '../../../../modules/api/jerahmeel/chapter';
import ChapterCreateForm from '../ChapterCreateForm/ChapterCreateForm';

interface ChapterCreateDialogProps {
  onCreateChapter: (data: ChapterCreateData) => Promise<void>;
}

interface ChapterCreateDialogState {
  isDialogOpen?: boolean;
}

export class ChapterCreateDialog extends React.Component<ChapterCreateDialogProps, ChapterCreateDialogState> {
  state: ChapterCreateDialogState = {};

  render() {
    return (
      <div className="content-card__section">
        {this.renderButton()}
        {this.renderDialog()}
      </div>
    );
  }

  private renderButton = () => {
    return (
      <Button intent={Intent.PRIMARY} icon="plus" onClick={this.toggleDialog} disabled={this.state.isDialogOpen}>
        New chapter
      </Button>
    );
  };

  private toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen }));
  };

  private renderDialog = () => {
    const props: any = {
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.createChapter,
    };
    return (
      <Dialog
        isOpen={this.state.isDialogOpen || false}
        onClose={this.toggleDialog}
        title="Create new chapter"
        canOutsideClickClose={false}
      >
        <ChapterCreateForm {...props} />
      </Dialog>
    );
  };

  private renderDialogForm = (fields: JSX.Element, submitButton: JSX.Element) => (
    <>
      <div className={Classes.DIALOG_BODY}>{fields}</div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={this.toggleDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  private createChapter = async (data: ChapterCreateData) => {
    await this.props.onCreateChapter(data);
    this.setState({ isDialogOpen: false });
  };
}
