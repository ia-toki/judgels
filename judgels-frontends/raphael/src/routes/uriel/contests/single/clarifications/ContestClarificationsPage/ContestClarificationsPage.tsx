import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import Pagination from 'components/Pagination/Pagination';
import { ContentCard } from 'components/ContentCard/ContentCard';
import { LoadingState } from 'components/LoadingState/LoadingState';
import { Page } from 'modules/api/pagination';
import { ProfilesMap } from 'modules/api/jophiel/profile';
import { Contest } from 'modules/api/uriel/contest';
import { ContestClarificationConfig, ContestClarificationData } from 'modules/api/uriel/contestClarification';
import { ContestClarification, ContestClarificationsResponse } from 'modules/api/uriel/contestClarification';
import { AppState } from 'modules/store';
import { selectMaybeUserJid } from 'modules/session/sessionSelectors';
import { selectStatementLanguage } from 'modules/webPrefs/webPrefsSelectors';

import { ContestClarificationCard } from '../ContestClarificationCard/ContestClarificationCard';
import {
  ContestClarificationCreateDialog,
  ContestClarificationCreateDialogProps,
} from '../ContestClarificationCreateDialog/ContestClarificationCreateDialog';
import { selectContest } from '../../../modules/contestSelectors';
import { contestClarificationActions as injectedContestClarificationActions } from '../modules/contestClarificationActions';

import './ContestClarificationsPage.css';

export interface ContestClarificationsPageProps {
  userJid: string;
  contest: Contest;
  statementLanguage: string;
  onGetClarifications: (contestJid: string, language?: string, page?: number) => Promise<ContestClarificationsResponse>;
  onGetClarificationConfig: (contestJid: string, language: string) => Promise<ContestClarificationConfig>;
  onCreateClarification: (contestJid: string, data: ContestClarificationData) => void;
}

interface ContestClarificationsPageState {
  clarifications?: Page<ContestClarification>;
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
    const { data, profilesMap, problemAliasesMap, problemNamesMap } = await this.props.onGetClarifications(
      this.props.contest.jid,
      this.props.statementLanguage,
      page
    );
    this.setState({ clarifications: data, profilesMap, problemAliasesMap, problemNamesMap });
    return data;
  };

  private renderClarifications = () => {
    const { clarifications, profilesMap, problemAliasesMap, problemNamesMap } = this.state;
    if (!clarifications) {
      return <LoadingState />;
    }

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

  private renderPagination = () => {
    // updates pagination when a new clarification is created
    const key = this.state.clarifications ? this.state.clarifications.totalData : 0;

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
    const props: ContestClarificationCreateDialogProps = {
      contest: this.props.contest,
      statementLanguage: this.props.statementLanguage,
      onGetClarificationConfig: this.props.onGetClarificationConfig,
      onCreateClarification: this.props.onCreateClarification,
      onRefreshClarifications: this.refreshClarifications,
    };
    return <ContestClarificationCreateDialog {...props} />;
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
    onGetClarificationConfig: contestClarificationActions.getClarificationConfig,
    onCreateClarification: contestClarificationActions.createClarification,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestClarificationsPage));
}

export default createContestClarificationsPage(injectedContestClarificationActions);
