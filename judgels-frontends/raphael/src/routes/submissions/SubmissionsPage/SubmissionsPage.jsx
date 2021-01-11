import { push } from 'connected-react-router';
import { parse, stringify } from 'query-string';
import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { LoadingState } from '../../../components/LoadingState/LoadingState';
import Pagination from '../../../components/Pagination/Pagination';
import SubmissionUserFilter from '../../../components/SubmissionUserFilter/SubmissionUserFilter';
import { SubmissionImageDialog } from '../../../components/SubmissionImageDialog/SubmissionImageDialog';
import { SubmissionsTable } from '../SubmissionsTable/SubmissionsTable';
import { selectMaybeUserJid, selectMaybeUsername } from '../../../modules/session/sessionSelectors';
import * as submissionActions from '../modules/submissionActions';

export class SubmissionsPage extends Component {
  static PAGE_SIZE = 20;

  state = {
    response: undefined,
    isDialogOpen: false,
    imageUrl: undefined,
    dialogTitle: '',
  };

  render() {
    return (
      <>
        {this.renderUserFilter()}
        <div className="clearfix" />
        {this.renderSubmissions()}
        {this.renderPagination()}
        <SubmissionImageDialog
          isOpen={this.state.isDialogOpen}
          onClose={this.toggleDialog}
          title={this.state.dialogTitle}
          imageUrl={this.state.imageUrl}
        />
      </>
    );
  }

  renderUserFilter = () => {
    return this.props.userJid && <SubmissionUserFilter />;
  };

  isUserFilterMine = () => {
    return (this.props.location.pathname + '/').includes('/mine/');
  };

  renderSubmissions = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const {
      data: submissions,
      config,
      profilesMap,
      problemAliasesMap,
      problemNamesMap,
      containerNamesMap,
      containerPathsMap,
    } = response;
    if (submissions.totalCount === 0) {
      return (
        <p>
          <small>No submissions.</small>
        </p>
      );
    }

    return (
      <SubmissionsTable
        submissions={submissions.page}
        userJid={this.props.userJid}
        canManage={config.canManage}
        profilesMap={profilesMap}
        problemAliasesMap={problemAliasesMap}
        problemNamesMap={problemNamesMap}
        containerNamesMap={containerNamesMap}
        containerPathsMap={containerPathsMap}
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
        pageSize={SubmissionsPage.PAGE_SIZE}
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
    const response = await this.props.onGetProgrammingSubmissions(undefined, username, undefined, page);
    this.setState({ response });
    return response.data;
  };

  onRegrade = async submissionJid => {
    await this.props.onRegrade(submissionJid);
    const queries = parse(this.props.location.search);
    await this.refreshSubmissions(queries.page);
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
});

const mapDispatchToProps = {
  onGetProgrammingSubmissions: submissionActions.getSubmissions,
  onRegrade: submissionActions.regradeSubmission,
  onAppendRoute: queries => push({ search: stringify(queries) }),
  onRegrade: submissionActions.regradeSubmission,
  onGetSubmissionSourceImage: submissionActions.getSubmissionSourceImage,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(SubmissionsPage));
