import { Component } from 'react';
import { connect } from 'react-redux';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../components/Pagination/Pagination';
import { selectContest } from '../../../modules/contestSelectors';
import { ContestSupervisorAddDialog } from '../ContestSupervisorAddDialog/ContestSupervisorAddDialog';
import { ContestSupervisorRemoveDialog } from '../ContestSupervisorRemoveDialog/ContestSupervisorRemoveDialog';
import { ContestSupervisorsTable } from '../ContestSupervisorsTable/ContestSupervisorsTable';

import * as contestSupervisorActions from '../../modules/contestSupervisorActions';

import './ContestSupervisorsPage.scss';

class ContestSupervisorsPage extends Component {
  static PAGE_SIZE = 250;

  state = {
    response: undefined,
    lastRefreshSupervisorsTime: 0,
  };

  render() {
    return (
      <ContentCard>
        <h3>Supervisors</h3>
        <hr />
        {this.renderAddRemoveDialogs()}
        {this.renderSupervisors()}
        {this.renderPagination()}
      </ContentCard>
    );
  }

  renderSupervisors = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: supervisors, profilesMap } = response;
    if (supervisors.page.length === 0) {
      return (
        <p>
          <small>No supervisors.</small>
        </p>
      );
    }

    return <ContestSupervisorsTable supervisors={supervisors.page} profilesMap={profilesMap} />;
  };

  renderPagination = () => {
    const { lastRefreshSupervisorsTime } = this.state;

    return (
      <Pagination
        key={lastRefreshSupervisorsTime}
        pageSize={ContestSupervisorsPage.PAGE_SIZE}
        onChangePage={this.onChangePage}
      />
    );
  };

  onChangePage = async nextPage => {
    const data = await this.refreshSupervisors(nextPage);
    return data.totalCount;
  };

  refreshSupervisors = async page => {
    const response = await this.props.onGetSupervisors(this.props.contest.jid, page);
    this.setState({ response });
    return response.data;
  };

  renderAddRemoveDialogs = () => {
    const { response } = this.state;
    if (!response) {
      return null;
    }
    return (
      <div className="content-card__header">
        <ContestSupervisorAddDialog contest={this.props.contest} onUpsertSupervisors={this.upsertSupervisors} />
        <ContestSupervisorRemoveDialog contest={this.props.contest} onDeleteSupervisors={this.deleteSupervisors} />
        <div className="clearfix" />
      </div>
    );
  };

  upsertSupervisors = async (contestJid, data) => {
    const response = await this.props.onUpsertSupervisors(contestJid, data);
    this.setState({ lastRefreshSupervisorsTime: new Date().getTime() });
    return response;
  };

  deleteSupervisors = async (contestJid, data) => {
    const response = await this.props.onDeleteSupervisors(contestJid, data);
    this.setState({ lastRefreshSupervisorsTime: new Date().getTime() });
    return response;
  };
}

const mapStateToProps = state => ({
  contest: selectContest(state),
});

const mapDispatchToProps = {
  onGetSupervisors: contestSupervisorActions.getSupervisors,
  onUpsertSupervisors: contestSupervisorActions.upsertSupervisors,
  onDeleteSupervisors: contestSupervisorActions.deleteSupervisors,
};

export default connect(mapStateToProps, mapDispatchToProps)(ContestSupervisorsPage);
