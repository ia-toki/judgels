import { Classes, Intent, Button, Dialog } from '@blueprintjs/core';
import * as React from 'react';

import { ProblemSetCreateData } from '../../../../modules/api/jerahmeel/problemSet';
import ProblemSetCreateForm from '../ProblemSetCreateForm/ProblemSetCreateForm';

interface ProblemSetCreateDialogProps {
  onCreateProblemSet: (data: ProblemSetCreateData) => Promise<void>;
}

interface ProblemSetCreateDialogState {
  isDialogOpen?: boolean;
}

export class ProblemSetCreateDialog extends React.Component<ProblemSetCreateDialogProps, ProblemSetCreateDialogState> {
  state: ProblemSetCreateDialogState = {};

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
        New problemset
      </Button>
    );
  };

  private toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen }));
  };

  private renderDialog = () => {
    const props: any = {
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.createProblemSet,
    };
    return (
      <Dialog
        isOpen={this.state.isDialogOpen || false}
        onClose={this.toggleDialog}
        title="Create new problemset"
        canOutsideClickClose={false}
      >
        <ProblemSetCreateForm {...props} />
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

  private createProblemSet = async (data: ProblemSetCreateData) => {
    await this.props.onCreateProblemSet(data);
    this.setState({ isDialogOpen: false });
  };
}
