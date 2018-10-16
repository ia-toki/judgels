import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { LoadingState } from 'components/LoadingState/LoadingState';
import { ContentCard } from 'components/ContentCard/ContentCard';
import { AppState } from 'modules/store';
import { Contest } from 'modules/api/uriel/contest';
import {
  ContestAnnouncement,
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
  response?: ContestAnnouncementsResponse;
  openEditDialogAnnouncement?: ContestAnnouncement;
}

class ContestAnnouncementsPage extends React.PureComponent<
  ContestAnnouncementsPageProps,
  ContestAnnouncementsPageState
> {
  state: ContestAnnouncementsPageState = {};

  async componentDidMount() {
    await this.refreshAnnouncements();
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
    const { response, openEditDialogAnnouncement } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: announcements, config } = response;

    if (announcements.length === 0) {
      return (
        <p>
          <small>No announcements.</small>
        </p>
      );
    }

    const props = {
      isAllowedToEditAnnouncement: config.isAllowedToEditAnnouncement,
      contest: this.props.contest,
      onRefreshAnnouncements: this.refreshAnnouncements,
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

  private refreshAnnouncements = async () => {
    const response = await this.props.onGetAnnouncements(this.props.contest.jid);
    this.setState({ response });
  };

  private createAnnouncement = async (contestJid, data) => {
    await this.props.onCreateAnnouncement(contestJid, data);
    await this.refreshAnnouncements();
  };

  private updateAnnouncement = async (contestJid, announcementJid, data) => {
    await this.props.onUpdateAnnouncement(contestJid, announcementJid, data);
    await this.refreshAnnouncements();
  };

  private renderCreateDialog = () => {
    const { response } = this.state;
    if (!response) {
      return null;
    }
    if (!response.config.isAllowedToCreateAnnouncement) {
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
    if (!response.config.isAllowedToEditAnnouncement) {
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

export function createContestAnnouncementsPage(contestAnnouncementActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
  });

  const mapDispatchToProps = {
    onGetAnnouncements: contestAnnouncementActions.getAnnouncements,
    onCreateAnnouncement: contestAnnouncementActions.createAnnouncement,
    onUpdateAnnouncement: contestAnnouncementActions.updateAnnouncement,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestAnnouncementsPage));
}

export default createContestAnnouncementsPage(injectedContestAnnouncementActions);
