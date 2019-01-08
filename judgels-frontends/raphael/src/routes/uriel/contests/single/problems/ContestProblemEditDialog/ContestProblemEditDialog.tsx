import { Callout, Classes, Button, Dialog, Intent } from '@blueprintjs/core';
import * as classNames from 'classnames';
import * as React from 'react';

import { Contest } from 'modules/api/uriel/contest';
import { ContestProblemData, ContestProblemStatus } from 'modules/api/uriel/contestProblem';

import ContestProblemEditForm, { ContestProblemEditFormData } from '../ContestProblemEditForm/ContestProblemEditForm';

import './ContestProblemEditDialog.css';

export interface ContestProblemEditDialogProps {
  contest: Contest;
  problems: ContestProblemData[];
  onSetProblems: (contestJid: string, data: ContestProblemData[]) => Promise<void>;
}

interface ContestProblemEditDialogState {
  isDialogOpen?: boolean;
}

export class ContestProblemEditDialog extends React.Component<
  ContestProblemEditDialogProps,
  ContestProblemEditDialogState
> {
  state: ContestProblemEditDialogState = {};

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
    const problems = this.props.problems
      .map(p => {
        if (p.submissionsLimit > 0) {
          return `${p.alias},${p.slug},${p.status},${p.submissionsLimit}`;
        } else if (p.status !== ContestProblemStatus.Open) {
          return `${p.alias},${p.slug},${p.status}`;
        } else {
          return `${p.alias},${p.slug}`;
        }
      })
      .join('\n');
    const props: any = {
      renderFormComponents: this.renderDialogForm,
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
          <strong>Format:</strong> <code>alias,slug[,status[,submissionsLimit]]</code>
        </p>
        <p>
          <strong>Example:</strong>
        </p>
        <pre>{'A,hello\nB,tree,CLOSED\nC,flow,OPEN,20'}</pre>
      </Callout>
    );
  };

  private setProblems = async (data: ContestProblemEditFormData) => {
    const problems = data.problems
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0)
      .map(s => s.split(','))
      .map(s => s.map(t => t.trim()))
      .map(
        s =>
          ({
            alias: s[0],
            slug: s[1],
            status: s[2] || ContestProblemStatus.Open,
            submissionsLimit: +s[3] || 0,
          } as ContestProblemData)
      );

    await this.props.onSetProblems(this.props.contest.jid, problems);
    this.setState({ isDialogOpen: false });
  };
}
