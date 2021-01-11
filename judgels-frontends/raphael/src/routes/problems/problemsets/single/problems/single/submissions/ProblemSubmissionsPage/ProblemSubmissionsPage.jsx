import { push } from 'connected-react-router';
import { parse, stringify } from 'query-string';
import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { reallyConfirm } from '../../../../../../../../utils/confirmation';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import { RegradeAllButton } from '../../../../../../../../components/RegradeAllButton/RegradeAllButton';
import Pagination from '../../../../../../../../components/Pagination/Pagination';
import SubmissionUserFilter from '../../../../../../../../components/SubmissionUserFilter/SubmissionUserFilter';
import { SubmissionImageDialog } from '../../../../../../../../components/SubmissionImageDialog/SubmissionImageDialog';
import { ProblemSubmissionsTable } from '../ProblemSubmissionsTable/ProblemSubmissionsTable';
import { selectMaybeUserJid, selectMaybeUsername } from '../../../../../../../../modules/session/sessionSelectors';
import { selectProblemSet } from '../../../../../modules/problemSetSelectors';
import { selectProblemSetProblem } from '../../../modules/problemSetProblemSelectors';
import * as problemSetSubmissionActions from '../modules/problemSetSubmissionActions';

export class ProblemSubmissionsPage extends Component {
  static PAGE_SIZE = 20;

  state = {
    response: undefined,
    isDialogOpen: false,
    imageUrl: undefined,
    dialogTitle: '',
  };

  render() {
    return (
      <ContentCard>
        <h3>Submissions</h3>
        <hr />
        {this.renderUserFilter()}
        {this.renderRegradeAllButton()}
        <div className="clearfix" />
        {this.renderSubmissions()}
        {this.renderPagination()}
        <SubmissionImageDialog
          isOpen={this.state.isDialogOpen}
          onClose={this.toggleDialog}
          title={this.state.dialogTitle}
          imageUrl={this.state.imageUrl}
        />
      </ContentCard>
    );
  }

  renderUserFilter = () => {
    return this.props.userJid && <SubmissionUserFilter />;
  };

  isUserFilterMine = () => {
    return (this.props.location.pathname + '/').includes('/mine/');
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
        userJid={this.props.userJid}
        canManage={config.canManage}
        profilesMap={profilesMap}
        problemAliasesMap={problemAliasesMap}
        onRegrade={this.onRegrade}
        onOpenSubmissionImage={this.onOpenSubmissionImage}
      />
    );
  };

  renderPagination = () => {
    return (
      <Pagination
        key={'' + this.isUserFilterMine()}
        currentPage={1}
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

  toggleDialog = () => {
    this.setState({ isDialogOpen: !this.state.isDialogOpen });
  };

  onOpenSubmissionImage = async (submissionJid, submissionId, username) => {
    const imageUrl = await this.props.onGetSubmissionSourceImage(submissionJid);
    const dialogTitle = `Submission #${submissionId} (${username})`;
    this.setState({ imageUrl, dialogTitle });
    this.toggleDialog();
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
  onGetSubmissionSourceImage: problemSetSubmissionActions.getSubmissionSourceImage,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ProblemSubmissionsPage));
