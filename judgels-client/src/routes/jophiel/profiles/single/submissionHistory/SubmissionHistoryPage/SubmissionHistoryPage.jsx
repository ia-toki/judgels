import { Component } from 'react';
import { connect } from 'react-redux';

import { Card } from '../../../../../../components/Card/Card';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../components/Pagination/Pagination';
import { selectMaybeUserJid } from '../../../../../../modules/session/sessionSelectors';
import { selectUsername } from '../../../../modules/profileSelectors';
import { SubmissionsTable } from '../SubmissionsTable/SubmissionsTable';

import * as profileActions from '../../modules/profileActions';

class SubmissionHistoryPage extends Component {
  state = {
    response: undefined,
  };

  render() {
    return (
      <Card title="Submission history">
        {this.renderSubmissions()}
        {this.renderPagination()}
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
        canManage={config.canManage}
        problemAliasesMap={problemAliasesMap}
        problemNamesMap={problemNamesMap}
        containerNamesMap={containerNamesMap}
        containerPathsMap={containerPathsMap}
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
}

const mapStateToProps = state => ({
  username: selectUsername(state),
  sessionUserJid: selectMaybeUserJid(state),
});
const mapDispatchToProps = {
  onGetSubmissions: profileActions.getSubmissions,
};

export default connect(mapStateToProps, mapDispatchToProps)(SubmissionHistoryPage);
