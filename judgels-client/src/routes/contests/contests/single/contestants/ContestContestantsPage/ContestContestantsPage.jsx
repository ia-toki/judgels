import { Button, Intent } from '@blueprintjs/core';
import { Refresh } from '@blueprintjs/icons';
import { Component } from 'react';
import { connect } from 'react-redux';

import { withBreadcrumb } from '../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../components/Pagination/Pagination';
import { reallyConfirm } from '../../../../../../utils/confirmation';
import { selectContest } from '../../../modules/contestSelectors';
import { ContestContestantAddDialog } from '../ContestContestantAddDialog/ContestContestantAddDialog';
import { ContestContestantRemoveDialog } from '../ContestContestantRemoveDialog/ContestContestantRemoveDialog';
import { ContestContestantsTable } from '../ContestContestantsTable/ContestContestantsTable';

import * as contestActions from '../../../modules/contestActions';
import * as contestContestantActions from '../../modules/contestContestantActions';

import './ContestContestantsPage.scss';

class ContestContestantsPage extends Component {
  static PAGE_SIZE = 1000;

  state = {
    response: undefined,
    lastRefreshContestantsTime: 0,
  };

  render() {
    return (
      <ContentCard>
        <h3>Contestants</h3>
        <hr />
        {this.renderAddRemoveDialogs()}
        {this.renderContestants()}
        {this.renderPagination()}
      </ContentCard>
    );
  }

  renderContestants = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: contestants, virtualModuleConfig, profilesMap } = response;
    if (contestants.page.length === 0) {
      return (
        <p>
          <small>No contestants.</small>
        </p>
      );
    }

    const { contest } = this.props;
    return (
      <ContestContestantsTable
        contest={contest}
        virtualModuleConfig={virtualModuleConfig}
        contestants={contestants.page}
        profilesMap={profilesMap}
        now={new Date().getTime()}
      />
    );
  };

  renderPagination = () => {
    const { lastRefreshContestantsTime } = this.state;

    return (
      <Pagination
        key={lastRefreshContestantsTime}
        pageSize={ContestContestantsPage.PAGE_SIZE}
        onChangePage={this.onChangePage}
      />
    );
  };

  onChangePage = async nextPage => {
    const data = await this.refreshContestants(nextPage);
    return data.totalCount;
  };

  refreshContestants = async page => {
    const response = await this.props.onGetContestants(this.props.contest.jid, page);
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
    const { data: contestants } = response;
    const isVirtualContest = contestants.page.some(contestant => contestant.contestStartTime !== null);
    return (
      <div className="content-card__header">
        <ContestContestantAddDialog contest={this.props.contest} onUpsertContestants={this.upsertContestants} />
        <ContestContestantRemoveDialog contest={this.props.contest} onDeleteContestants={this.deleteContestants} />
        {isVirtualContest && (
          <Button
            className="contest-contestant-dialog-button"
            intent={Intent.DANGER}
            icon={<Refresh />}
            onClick={this.onResetVirtualContest}
          >
            Reset all contestant virtual start times
          </Button>
        )}
        <div className="clearfix" />
      </div>
    );
  };

  onResetVirtualContest = async () => {
    if (reallyConfirm('Are you sure to reset all contestant virtual start times?')) {
      await this.props.onResetVirtualContest(this.props.contest.jid);
      this.setState({ lastRefreshContestantsTime: new Date().getTime() });
    }
  };

  upsertContestants = async (contestJid, data) => {
    const response = await this.props.onUpsertContestants(contestJid, data);
    this.setState({ lastRefreshContestantsTime: new Date().getTime() });
    return response;
  };

  deleteContestants = async (contestJid, data) => {
    const response = await this.props.onDeleteContestants(contestJid, data);
    this.setState({ lastRefreshContestantsTime: new Date().getTime() });
    return response;
  };
}

const mapStateToProps = state => ({
  contest: selectContest(state),
});

const mapDispatchToProps = {
  onGetContestants: contestContestantActions.getContestants,
  onUpsertContestants: contestContestantActions.upsertContestants,
  onDeleteContestants: contestContestantActions.deleteContestants,
  onResetVirtualContest: contestActions.resetVirtualContest,
};

export default withBreadcrumb('Contestants')(connect(mapStateToProps, mapDispatchToProps)(ContestContestantsPage));
