import { Callout, Classes, Button, Dialog, Intent } from '@blueprintjs/core';
import classNames from 'classnames';
import * as React from 'react';

import { Contest } from '../../../../../../modules/api/uriel/contest';
import { ContestProblemData } from '../../../../../../modules/api/uriel/contestProblem';

import ContestProblemEditForm, { ContestProblemEditFormData } from '../ContestProblemEditForm/ContestProblemEditForm';
import { getContestProblemEditor } from '../modules/editor/contestProblemEditorRegistry';
import { ContestProblemEditor } from '../modules/editor/contestProblemEditor';

import './ContestProblemEditDialog.css';

export interface ContestProblemEditDialogProps {
  contest: Contest;
  problems: ContestProblemData[];
  onSetProblems: (contestJid: string, data: ContestProblemData[]) => Promise<void>;
}

interface ContestProblemEditDialogState {
  isDialogOpen?: boolean;
  editor: ContestProblemEditor;
}

export class ContestProblemEditDialog extends React.Component<
  ContestProblemEditDialogProps,
  ContestProblemEditDialogState
> {
  state: ContestProblemEditDialogState;

  constructor(props) {
    super(props);
    this.state = {
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

  componentDidUpdate(prevProps: ContestProblemEditDialogProps) {
    if (prevProps.contest.style !== this.props.contest.style) {
      this.updateEditor();
    }
  }

  private updateEditor() {
    this.setState({
      editor: getContestProblemEditor(this.props.contest.style),
    });
  }

  private renderButton = () => {
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

  private toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen }));
  };

  private renderDialog = () => {
    return (
      <Dialog
        className="contest-problem-set-dialog"
        isOpen={this.state.isDialogOpen || false}
        onClose={this.toggleDialog}
        title="Edit problems"
        canOutsideClickClose={false}
        enforceFocus={false}
      >
        {this.renderDialogSetForm()}
      </Dialog>
    );
  };

  private renderDialogSetForm = () => {
    const problems = this.state.editor.serializer(this.props.problems);
    const props: any = {
      renderFormComponents: this.renderDialogForm,
      validator: this.state.editor.validator,
      onSubmit: this.setProblems,
      initialValues: { problems },
    };
    return <ContestProblemEditForm {...props} />;
  };

  private renderDialogForm = (fields: JSX.Element, submitButton: JSX.Element) => (
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

  private renderInstructions = () => {
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

  private setProblems = async (data: ContestProblemEditFormData) => {
    const problems = this.state.editor.deserializer(data.problems);

    await this.props.onSetProblems(this.props.contest.jid, problems);
    this.setState({ isDialogOpen: false });
  };
}
