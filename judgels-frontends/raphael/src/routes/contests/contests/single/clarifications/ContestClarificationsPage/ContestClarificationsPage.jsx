import { push } from 'connected-react-router';
import { Component } from 'react';
import { parse, stringify } from 'query-string';
import { connect } from 'react-redux';

import Pagination from '../../../../../../components/Pagination/Pagination';
import { ClarificationFilterWidget } from '../../../../../../components/ClarificationFilterWidget/ClarificationFilterWidget';
import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { withBreadcrumb } from '../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { selectMaybeUserJid } from '../../../../../../modules/session/sessionSelectors';
import { selectStatementLanguage } from '../../../../../../modules/webPrefs/webPrefsSelectors';
import { ContestClarificationCard } from '../ContestClarificationCard/ContestClarificationCard';
import { ContestClarificationCreateDialog } from '../ContestClarificationCreateDialog/ContestClarificationCreateDialog';
import { selectContest } from '../../../modules/contestSelectors';
import { askDesktopNotificationPermission } from '../../../../../../modules/notification/notification';
import * as contestClarificationActions from '../modules/contestClarificationActions';

class ContestClarificationsPage extends Component {
  static PAGE_SIZE = 20;

  state;

  constructor(props) {
    super(props);

    const queries = parse(this.props.location.search);
    const status = queries.status;

    this.state = {
      response: undefined,
      lastRefreshClarificationsTime: 0,
      openAnswerBoxClarification: undefined,
      isAnswerBoxLoading: false,
      filter: { status },
      isFilterLoading: false,
    };
  }

  componentDidMount() {
    askDesktopNotificationPermission();
  }

  componentDidUpdate() {
    const queries = parse(this.props.location.search);
    const status = queries.status;

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
        {this.renderClarifications()}
        {this.renderPagination()}
      </ContentCard>
    );
  }

  refreshClarifications = async (status, page) => {
    const response = await this.props.onGetClarifications(
      this.props.contest.jid,
      status,
      this.props.statementLanguage,
      page
    );
    this.setState({ response, isAnswerBoxLoading: false, isFilterLoading: false });
    return response.data;
  };

  renderClarifications = () => {
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

  renderPagination = () => {
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

  toggleAnswerBox = clarification => {
    this.setState({ openAnswerBoxClarification: clarification });
  };

  onChangePage = async nextPage => {
    const { filter } = this.state;
    const { status } = filter;
    const data = await this.refreshClarifications(status, nextPage);
    return data.totalCount;
  };

  renderCreateDialog = () => {
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

  createClarification = async (contestJid, data) => {
    await this.props.onCreateClarification(contestJid, data);
    this.setState({ lastRefreshClarificationsTime: new Date().getTime() });
  };

  answerClarification = async (contestJid, clarificationJid, data) => {
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

  renderFilterWidget = () => {
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

  onFilter = async filter => {
    this.props.onAppendRoute(filter);
  };
}

const mapStateToProps = state => ({
  userJid: selectMaybeUserJid(state),
  contest: selectContest(state),
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
