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
} from 'modules/api/uriel/contestAnnouncement';

import { selectContest } from '../../../modules/contestSelectors';
import { contestAnnouncementActions as injectedContestAnnouncementActions } from '../modules/contestAnnouncementActions';
import { ContestAnnouncementCard } from '../ContestAnnouncementCard/ContestAnnouncementCard';
import { ContestAnnouncementCreateDialog } from '../ContestAnnouncementCreateDialog/ContestAnnouncementCreateDialog';

export interface ContestAnnouncementsPageProps {
  contest: Contest;
  onGetAllAnnouncements: (contestJid: string) => Promise<ContestAnnouncement[]>;
  onGetPublishedAnnouncements: (contestJid: string) => Promise<ContestAnnouncement[]>;
  onGetAnnouncementConfig: (contestJid: string) => Promise<ContestAnnouncementConfig>;
  onCreateAnnouncement: (contestJid: string, data: ContestAnnouncementData) => void;
}

interface ContestAnnouncementsPageState {
  announcements?: ContestAnnouncement[];
}

export class ContestAnnouncementsPage extends React.PureComponent<
  ContestAnnouncementsPageProps,
  ContestAnnouncementsPageState
> {
  state: ContestAnnouncementsPageState = {};

  async componentDidMount() {
    await this.refreshAnnouncement(false);
  }

  render() {
    return (
      <ContentCard>
        <h3>Announcements</h3>
        <hr />
        {this.renderCreateDialog()}
        {this.renderAnnouncements()}
      </ContentCard>
    );
  }

  private renderAnnouncements = () => {
    const { announcements } = this.state;
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

    return announcements.map(announcement => (
      <div className="content-card__section" key={announcement.jid}>
        <ContestAnnouncementCard announcement={announcement} />
      </div>
    ));
  };

  private refreshAnnouncement = async (isShowDrafts) => {
    let getAnnouncements
    if (isShowDrafts) {
      getAnnouncements = this.props.onGetAllAnnouncements
    } else {
      getAnnouncements = this.props.onGetPublishedAnnouncements
    }
    const announcements = await getAnnouncements(this.props.contest.jid);
    this.setState({
      announcements,
    });
  };

  private renderCreateDialog = () => {
    const props = {
      contest: this.props.contest,
      onRefreshAnnouncements: this.refreshAnnouncement,
      onGetAnnouncementConfig: this.props.onGetAnnouncementConfig,
      onCreateAnnouncement: this.props.onCreateAnnouncement,
    };
    return <ContestAnnouncementCreateDialog {...props} />;
  };
}

function createContestAnnouncementsPage(contestAnnouncementActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
  });

  const mapDispatchToProps = {
    onGetPublishedAnnouncements: contestAnnouncementActions.getPublishedAnnouncements,
    onGetAllAnnouncements: contestAnnouncementActions.getAllAnnouncements,
    onGetAnnouncementConfig: (contestJid: string) => contestAnnouncementActions.getAnnouncementConfig(contestJid),
    onCreateAnnouncement: (contestJid: string, data: ContestAnnouncementData) =>
      contestAnnouncementActions.createAnnouncement(contestJid, data),
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestAnnouncementsPage));
}

export default createContestAnnouncementsPage(injectedContestAnnouncementActions);
