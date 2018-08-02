import { Button, Callout, Dialog, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';
import { change } from 'redux-form';

import ContestClarificationCreateForm, {
  ContestClarificationCreateFormData,
} from '../../ContestClarificationCreateForm/ContestClarificationCreateForm';
import { ContestClarificationConfig, ContestClarificationData } from 'modules/api/uriel/contestClarification';
import { selectStatementLanguage } from 'modules/webPrefs/webPrefsSelectors';
import { AppState } from 'modules/store';
import { Contest } from 'modules/api/uriel/contest';

import { selectContest } from '../../../../modules/contestSelectors';
import { contestClarificationActions as injectedContestClarificationActions } from '../../modules/contestClarificationActions';

export interface ContestClarificationCreateDialogProps {
  onRefreshClarifications: () => Promise<void>;
}

export interface ContestClarificationCreateDialogConnectedProps {
  contest: Contest;
  statementLanguage: string;
  onGetClarificationConfig: (contestJid: string, language: string) => Promise<ContestClarificationConfig>;
  onCreateClarification: (contestJid: string, data: ContestClarificationData) => void;
  onSetDefaultTopic: (contestJid: string) => void;
}

interface ContestClarificationCreateDialogState {
  config?: ContestClarificationConfig;
  isDialogOpen?: boolean;
  isDialogLoading?: boolean;
}

class ContestClarificationCreateDialog extends React.Component<
  ContestClarificationCreateDialogProps & ContestClarificationCreateDialogConnectedProps,
  ContestClarificationCreateDialogState
> {
  state: ContestClarificationCreateDialogState = {};

  async componentDidMount() {
    const config = await this.props.onGetClarificationConfig(this.props.contest.jid, this.props.statementLanguage);
    if (config.isAllowedToCreateClarification) {
      this.props.onSetDefaultTopic(this.props.contest.jid);
    }
    this.setState({ config });
  }

  render() {
    const { config } = this.state;
    if (!config) {
      return null;
    }

    return (
      <>
        {this.renderButton(config)}
        {this.renderDialog(config)}
      </>
    );
  }

  private renderButton = (config: ContestClarificationConfig) => {
    if (!config.isAllowedToCreateClarification) {
      return (
        <Callout icon="ban-circle" className="secondary-info">
          No more clarifications are allowed.
        </Callout>
      );
    }
    return (
      <Button intent={Intent.PRIMARY} icon="plus" onClick={this.toggleDialog} disabled={this.state.isDialogOpen}>
        New Clarification
      </Button>
    );
  };

  private toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen }));
  };

  private renderDialog = (config: ContestClarificationConfig) => {
    const props: any = {
      contestJid: this.props.contest.jid,
      problemJids: config.problemJids,
      problemAliasesMap: config.problemAliasesMap,
      problemNamesMap: config.problemNamesMap,
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
    await this.props.onCreateClarification(this.props.contest.jid, data);
    await this.props.onRefreshClarifications();
    this.setState({ isDialogLoading: false, isDialogOpen: false });
  };
}

function createContestClarificationCreateDialog(contestClarificationActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
    statementLanguage: selectStatementLanguage(state),
  });

  const mapDispatchToProps = dispatch => ({
    onGetClarificationConfig: (contestJid: string, language: string) =>
      dispatch(contestClarificationActions.getClarificationConfig(contestJid, language)),
    onCreateClarification: (contestJid: string, data: ContestClarificationData) =>
      dispatch(contestClarificationActions.createClarification(contestJid, data)),
    onSetDefaultTopic: (contestJid: string) => dispatch(change('contest-clarification-create', 'topicJid', contestJid)),
  });
  return connect(mapStateToProps, mapDispatchToProps)(ContestClarificationCreateDialog);
}

export default createContestClarificationCreateDialog(injectedContestClarificationActions);
