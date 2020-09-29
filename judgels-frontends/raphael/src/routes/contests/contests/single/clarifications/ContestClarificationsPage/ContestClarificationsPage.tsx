import { push } from 'connected-react-router';
import * as React from 'react';
import { parse, stringify } from 'query-string';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router';

import Pagination from '../../../../../../components/Pagination/Pagination';
import { ClarificationFilterWidget } from '../../../../../../components/ClarificationFilterWidget/ClarificationFilterWidget';
import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { withBreadcrumb } from '../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
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
import * as contestClarificationActions from '../modules/contestClarificationActions';

import './ContestClarificationsPage.css';

export interface ContestClarificationsPageProps extends RouteComponentProps<{}> {
  userJid: string;
  contest: Contest;
  statementLanguage: string;
  onGetClarifications: (
    contestJid: string,
    status?: string,
    language?: string,
    page?: number
  ) => Promise<ContestClarificationsResponse>;
  onCreateClarification: (contestJid: string, data: ContestClarificationData) => void;
  onAnswerClarification: (contestJid: string, clarificationJid: string, answer: string) => void;
  onAppendRoute: (queries) => any;
}

interface ContestClarificationsFilter {
  status?: string;
}

interface ContestClarificationsPageState {
  response?: ContestClarificationsResponse;
  lastRefreshClarificationsTime?: number;
  openAnswerBoxClarification?: ContestClarification;
  isAnswerBoxLoading?: boolean;
  filter?: ContestClarificationsFilter;
  isFilterLoading?: boolean;
}

class ContestClarificationsPage extends React.PureComponent<
  ContestClarificationsPageProps,
  ContestClarificationsPageState
> {
  private static PAGE_SIZE = 20;

  state: ContestClarificationsPageState = {};

  constructor(props) {
    super(props);

    const queries = parse(this.props.location.search);
    const status = queries.status as string;

    this.state = { filter: { status } };
  }

  componentDidUpdate() {
    const queries = parse(this.props.location.search);
    const status = queries.status as string;

    if (status !== this.state.filter.status) {
      this.setState({
        filter: { status },
        isFilterLoading: true,
        lastRefreshClarificationsTime: new Date().getTime(),
      });
    }
  }

  render() {
    return (
      <ContentCard>
        <h3>Clarifications</h3>
        <hr />
        {this.renderCreateDialog()}
        {this.renderFilterWidget()}
        <div className="clearfix" />
        {this.renderClarifications()}
        {this.renderPagination()}
      </ContentCard>
    );
  }

  private refreshClarifications = async (status?: string, page?: number) => {
    const response = await this.props.onGetClarifications(
      this.props.contest.jid,
      status,
      this.props.statementLanguage,
      page
    );
    this.setState({ response, isAnswerBoxLoading: false, isFilterLoading: false });
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
    const { filter } = this.state;
    const { status } = filter;
    const data = await this.refreshClarifications(status, nextPage);
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

  private renderFilterWidget = () => {
    const { response, filter, isFilterLoading } = this.state;
    if (!response) {
      return null;
    }
    const { config } = response;
    const { canSupervise } = config;
    if (!canSupervise) {
      return null;
    }

    const { status } = filter;
    return (
      <ClarificationFilterWidget
        statuses={['ASKED']}
        status={status}
        onFilter={this.onFilter}
        isLoading={!!isFilterLoading}
      />
    );
  };

  private onFilter = async filter => {
    this.props.onAppendRoute(filter);
  };
}

const mapStateToProps = (state: AppState) => ({
  userJid: selectMaybeUserJid(state),
  contest: selectContest(state)!,
  statementLanguage: selectStatementLanguage(state),
});

const mapDispatchToProps = {
  onGetClarifications: contestClarificationActions.getClarifications,
  onCreateClarification: contestClarificationActions.createClarification,
  onAnswerClarification: contestClarificationActions.answerClarification,
  onAppendRoute: queries => push({ search: stringify(queries) }),
};

export default withBreadcrumb('Clarifications')(
  connect(mapStateToProps, mapDispatchToProps)(ContestClarificationsPage)
);
