import * as React from 'react';
import { connect } from 'react-redux';

import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import Pagination from '../../../../../../components/Pagination/Pagination';
import { withBreadcrumb } from '../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { selectContest } from '../../../modules/contestSelectors';
import { ContestAnnouncementCard } from '../ContestAnnouncementCard/ContestAnnouncementCard';
import { ContestAnnouncementCreateDialog } from '../ContestAnnouncementCreateDialog/ContestAnnouncementCreateDialog';
import { ContestAnnouncementEditDialog } from '../ContestAnnouncementEditDialog/ContestAnnouncementEditDialog';
import * as contestAnnouncementActions from '../modules/contestAnnouncementActions';

class ContestAnnouncementsPage extends React.Component {
  static PAGE_SIZE = 20;

  state = {
    response: undefined,
    lastRefreshAnnouncementsTime: 0,
    openEditDialogAnnouncement: undefined,
  };

  render() {
    return (
      <ContentCard>
        <h3>Announcements</h3>
        <hr />
        {this.renderCreateDialog()}
        {this.renderAnnouncements()}
        {this.renderPagination()}
        {this.renderEditDialog()}
      </ContentCard>
    );
  }

  renderAnnouncements = () => {
    const { response, openEditDialogAnnouncement } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: announcements, config, profilesMap } = response;
    if (announcements.page.length === 0) {
      return (
        <p>
          <small>No announcements.</small>
        </p>
      );
    }

    const { canSupervise, canManage } = config;

    return announcements.page.map(announcement => (
      <div className="content-card__section" key={announcement.jid}>
        <ContestAnnouncementCard
          contest={this.props.contest}
          announcement={announcement}
          canSupervise={canSupervise}
          canManage={canManage}
          profile={canSupervise ? profilesMap[announcement.userJid] : undefined}
          isEditDialogOpen={!openEditDialogAnnouncement ? false : announcement.jid === openEditDialogAnnouncement.jid}
          onToggleEditDialog={this.toggleEditDialog}
        />
      </div>
    ));
  };

  renderPagination = () => {
    // updates pagination when announcements are refreshed
    const { lastRefreshAnnouncementsTime } = this.state;

    return (
      <Pagination
        key={lastRefreshAnnouncementsTime}
        currentPage={1}
        pageSize={ContestAnnouncementsPage.PAGE_SIZE}
        onChangePage={this.onChangePage}
      />
    );
  };

  onChangePage = async nextPage => {
    const data = await this.refreshAnnouncements(nextPage);
    return data.totalCount;
  };

  refreshAnnouncements = async page => {
    const response = await this.props.onGetAnnouncements(this.props.contest.jid, page);
    this.setState({ response });
    return response.data;
  };

  createAnnouncement = async (contestJid, data) => {
    await this.props.onCreateAnnouncement(contestJid, data);
    this.setState({ lastRefreshAnnouncementsTime: new Date().getTime() });
  };

  updateAnnouncement = async (contestJid, announcementJid, data) => {
    await this.props.onUpdateAnnouncement(contestJid, announcementJid, data);
    this.setState({ lastRefreshAnnouncementsTime: new Date().getTime() });
  };

  renderCreateDialog = () => {
    const { response } = this.state;
    if (!response) {
      return null;
    }
    if (!response.config.canSupervise) {
      return null;
    }

    return (
      <ContestAnnouncementCreateDialog contest={this.props.contest} onCreateAnnouncement={this.createAnnouncement} />
    );
  };

  renderEditDialog = () => {
    const { response, openEditDialogAnnouncement } = this.state;
    if (!response) {
      return null;
    }
    if (!response.config.canSupervise) {
      return null;
    }

    return (
      <ContestAnnouncementEditDialog
        contest={this.props.contest}
        announcement={openEditDialogAnnouncement}
        onToggleEditDialog={this.toggleEditDialog}
        onUpdateAnnouncement={this.updateAnnouncement}
      />
    );
  };

  toggleEditDialog = announcement => {
    this.setState({ openEditDialogAnnouncement: announcement });
  };
}

const mapStateToProps = state => ({
  contest: selectContest(state),
});

const mapDispatchToProps = {
  onGetAnnouncements: contestAnnouncementActions.getAnnouncements,
  onCreateAnnouncement: contestAnnouncementActions.createAnnouncement,
  onUpdateAnnouncement: contestAnnouncementActions.updateAnnouncement,
};

export default withBreadcrumb('Announcements')(connect(mapStateToProps, mapDispatchToProps)(ContestAnnouncementsPage));
