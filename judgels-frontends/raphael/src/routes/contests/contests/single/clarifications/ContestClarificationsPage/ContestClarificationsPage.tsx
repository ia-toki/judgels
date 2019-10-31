import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import Pagination from '../../../../../../components/Pagination/Pagination';
import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { Contest } from '../../../../../../modules/api/uriel/contest';
import {
  ContestClarification,
  ContestClarificationData,
} from '../../../../../../modules/api/uriel/contestClarification';
import { ContestClarificationsResponse } from '../../../../../../modules/api/uriel/contestClarification';
import { AppState } from '../../../../../../modules/store';
import { selectMaybeUserJid } from '../../../../../../modules/session/sessionSelectors';
import { selectStatementLanguage } from '../../../../../../modules/webPrefs/webPrefsSelectors';

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
  onAnswerClarification: (contestJid: string, clarificationJid: string, answer: string) => void;
}

interface ContestClarificationsPageState {
  response?: ContestClarificationsResponse;
  lastRefreshClarificationsTime?: number;
  openAnswerBoxClarification?: ContestClarification;
  isAnswerBoxLoading?: boolean;
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
    this.setState({ response, isAnswerBoxLoading: false });
    return response.data;
  };

  private renderClarifications = () => {
    const { response, openAnswerBoxClarification } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: clarifications, config, profilesMap, problemAliasesMap, problemNamesMap } = response;
    if (clarifications.page.length === 0) {
      return (
        <p>
          <small>No clarifications.</small>
        </p>
      );
    }

    const { canSupervise, canManage } = config;

    return clarifications.page.map(clarification => (
      <div className="content-card__section" key={clarification.jid}>
        <ContestClarificationCard
          contest={this.props.contest}
          clarification={clarification}
          canSupervise={canSupervise}
          canManage={canManage}
          askerProfile={canSupervise ? profilesMap[clarification.userJid] : undefined}
          answererProfile={
            canSupervise && clarification.answererJid ? profilesMap[clarification.answererJid] : undefined
          }
          problemAlias={problemAliasesMap[clarification.topicJid]}
          problemName={problemNamesMap[clarification.topicJid]}
          isAnswerBoxOpen={openAnswerBoxClarification === clarification}
          isAnswerBoxLoading={!!this.state.isAnswerBoxLoading}
          onToggleAnswerBox={this.toggleAnswerBox}
          onAnswerClarification={this.answerClarification}
        />
      </div>
    ));
  };

  private renderPagination = () => {
    // updates pagination when clarifications are refreshed
    const { lastRefreshClarificationsTime } = this.state;
    const key = lastRefreshClarificationsTime || 0;

    return (
      <Pagination
        key={key}
        currentPage={1}
        pageSize={ContestClarificationsPage.PAGE_SIZE}
        onChangePage={this.onChangePage}
      />
    );
  };

  private toggleAnswerBox = (clarification?: ContestClarification) => {
    this.setState({ openAnswerBoxClarification: clarification });
  };

  private onChangePage = async (nextPage: number) => {
    const data = await this.refreshClarifications(nextPage);
    return data.totalCount;
  };

  private renderCreateDialog = () => {
    const { response } = this.state;
    if (!response) {
      return null;
    }
    const { config } = response;
    if (!config.canCreate) {
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
    this.setState({ lastRefreshClarificationsTime: new Date().getTime() });
  };

  private answerClarification = async (contestJid, clarificationJid, data) => {
    this.setState({ isAnswerBoxLoading: true });
    try {
      await this.props.onAnswerClarification(contestJid, clarificationJid, data);
      this.setState({ lastRefreshClarificationsTime: new Date().getTime() });
      this.toggleAnswerBox();
    } catch (err) {
      // Don't close answer box yet on error
    } finally {
      this.setState({ isAnswerBoxLoading: false });
    }
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
    onAnswerClarification: contestClarificationActions.answerClarification,
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(ContestClarificationsPage));
}

export default createContestClarificationsPage(injectedContestClarificationActions);
