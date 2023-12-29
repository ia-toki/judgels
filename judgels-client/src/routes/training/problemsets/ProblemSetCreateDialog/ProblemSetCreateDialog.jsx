import { Button, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Plus } from '@blueprintjs/icons';
import { Component } from 'react';

import ProblemSetCreateForm from '../ProblemSetCreateForm/ProblemSetCreateForm';

export class ProblemSetCreateDialog extends Component {
  state = {
    isDialogOpen: false,
  };

  render() {
    return (
      <div className="content-card__section">
        {this.renderButton()}
        {this.renderDialog()}
      </div>
    );
  }

  renderButton = () => {
    return (
      <Button intent={Intent.PRIMARY} icon={<Plus />} onClick={this.toggleDialog} disabled={this.state.isDialogOpen}>
        New problemset
      </Button>
    );
  };

  toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen }));
  };

  renderDialog = () => {
    const initialValues = {
      contestTime: new Date().toISOString(),
    };
    const props = {
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.createProblemSet,
      initialValues,
    };
    return (
      <Dialog
        isOpen={this.state.isDialogOpen}
        onClose={this.toggleDialog}
        title="Create new problemset"
        canOutsideClickClose={false}
      >
        <ProblemSetCreateForm {...props} />
      </Dialog>
    );
  };

  renderDialogForm = (fields, submitButton) => (
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

  createProblemSet = async data => {
    await this.props.onCreateProblemSet({
      slug: data.slug,
      name: data.name,
      archiveSlug: data.archiveSlug,
      description: data.description,
      contestTime: new Date(data.contestTime).getTime(),
    });
    this.setState({ isDialogOpen: false });
  };
}
