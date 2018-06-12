import { Button, Dialog, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';
import { change } from 'redux-form';

import ContestClarificationCreateForm, {
  ContestClarificationCreateFormData,
} from '../../ContestClarificationCreateForm/ContestClarificationCreateForm';
import { ContestClarificationData } from '../../../../../../../../../../../modules/api/uriel/contestClarification';
import { contestClarificationActions as injectedContestClarificationActions } from '../../modules/contestClarificationActions';

export interface ContestClarificationCreateDialogProps {
  contestJid: string;
  problemJids: string[];
  problemAliasesMap: { [problemJid: string]: string };
  problemNamesMap: { [problemJid: string]: string };
  onRefreshClarifications: () => Promise<void>;
}

export interface ContestClarificationCreateDialogConnectedProps {
  onCreateClarification: (contestJid: string, data: ContestClarificationData) => void;
  onSubmitCreateClarification: () => Promise<void>;
  onSetDefaultTopic: (contestJid: string) => void;
}

interface ContestClarificationCreateDialogState {
  isDialogOpen?: boolean;
  isDialogLoading?: boolean;
}

class ContestClarificationCreateDialog extends React.Component<
  ContestClarificationCreateDialogProps & ContestClarificationCreateDialogConnectedProps,
  ContestClarificationCreateDialogState
> {
  state: ContestClarificationCreateDialogState = {};

  componentDidMount() {
    this.props.onSetDefaultTopic(this.props.contestJid);
  }

  componentDidUpdate() {
    this.props.onSetDefaultTopic(this.props.contestJid);
  }

  render() {
    return (
      <>
        {this.renderButton()}
        {this.renderDialog()}
      </>
    );
  }

  private renderButton = () => {
    return (
      <Button intent={Intent.PRIMARY} icon="plus" onClick={this.toggleDialog} disabled={this.state.isDialogOpen}>
        New Clarification
      </Button>
    );
  };

  private toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen }));
  };

  private renderDialog = () => {
    const props: any = {
      contestJid: this.props.contestJid,
      problemJids: this.props.problemJids,
      problemAliasesMap: this.props.problemAliasesMap,
      problemNamesMap: this.props.problemNamesMap,
      renderFormComponents: this.renderDialogForm,
      onSubmit: this.createClarification,
    };
    return (
      <Dialog
        isOpen={this.state.isDialogOpen || false}
        onClose={this.toggleDialog}
        title="Submit new clarification"
        canOutsideClickClose={false}
      >
        <ContestClarificationCreateForm {...props} />
      </Dialog>
    );
  };

  private renderDialogForm = (fields: JSX.Element, submitButton: JSX.Element) => (
    <>
      <div className="pt-dialog-body">{fields}</div>
      <div className="pt-dialog-footer">
        <div className="pt-dialog-footer-actions">
          <Button text="Cancel" onClick={this.toggleDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  private createClarification = async (data: ContestClarificationCreateFormData) => {
    this.setState({ isDialogLoading: true });
    await this.props.onCreateClarification(this.props.contestJid, data);
    await this.props.onRefreshClarifications();
    this.setState({ isDialogLoading: false, isDialogOpen: false });
  };
}

function createContestClarificationCreateDialog(contestClarificationActions) {
  const mapDispatchToProps = dispatch => ({
    onCreateClarification: (contestJid: string, data: ContestClarificationData) =>
      dispatch(contestClarificationActions.create(contestJid, data)),
    onSetDefaultTopic: (contestJid: string) => dispatch(change('contest-clarification-create', 'topicJid', contestJid)),
  });
  return connect(undefined, mapDispatchToProps)(ContestClarificationCreateDialog);
}

export default createContestClarificationCreateDialog(injectedContestClarificationActions);
