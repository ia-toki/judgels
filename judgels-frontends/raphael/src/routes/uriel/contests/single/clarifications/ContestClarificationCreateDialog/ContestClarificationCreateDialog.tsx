import { Button, Callout, Dialog, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';

import ContestClarificationCreateForm, {
  ContestClarificationCreateFormData,
} from '../ContestClarificationCreateForm/ContestClarificationCreateForm';
import { ContestClarificationConfig, ContestClarificationData } from 'modules/api/uriel/contestClarification';
import { selectStatementLanguage } from 'modules/webPrefs/webPrefsSelectors';
import { AppState } from 'modules/store';
import { Contest } from 'modules/api/uriel/contest';

import { selectContest } from '../../../modules/contestSelectors';
import { contestClarificationActions as injectedContestClarificationActions } from '../modules/contestClarificationActions';

export interface ContestClarificationCreateDialogProps {
  onRefreshClarifications: () => Promise<void>;
}

export interface ContestClarificationCreateDialogConnectedProps {
  contest: Contest;
  statementLanguage: string;
  onGetClarificationConfig: (contestJid: string, language: string) => Promise<ContestClarificationConfig>;
  onCreateClarification: (contestJid: string, data: ContestClarificationData) => void;
}

interface ContestClarificationCreateDialogState {
  config?: ContestClarificationConfig;
  isDialogOpen?: boolean;
}

class ContestClarificationCreateDialog extends React.Component<
  ContestClarificationCreateDialogProps & ContestClarificationCreateDialogConnectedProps,
  ContestClarificationCreateDialogState
> {
  state: ContestClarificationCreateDialogState = {};

  async componentDidMount() {
    const config = await this.props.onGetClarificationConfig(this.props.contest.jid, this.props.statementLanguage);
    this.setState({ config });
  }

  render() {
    const { config } = this.state;
    if (!config) {
      return null;
    }

    return (
      <div className="content-card__section">
        {this.renderButton(config)}
        {this.renderDialog(config)}
      </div>
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
        New clarification
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
      initialValues: {
        topicJid: this.props.contest.jid,
      },
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
      <div className="bp3-dialog-body">{fields}</div>
      <div className="bp3-dialog-footer">
        <div className="bp3-dialog-footer-actions">
          <Button text="Cancel" onClick={this.toggleDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  private createClarification = async (data: ContestClarificationCreateFormData) => {
    await this.props.onCreateClarification(this.props.contest.jid, data);
    await this.props.onRefreshClarifications();
    this.setState({ isDialogOpen: false });
  };
}

function createContestClarificationCreateDialog(contestClarificationActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
    statementLanguage: selectStatementLanguage(state),
  });

  const mapDispatchToProps = {
    onGetClarificationConfig: contestClarificationActions.getClarificationConfig,
    onCreateClarification: contestClarificationActions.createClarification,
  };
  return connect(mapStateToProps, mapDispatchToProps)(ContestClarificationCreateDialog);
}

export default createContestClarificationCreateDialog(injectedContestClarificationActions);
