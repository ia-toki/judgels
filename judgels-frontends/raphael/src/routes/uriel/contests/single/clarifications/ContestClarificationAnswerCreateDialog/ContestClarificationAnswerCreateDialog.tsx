import { Button, Dialog, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';

import ContestClarificationAnswerCreateForm, {
  ContestClarificationAnswerCreateFormData,
} from '../ContestClarificationAnswerCreateForm/ContestClarificationAnswerCreateForm';
import { ContestClarificationConfig, ContestClarificationAnswerData, ContestClarification } from 'modules/api/uriel/contestClarification';
import { selectStatementLanguage } from 'modules/webPrefs/webPrefsSelectors';
import { AppState } from 'modules/store';
import { selectMaybeUserJid } from 'modules/session/sessionSelectors';
import { Contest } from 'modules/api/uriel/contest';

import { selectContest } from '../../../modules/contestSelectors';
import { contestClarificationActions as injectedContestClarificationActions } from '../modules/contestClarificationActions';

export interface ContestClarificationAnswerCreateDialogProps {
  onRefreshClarifications: () => Promise<void>;
}

export interface ContestClarificationAnswerCreateDialogConnectedProps {
  contest: Contest;
  userJid: string;
  statementLanguage: string;
  onGetClarificationConfig: (contestJid: string, language: string) => Promise<ContestClarificationConfig>;
  onCreateClarificationAnswer: (contestJid: string, data: ContestClarificationAnswerData) => Promise<ContestClarification>;
}

interface ContestClarificationAnswerCreateDialogState {
  config?: ContestClarificationConfig;
  isDialogOpen?: boolean;
}

class ContestClarificationAnswerCreateDialog extends React.Component<
  ContestClarificationAnswerCreateDialogProps & ContestClarificationAnswerCreateDialogConnectedProps,
  ContestClarificationAnswerCreateDialogState
> {
  state: ContestClarificationAnswerCreateDialogState = {};

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
      return null;
    }
    return (
      <Button intent={Intent.PRIMARY} icon="comment" onClick={this.toggleDialog} disabled={this.state.isDialogOpen}>
        Answer
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
      onSubmit: this.createClarificationAnswer,
      initialValues: {
        answererJid: this.props.userJid,
      },
    };
    return (
      <Dialog
        isOpen={this.state.isDialogOpen || false}
        onClose={this.toggleDialog}
        title="Answer clarification"
        canOutsideClickClose={false}
      >
        <ContestClarificationAnswerCreateForm {...props} />
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

  private createClarificationAnswer = async (data: ContestClarificationAnswerCreateFormData) => {
    await this.props.onCreateClarificationAnswer(this.props.contest.jid, data);
    await this.props.onRefreshClarifications();
    this.setState({ isDialogOpen: false });
  };
}

function createContestClarificationAnswerCreateDialog(contestClarificationActions) {
  const mapStateToProps = (state: AppState) => ({
    userJid: selectMaybeUserJid(state),
    contest: selectContest(state)!,
    statementLanguage: selectStatementLanguage(state),
  });

  const mapDispatchToProps = {
    onGetClarificationConfig: contestClarificationActions.getClarificationConfig,
    onCreateClarificationAnswer: contestClarificationActions.createAnswer,
  };
  return connect(mapStateToProps, mapDispatchToProps)(ContestClarificationAnswerCreateDialog);
}

export default createContestClarificationAnswerCreateDialog(injectedContestClarificationActions);
