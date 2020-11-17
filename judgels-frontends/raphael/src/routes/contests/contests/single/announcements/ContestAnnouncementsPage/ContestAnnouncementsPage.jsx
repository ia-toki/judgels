import * as React from 'react';
import { connect } from 'react-redux';

import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import Pagination from '../../../../../../components/Pagination/Pagination';
import { withBreadcrumb } from '../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { AppState } from '../../../../../../modules/store';
import { Contest } from '../../../../../../modules/api/uriel/contest';
import {
  ContestAnnouncement,
  ContestAnnouncementData,
  ContestAnnouncementsResponse,
} from '../../../../../../modules/api/uriel/contestAnnouncement';
import { selectContest } from '../../../modules/contestSelectors';
import { ContestAnnouncementCard } from '../ContestAnnouncementCard/ContestAnnouncementCard';
import { ContestAnnouncementCreateDialog } from '../ContestAnnouncementCreateDialog/ContestAnnouncementCreateDialog';
import { ContestAnnouncementEditDialog } from '../ContestAnnouncementEditDialog/ContestAnnouncementEditDialog';
import * as contestAnnouncementActions from '../modules/contestAnnouncementActions';

export interface ContestAnnouncementsPageProps {
  contest: Contest;
  onGetAnnouncements: (contestJid: string, page?: number) => Promise<ContestAnnouncementsResponse>;
  onCreateAnnouncement: (contestJid: string, data: ContestAnnouncementData) => void;
  onUpdateAnnouncement: (contestJid: string, announcementJid: string, data: ContestAnnouncementData) => void;
}

interface ContestAnnouncementsPageState {
  response?: ContestAnnouncementsResponse;
  lastRefreshAnnouncementsTime?: number;
  openEditDialogAnnouncement?: ContestAnnouncement;
}

class ContestAnnouncementsPage extends React.PureComponent<
  ContestAnnouncementsPageProps,
  ContestAnnouncementsPageState
> {
  private static PAGE_SIZE = 20;

  state: ContestAnnouncementsPageState = {};

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

  private renderAnnouncements = () => {
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

  private renderPagination = () => {
    // updates pagination when announcements are refreshed
    const { lastRefreshAnnouncementsTime } = this.state;
    const key = lastRefreshAnnouncementsTime || 0;

    return (
      <Pagination
        key={key}
        currentPage={1}
        pageSize={ContestAnnouncementsPage.PAGE_SIZE}
        onChangePage={this.onChangePage}
      />
    );
  };

  private onChangePage = async (nextPage: number) => {
    const data = await this.refreshAnnouncements(nextPage);
    return data.totalCount;
  };

  private refreshAnnouncements = async (page?: number) => {
    const response = await this.props.onGetAnnouncements(this.props.contest.jid, page);
    this.setState({ response });
    return response.data;
  };

  private createAnnouncement = async (contestJid, data) => {
    await this.props.onCreateAnnouncement(contestJid, data);
    this.setState({ lastRefreshAnnouncementsTime: new Date().getTime() });
  };

  private updateAnnouncement = async (contestJid, announcementJid, data) => {
    await this.props.onUpdateAnnouncement(contestJid, announcementJid, data);
    this.setState({ lastRefreshAnnouncementsTime: new Date().getTime() });
  };

  private renderCreateDialog = () => {
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

  private renderEditDialog = () => {
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

  private toggleEditDialog = (announcement?: ContestAnnouncement) => {
    this.setState({ openEditDialogAnnouncement: announcement });
  };
}

const mapStateToProps = (state: AppState) => ({
  contest: selectContest(state)!,
});

const mapDispatchToProps = {
  onGetAnnouncements: contestAnnouncementActions.getAnnouncements,
  onCreateAnnouncement: contestAnnouncementActions.createAnnouncement,
  onUpdateAnnouncement: contestAnnouncementActions.updateAnnouncement,
};

export default withBreadcrumb('Announcements')(connect(mapStateToProps, mapDispatchToProps)(ContestAnnouncementsPage));
