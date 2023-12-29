import { push } from 'connected-react-router';
import { parse, stringify } from 'query-string';
import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../../../components/Pagination/Pagination';
import { RegradeAllButton } from '../../../../../../../../components/RegradeAllButton/RegradeAllButton';
import SubmissionUserFilter from '../../../../../../../../components/SubmissionUserFilter/SubmissionUserFilter';
import { selectMaybeUserJid, selectMaybeUsername } from '../../../../../../../../modules/session/sessionSelectors';
import { reallyConfirm } from '../../../../../../../../utils/confirmation';
import { selectProblemSet } from '../../../../../modules/problemSetSelectors';
import { selectProblemSetProblem } from '../../../modules/problemSetProblemSelectors';
import { ProblemSubmissionsTable } from '../ProblemSubmissionsTable/ProblemSubmissionsTable';

import * as problemSetSubmissionActions from '../modules/problemSetSubmissionActions';

export class ProblemSubmissionsPage extends Component {
  static PAGE_SIZE = 20;

  state = {
    response: undefined,
  };

  render() {
    return (
      <ContentCard>
        <h3>Submissions</h3>
        <hr />
        {this.renderUserFilter()}
        {this.renderHeader()}
        {this.renderSubmissions()}
        {this.renderPagination()}
      </ContentCard>
    );
  }

  renderUserFilter = () => {
    return this.props.userJid && <SubmissionUserFilter />;
  };

  isUserFilterMine = () => {
    return (this.props.location.pathname + '/').includes('/mine/');
  };

  renderHeader = () => {
    return <div className="content-card__header">{this.renderRegradeAllButton()}</div>;
  };

  renderRegradeAllButton = () => {
    if (!this.state.response || !this.state.response.config.canManage) {
      return null;
    }
    return <RegradeAllButton onRegradeAll={this.onRegradeAll} />;
  };

  renderSubmissions = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: submissions, config, profilesMap, problemAliasesMap } = response;
    if (submissions.totalCount === 0) {
      return (
        <p>
          <small>No submissions.</small>
        </p>
      );
    }

    return (
      <ProblemSubmissionsTable
        problemSet={this.props.problemSet}
        problem={this.props.problem}
        submissions={submissions.page}
        canManage={config.canManage}
        profilesMap={profilesMap}
        problemAliasesMap={problemAliasesMap}
        onRegrade={this.onRegrade}
      />
    );
  };

  renderPagination = () => {
    return (
      <Pagination
        key={'' + this.isUserFilterMine()}
        pageSize={ProblemSubmissionsPage.PAGE_SIZE}
        onChangePage={this.onChangePage}
      />
    );
  };

  onChangePage = async nextPage => {
    const data = await this.refreshSubmissions(nextPage);
    return data.totalCount;
  };

  refreshSubmissions = async page => {
    const username = this.isUserFilterMine() ? this.props.username : undefined;
    const response = await this.props.onGetProgrammingSubmissions(
      undefined,
      username,
      this.props.problem.problemJid,
      page
    );
    this.setState({ response });
    return response.data;
  };

  onRegrade = async submissionJid => {
    await this.props.onRegrade(submissionJid);
    const queries = parse(this.props.location.search);
    await this.refreshSubmissions(queries.page);
  };

  onRegradeAll = async () => {
    if (reallyConfirm('Regrade all submissions in all pages?')) {
      await this.props.onRegradeAll(undefined, undefined, this.props.problem.problemJid);
      const queries = parse(this.props.location.search);
      await this.refreshSubmissions(queries.page);
    }
  };
}

const mapStateToProps = state => ({
  userJid: selectMaybeUserJid(state),
  username: selectMaybeUsername(state),
  problemSet: selectProblemSet(state),
  problem: selectProblemSetProblem(state),
});

const mapDispatchToProps = {
  onGetProgrammingSubmissions: problemSetSubmissionActions.getSubmissions,
  onRegrade: problemSetSubmissionActions.regradeSubmission,
  onRegradeAll: problemSetSubmissionActions.regradeSubmissions,
  onAppendRoute: queries => push({ search: stringify(queries) }),
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ProblemSubmissionsPage));
