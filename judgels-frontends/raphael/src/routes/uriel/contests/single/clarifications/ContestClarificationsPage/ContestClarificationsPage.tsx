import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import Pagination from 'components/Pagination/Pagination';
import { ContentCard } from 'components/ContentCard/ContentCard';
import { LoadingState } from 'components/LoadingState/LoadingState';
import { Contest } from 'modules/api/uriel/contest';
import { ContestClarificationData } from 'modules/api/uriel/contestClarification';
import { ContestClarificationsResponse } from 'modules/api/uriel/contestClarification';
import { AppState } from 'modules/store';
import { selectMaybeUserJid } from 'modules/session/sessionSelectors';
import { selectStatementLanguage } from 'modules/webPrefs/webPrefsSelectors';

import { ContestClarificationCard } from '../ContestClarificationCard/ContestClarificationCard';
import { ContestClarificationCreateDialog } from '../ContestClarificationCreateDialog/ContestClarificationCreateDialog';
import { selectContest } from '../../../modules/contestSelectors';
import { contestClarificationActions as injectedContestClarificationActions } from '../modules/contestClarificationActions';

import './ContestClarificationsPage.css';

export interface ContestClarificationsPageProps {
  userJid: string;
  contest: Contest;
  statementLanguage: string;
  onGetClarifications: (contestJid: string, language?: string, page?: number) => Promise<ContestClarificationsResponse>;
  onCreateClarification: (contestJid: string, data: ContestClarificationData) => void;
}

interface ContestClarificationsPageState {
  response?: ContestClarificationsResponse;
  lastCreateClarificationTime?: number;
}

class ContestClarificationsPage extends React.Component<
  ContestClarificationsPageProps,
  ContestClarificationsPageState
> {
  private static PAGE_SIZE = 20;

  state: ContestClarificationsPageState = {};

  render() {
    return (
      <ContentCard>
        <h3>Clarifications</h3>
        <hr />
        {this.renderCreateDialog()}
        {this.renderClarifications()}
        {this.renderPagination()}
      </ContentCard>
    );
  }

  private refreshClarifications = async (page?: number) => {
    const response = await this.props.onGetClarifications(this.props.contest.jid, this.props.statementLanguage, page);
    this.setState({ response });
    return response.data;
  };

  private renderClarifications = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: clarifications, profilesMap, problemAliasesMap, problemNamesMap } = response;
    if (clarifications.data.length === 0) {
      return (
        <p>
          <small>No clarifications.</small>
        </p>
      );
    }

    return clarifications.data.map(clarification => {
      const isSupervisor = this.props.userJid !== clarification.userJid;

      return (
        <div className="content-card__section" key={clarification.jid}>
          <ContestClarificationCard
            clarification={clarification}
            askerProfile={isSupervisor ? profilesMap[clarification.userJid] : undefined}
            answererProfile={
              isSupervisor && clarification.answererJid ? profilesMap[clarification.answererJid] : undefined
            }
            problemAlias={problemAliasesMap[clarification.topicJid]}
            problemName={problemNamesMap[clarification.topicJid]}
          />
        </div>
      );
    });
  };

  private renderPagination = () => {
    // updates pagination when a new clarification is created
    const { lastCreateClarificationTime } = this.state;
    const key = lastCreateClarificationTime || 0;

    return (
      <Pagination
        key={key}
        currentPage={1}
        pageSize={ContestClarificationsPage.PAGE_SIZE}
        onChangePage={this.onChangePage}
      />
    );
  };

  private onChangePage = async (nextPage: number) => {
    const data = await this.refreshClarifications(nextPage);
    return data.totalData;
  };

  private renderCreateDialog = () => {
    const { response } = this.state;
    if (!response) {
      return null;
    }
    const { config } = response;
    if (!config.isAllowedToCreateClarification) {
      return null;
    }

    return (
      <ContestClarificationCreateDialog
        contest={this.props.contest}
        problemJids={config.problemJids}
        problemAliasesMap={response.problemAliasesMap}
        problemNamesMap={response.problemNamesMap}
        statementLanguage={this.props.statementLanguage}
        onCreateClarification={this.createClarification}
      />
    );
  };
  private createClarification = async (contestJid, data) => {
    await this.props.onCreateClarification(contestJid, data);
    this.setState({ lastCreateClarificationTime: new Date().getTime() });
  };
}

export function createContestClarificationsPage(contestClarificationActions) {
  const mapStateToProps = (state: AppState) => ({
    userJid: selectMaybeUserJid(state),
    contest: selectContest(state)!,
    statementLanguage: selectStatementLanguage(state),
  });

  const mapDispatchToProps = {
    onGetClarifications: contestClarificationActions.getClarifications,
    onCreateClarification: contestClarificationActions.createClarification,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestClarificationsPage));
}

export default createContestClarificationsPage(injectedContestClarificationActions);
