import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { LoadingState } from 'components/LoadingState/LoadingState';
import { ContentCard } from 'components/ContentCard/ContentCard';
import { AppState } from 'modules/store';
import { Contest } from 'modules/api/uriel/contest';
import {
  ContestAnnouncement,
  ContestAnnouncementConfig,
  ContestAnnouncementData,
  ContestAnnouncementsResponse,
} from 'modules/api/uriel/contestAnnouncement';

import { selectContest } from '../../../modules/contestSelectors';
import { contestAnnouncementActions as injectedContestAnnouncementActions } from '../modules/contestAnnouncementActions';
import { ContestAnnouncementCard } from '../ContestAnnouncementCard/ContestAnnouncementCard';
import { ContestAnnouncementCreateDialog } from '../ContestAnnouncementCreateDialog/ContestAnnouncementCreateDialog';
import { ContestAnnouncementEditDialog } from '../ContestAnnouncementEditDialog/ContestAnnouncementEditDialog';

export interface ContestAnnouncementsPageProps {
  contest: Contest;
  onGetAnnouncements: (contestJid: string) => Promise<ContestAnnouncementsResponse>;
  onCreateAnnouncement: (contestJid: string, data: ContestAnnouncementData) => void;
  onUpdateAnnouncement: (contestJid: string, announcementJid: string, data: ContestAnnouncementData) => void;
}

interface ContestAnnouncementsPageState {
  announcements?: ContestAnnouncement[];
  config?: ContestAnnouncementConfig;
  openEditDialogAnnouncement?: ContestAnnouncement;
}

export class ContestAnnouncementsPage extends React.PureComponent<
  ContestAnnouncementsPageProps,
  ContestAnnouncementsPageState
> {
  state: ContestAnnouncementsPageState = {};

  async componentDidMount() {
    await this.refreshAnnouncement();
  }

  render() {
    return (
      <ContentCard>
        <h3>Announcements</h3>
        <hr />
        {this.renderCreateDialog()}
        {this.renderAnnouncements()}
        {this.renderEditDialog()}
      </ContentCard>
    );
  }

  private renderAnnouncements = () => {
    const { announcements, config, openEditDialogAnnouncement } = this.state;
    if (!announcements) {
      return <LoadingState />;
    }

    if (announcements.length === 0) {
      return (
        <p>
          <small>No announcements.</small>
        </p>
      );
    }

    const props = {
      isAllowedToEditAnnouncement: (config && config.isAllowedToEditAnnouncement) || false,
      contest: this.props.contest,
      onRefreshAnnouncements: this.refreshAnnouncement,
      onUpdateAnnouncement: this.props.onUpdateAnnouncement,
    };

    return announcements.map(announcement => (
      <div className="content-card__section" key={announcement.jid}>
        <ContestAnnouncementCard
          announcement={announcement}
          isEditDialogOpen={!openEditDialogAnnouncement ? false : announcement.jid === openEditDialogAnnouncement.jid}
          onToggleEditDialog={this.toggleEditDialog}
          {...props}
        />
      </div>
    ));
  };

  private refreshAnnouncement = async () => {
    const announcementsResponse = await this.props.onGetAnnouncements(this.props.contest.jid);
    const config = announcementsResponse.config;
    const announcements = announcementsResponse.data;
    this.setState({ config, announcements });
  };

  private renderCreateDialog = () => {
    const { config } = this.state;
    const props = {
      contest: this.props.contest,
      onRefreshAnnouncements: this.refreshAnnouncement,
      isAllowedToCreateAnnouncement: (config && config.isAllowedToCreateAnnouncement) || false,
      onCreateAnnouncement: this.props.onCreateAnnouncement,
    };
    return <ContestAnnouncementCreateDialog {...props} />;
  };

  private renderEditDialog = () => {
    const { config, openEditDialogAnnouncement } = this.state;
    return (
      <ContestAnnouncementEditDialog
        contest={this.props.contest}
        announcement={openEditDialogAnnouncement}
        isAllowedToEditAnnouncement={!!(config && config.isAllowedToEditAnnouncement)}
        onToggleEditDialog={this.toggleEditDialog}
        onRefreshAnnouncements={this.refreshAnnouncement}
        onUpdateAnnouncement={this.props.onUpdateAnnouncement}
      />
    );
  };

  private toggleEditDialog = (announcement?: ContestAnnouncement) => {
    this.setState({ openEditDialogAnnouncement: announcement });
  };
}

function createContestAnnouncementsPage(contestAnnouncementActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
  });

  const mapDispatchToProps = {
    onGetAnnouncements: (contestJid: string) => contestAnnouncementActions.getAnnouncements(contestJid),
    onCreateAnnouncement: (contestJid: string, data: ContestAnnouncementData) =>
      contestAnnouncementActions.createAnnouncement(contestJid, data),
    onUpdateAnnouncement: (contestJid: string, announcementJid: string, data: ContestAnnouncementData) =>
      contestAnnouncementActions.updateAnnouncement(contestJid, announcementJid, data),
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestAnnouncementsPage));
}

export default createContestAnnouncementsPage(injectedContestAnnouncementActions);
