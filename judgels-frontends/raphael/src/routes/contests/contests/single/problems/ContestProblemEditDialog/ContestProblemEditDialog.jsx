import { Callout, Classes, Button, Dialog, Intent } from '@blueprintjs/core';
import classNames from 'classnames';
import * as React from 'react';

import ContestProblemEditForm from '../ContestProblemEditForm/ContestProblemEditForm';
import { getContestProblemEditor } from '../modules/editor/contestProblemEditorRegistry';

import './ContestProblemEditDialog.css';

export class ContestProblemEditDialog extends React.Component {
  state;

  constructor(props) {
    super(props);
    this.state = {
      isDialogOpen: false,
      editor: getContestProblemEditor(props.contest.style),
    };
  }

  render() {
    return (
      <div className="content-card__section">
        {this.renderButton()}
        {this.renderDialog()}
      </div>
    );
  }

  componentDidUpdate(prevProps) {
    if (prevProps.contest.style !== this.props.contest.style) {
      this.updateEditor();
    }
  }

  updateEditor() {
    this.setState({
      editor: getContestProblemEditor(this.props.contest.style),
    });
  }

  renderButton = () => {
    return (
      <Button
        className="contest-problem-set-dialog__button"
        intent={Intent.PRIMARY}
        icon="edit"
        onClick={this.toggleDialog}
        disabled={this.state.isDialogOpen}
      >
        Edit problems
      </Button>
    );
  };

  toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen }));
  };

  renderDialog = () => {
    return (
      <Dialog
        className="contest-problem-set-dialog"
        isOpen={this.state.isDialogOpen}
        onClose={this.toggleDialog}
        title="Edit problems"
        canOutsideClickClose={false}
        enforceFocus={false}
      >
        {this.renderDialogSetForm()}
      </Dialog>
    );
  };

  renderDialogSetForm = () => {
    const problems = this.state.editor.serializer(this.props.problems);
    const props = {
      renderFormComponents: this.renderDialogForm,
      validator: this.state.editor.validator,
      onSubmit: this.setProblems,
      initialValues: { problems },
    };
    return <ContestProblemEditForm {...props} />;
  };

  renderDialogForm = (fields, submitButton) => (
    <>
      <div className={classNames(Classes.DIALOG_BODY, 'contest-problem-edit-dialog__body')}>
        {fields}
        {this.renderInstructions()}
      </div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={this.toggleDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  renderInstructions = () => {
    return (
      <Callout icon={null}>
        <p>
          <strong>Format:</strong> {this.state.editor.format}
        </p>
        <p>
          <strong>Example:</strong>
        </p>
        {this.state.editor.example}
      </Callout>
    );
  };

  setProblems = async data => {
    const problems = this.state.editor.deserializer(data.problems);

    await this.props.onSetProblems(this.props.contest.jid, problems);
    this.setState({ isDialogOpen: false });
  };
}
