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
  onGetAnnouncements: (contestJid: string) => Promise<ContestAnnouncement[]>;
  onGetAnnouncementConfig: (contestJid: string) => Promise<ContestAnnouncementConfig>;
  onCreateAnnouncement: (contestJid: string, data: ContestAnnouncementData) => void;
  onUpdateAnnouncement: (contestJid: string, announcementJid: string, data: ContestAnnouncementData) => void;
}

interface ContestAnnouncementsPageState {
  announcements?: ContestAnnouncement[];
  config: ContestAnnouncementConfig;
}

export class ContestAnnouncementsPage extends React.PureComponent<
  ContestAnnouncementsPageProps,
  ContestAnnouncementsPageState
> {
  state: ContestAnnouncementsPageState = {
    config: {
      isAllowedToCreateAnnouncement: false,
      isAllowedToEditAnnouncement: false,
    } as ContestAnnouncementConfig,
  };

  async componentDidMount() {
    const config = await this.props.onGetAnnouncementConfig(this.props.contest.jid);
    this.setState({ config });
    await this.refreshAnnouncement();
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
    const { announcements, config } = this.state;
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
      config,
      contest: this.props.contest,
      onRefreshAnnouncements: this.refreshAnnouncement,
      onUpdateAnnouncement: this.props.onUpdateAnnouncement,
    };

    return announcements.map(announcement => (
      <div className="content-card__section" key={announcement.jid}>
        <ContestAnnouncementCard announcement={announcement} {...props} />
      </div>
    ));
  };

  private refreshAnnouncement = async () => {
    const announcements = await this.props.onGetAnnouncements(this.props.contest.jid);
    this.setState({
      announcements,
    });
  };

  private renderCreateDialog = () => {
    const props = {
      contest: this.props.contest,
      onRefreshAnnouncements: this.refreshAnnouncement,
      config: this.state.config,
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
    onGetAnnouncements: (contestJid: string) => contestAnnouncementActions.getAnnouncements(contestJid),
    onGetAnnouncementConfig: (contestJid: string) => contestAnnouncementActions.getAnnouncementConfig(contestJid),
    onCreateAnnouncement: (contestJid: string, data: ContestAnnouncementData) =>
      contestAnnouncementActions.createAnnouncement(contestJid, data),
    onUpdateAnnouncement: (contestJid: string, announcementJid: string, data: ContestAnnouncementData) =>
      contestAnnouncementActions.updateAnnouncement(contestJid, announcementJid, data),
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestAnnouncementsPage));
}

export default createContestAnnouncementsPage(injectedContestAnnouncementActions);
