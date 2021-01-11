import { Component } from 'react';
import { connect } from 'react-redux';

import Pagination from '../../../../../../components/Pagination/Pagination';
import { Card } from '../../../../../../components/Card/Card';
import { SubmissionImageDialog } from '../../../../../../components/SubmissionImageDialog/SubmissionImageDialog';
import { SubmissionsTable } from '../SubmissionsTable/SubmissionsTable';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { selectUsername } from '../../../../modules/profileSelectors';
import { selectMaybeUserJid } from '../../../../../../modules/session/sessionSelectors';
import * as profileActions from '../../modules/profileActions';

class SubmissionHistoryPage extends Component {
  state = {
    response: undefined,
    isDialogOpen: false,
    imageUrl: undefined,
    dialogTitle: '',
  };

  render() {
    return (
      <Card title="Submission history">
        {this.renderSubmissions()}
        {this.renderPagination()}
        <SubmissionImageDialog
          isOpen={this.state.isDialogOpen}
          onClose={this.toggleDialog}
          title={this.state.dialogTitle}
          imageUrl={this.state.imageUrl}
        />
      </Card>
    );
  }

  renderSubmissions = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const {
      data: submissions,
      config,
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
        userJid={this.props.sessionUserJid}
        canManage={config.canManage}
        problemAliasesMap={problemAliasesMap}
        problemNamesMap={problemNamesMap}
        containerNamesMap={containerNamesMap}
        containerPathsMap={containerPathsMap}
        onOpenSubmissionImage={this.onOpenSubmissionImage}
      />
    );
  };

  renderPagination = () => {
    return <Pagination key={1} pageSize={20} onChangePage={this.onChangePage} />;
  };

  onChangePage = async nextPage => {
    const data = await this.refreshSubmissions(nextPage);
    return data.totalCount;
  };

  refreshSubmissions = async page => {
    const response = await this.props.onGetSubmissions(this.props.username, page);
    this.setState({ response });
    return response.data;
  };

  toggleDialog = () => {
    this.setState({ isDialogOpen: !this.state.isDialogOpen });
  };

  onOpenSubmissionImage = async (submissionJid, submissionId) => {
    const imageUrl = await this.props.onGetSubmissionSourceImage(submissionJid);
    const dialogTitle = `Submission #${submissionId} (${this.props.username})`;
    this.setState({ imageUrl, dialogTitle });
    this.toggleDialog();
  };
}

const mapStateToProps = state => ({
  username: selectUsername(state),
  sessionUserJid: selectMaybeUserJid(state),
});
const mapDispatchToProps = {
  onGetSubmissions: profileActions.getSubmissions,
  onGetSubmissionSourceImage: profileActions.getSubmissionSourceImage,
};

export default connect(mapStateToProps, mapDispatchToProps)(SubmissionHistoryPage);
