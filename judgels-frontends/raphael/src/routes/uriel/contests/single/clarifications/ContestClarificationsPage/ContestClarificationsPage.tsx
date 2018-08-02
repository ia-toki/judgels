import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContentCard } from 'components/ContentCard/ContentCard';
import { LoadingState } from 'components/LoadingState/LoadingState';
import { ContestClarificationCard } from '../ContestClarificationCard/ContestClarificationCard';
import ContestClarificationCreateDialog from '../ContestClarificationCard/ContestClarificationCreateDialog/ContestClarificationCreateDialog';
import { Contest } from 'modules/api/uriel/contest';
import { ContestClarification, ContestClarificationsResponse } from 'modules/api/uriel/contestClarification';
import { AppState } from 'modules/store';

import { selectStatementLanguage } from 'modules/webPrefs/webPrefsSelectors';
import { selectContest } from '../../../modules/contestSelectors';
import { contestClarificationActions as injectedContestClarificationActions } from '../modules/contestClarificationActions';

import './ContestClarificationsPage.css';

export interface ContestClarificationsPageProps {
  contest: Contest;
  statementLanguage: string;
  onGetMyClarifications: (contestJid: string, language: string) => Promise<ContestClarificationsResponse>;
}

interface ContestClarificationsPageState {
  clarifications?: ContestClarification[];
  problemJids?: string[];
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
      </ContentCard>
    );
  }

  private refreshClarifications = async () => {
    const { data, problemAliasesMap, problemNamesMap } = await this.props.onGetMyClarifications(
      this.props.contest.jid,
      this.props.statementLanguage
    );
    this.setState({ clarifications: data, problemAliasesMap, problemNamesMap });
  };

  private renderClarifications = () => {
    const { clarifications, problemAliasesMap, problemNamesMap } = this.state;
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

    return clarifications.map(clarification => (
      <div className="content-card__section" key={clarification.jid}>
        <ContestClarificationCard
          clarification={clarification}
          problemAlias={problemAliasesMap![clarification.topicJid]}
          problemName={problemNamesMap![clarification.topicJid]}
        />
      </div>
    ));
  };

  private renderCreateDialog = () => {
    const props = {
      onRefreshClarifications: this.refreshClarifications,
    };
    return (
      <div className="content-card__section">
        <ContestClarificationCreateDialog {...props} />
      </div>
    );
  };
}

function createContestClarificationsPage(contestClarificationActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
    statementLanguage: selectStatementLanguage(state),
  });

  const mapDispatchToProps = {
    onGetMyClarifications: contestClarificationActions.getMyClarifications,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestClarificationsPage));
}

export default createContestClarificationsPage(injectedContestClarificationActions);
