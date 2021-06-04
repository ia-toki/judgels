import { Component } from 'react';
import { connect } from 'react-redux';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../components/Pagination/Pagination';
import { withBreadcrumb } from '../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { ContestManagersTable } from '../ContestManagersTable/ContestManagersTable';
import { ContestManagerAddDialog } from '../ContestManagerAddDialog/ContestManagerAddDialog';
import { ContestManagerRemoveDialog } from '../ContestManagerRemoveDialog/ContestManagerRemoveDialog';
import { selectContest } from '../../../modules/contestSelectors';
import * as contestManagerActions from '../modules/contestManagerActions';

import './ContestManagersPage.scss';

class ContestManagersPage extends Component {
  static PAGE_SIZE = 250;

  state = {
    response: undefined,
    lastRefreshManagersTime: 0,
  };

  render() {
    return (
      <ContentCard>
        <h3>Managers</h3>
        <hr />
        {this.renderAddRemoveDialogs()}
        {this.renderManagers()}
        {this.renderPagination()}
      </ContentCard>
    );
  }

  renderManagers = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: managers, profilesMap } = response;
    if (managers.page.length === 0) {
      return (
        <p>
          <small>No managers.</small>
        </p>
      );
    }

    return <ContestManagersTable managers={managers.page} profilesMap={profilesMap} />;
  };

  renderPagination = () => {
    // updates pagination when managers are refreshed
    const { lastRefreshManagersTime } = this.state;
    const key = lastRefreshManagersTime || 0;

    return (
      <Pagination key={key} currentPage={1} pageSize={ContestManagersPage.PAGE_SIZE} onChangePage={this.onChangePage} />
    );
  };

  onChangePage = async nextPage => {
    const data = await this.refreshManagers(nextPage);
    return data.totalCount;
  };

  refreshManagers = async page => {
    const response = await this.props.onGetManagers(this.props.contest.jid, page);
    this.setState({ response });
    return response.data;
  };

  renderAddRemoveDialogs = () => {
    const { response } = this.state;
    if (!response) {
      return null;
    }
    if (!response.config.canManage) {
      return null;
    }
    return (
      <>
        <ContestManagerAddDialog contest={this.props.contest} onUpsertManagers={this.upsertManagers} />
        <ContestManagerRemoveDialog contest={this.props.contest} onDeleteManagers={this.deleteManagers} />
        <div className="clearfix" />
      </>
    );
  };

  upsertManagers = async (contestJid, data) => {
    const response = await this.props.onUpsertManagers(contestJid, data);
    this.setState({ lastRefreshManagersTime: new Date().getTime() });
    return response;
  };

  deleteManagers = async (contestJid, data) => {
    const response = await this.props.onDeleteManagers(contestJid, data);
    this.setState({ lastRefreshManagersTime: new Date().getTime() });
    return response;
  };
}

const mapStateToProps = state => ({
  contest: selectContest(state),
});

const mapDispatchToProps = {
  onGetManagers: contestManagerActions.getManagers,
  onUpsertManagers: contestManagerActions.upsertManagers,
  onDeleteManagers: contestManagerActions.deleteManagers,
};

export default withBreadcrumb('Managers')(connect(mapStateToProps, mapDispatchToProps)(ContestManagersPage));
