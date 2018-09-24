import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContentCard } from 'components/ContentCard/ContentCard';
import { LoadingState } from 'components/LoadingState/LoadingState';
import { ProfilesMap } from 'modules/api/jophiel/profile';
import { Contest } from 'modules/api/uriel/contest';
import { ContestClarification, ContestClarificationsResponse } from 'modules/api/uriel/contestClarification';
import { AppState } from 'modules/store';
import { selectMaybeUserJid } from 'modules/session/sessionSelectors';
import { selectStatementLanguage } from 'modules/webPrefs/webPrefsSelectors';

import { ContestClarificationCard } from '../ContestClarificationCard/ContestClarificationCard';
import ContestClarificationAnswerCreateDialog from '../ContestClarificationAnswerCreateDialog/ContestClarificationAnswerCreateDialog';
import ContestClarificationCreateDialog from '../ContestClarificationCreateDialog/ContestClarificationCreateDialog';
import { selectContest } from '../../../modules/contestSelectors';
import { contestClarificationActions as injectedContestClarificationActions } from '../modules/contestClarificationActions';

import './ContestClarificationsPage.css';

export interface ContestClarificationsPageProps {
  userJid: string;
  contest: Contest;
  statementLanguage: string;
  onGetClarifications: (contestJid: string, language: string) => Promise<ContestClarificationsResponse>;
}

interface ContestClarificationsPageState {
  clarifications?: ContestClarification[];
  problemJids?: string[];
  profilesMap?: ProfilesMap;
  problemAliasesMap?: { [problemJid: string]: string };
  problemNamesMap?: { [problemJid: string]: string };
  isCreateDialogOpen?: boolean;
}

class ContestClarificationsPage extends React.Component<
  ContestClarificationsPageProps,
  ContestClarificationsPageState
> {
  state: ContestClarificationsPageState = {};

  async componentDidMount() {
    await this.refreshClarifications();
  }

  render() {
    return (
      <ContentCard>
        <h3>Clarifications</h3>
        <hr />
        {this.renderCreateDialog()}
        {this.renderClarifications()}
        {this.renderCreateAnswerDialog()}
      </ContentCard>
    );
  }

  private refreshClarifications = async () => {
    const { data, profilesMap, problemAliasesMap, problemNamesMap } = await this.props.onGetClarifications(
      this.props.contest.jid,
      this.props.statementLanguage
    );
    this.setState({ clarifications: data, profilesMap, problemAliasesMap, problemNamesMap });
  };

  private renderClarifications = () => {
    const { clarifications, profilesMap, problemAliasesMap, problemNamesMap } = this.state;
    if (!clarifications) {
      return <LoadingState />;
    }

    if (clarifications.length === 0) {
      return (
        <p>
          <small>No clarifications.</small>
        </p>
      );
    }

    return clarifications.map(clarification => {
      const isSupervisor = this.props.userJid !== clarification.userJid;

      return (
        <div className="content-card__section" key={clarification.jid}>
          <ContestClarificationCard
            clarification={clarification}
            askerProfile={isSupervisor ? profilesMap![clarification.userJid] : undefined}
            answererProfile={
              isSupervisor && clarification.answererJid ? profilesMap![clarification.answererJid] : undefined
            }
            problemAlias={problemAliasesMap![clarification.topicJid]}
            problemName={problemNamesMap![clarification.topicJid]}
          />
        </div>
      );
    });
  };

  private renderCreateDialog = () => {
    const props = {
      onRefreshClarifications: this.refreshClarifications,
    };
    return <ContestClarificationCreateDialog {...props} />;
  };

  private renderCreateAnswerDialog = () => {
    const props = {
        onRefreshClarifications: this.refreshClarifications,
    };
    return <ContestClarificationAnswerCreateDialog {...props} />;
  };
}

function createContestClarificationsPage(contestClarificationActions) {
  const mapStateToProps = (state: AppState) => ({
    userJid: selectMaybeUserJid(state),
    contest: selectContest(state)!,
    statementLanguage: selectStatementLanguage(state),
  });

  const mapDispatchToProps = {
    onGetClarifications: contestClarificationActions.getClarifications,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestClarificationsPage));
}

export default createContestClarificationsPage(injectedContestClarificationActions);
