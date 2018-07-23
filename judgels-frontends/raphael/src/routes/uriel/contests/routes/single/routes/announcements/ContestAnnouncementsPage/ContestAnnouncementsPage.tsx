import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import { AppState } from '../../../../../../../../modules/store';
import { selectContest } from '../../../../../modules/contestSelectors';
import { Contest } from '../../../../../../../../modules/api/uriel/contest';
import { ContestAnnouncement } from '../../../../../../../../modules/api/uriel/contestAnnouncement';
import { contestAnnouncementActions as injectedContestAnnouncementActions } from '../modules/contestAnnouncementActions';
import { ContestAnnouncementCard } from '../ContestAnnouncementCard/ContestAnnouncementCard';

export interface ContestAnnouncementsPageProps {
  contest: Contest;
  onGetPublishedAnnouncements: (contestJid: string) => Promise<ContestAnnouncement[]>;
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
    const announcements = await this.props.onGetPublishedAnnouncements(this.props.contest.jid);
    this.setState({
      announcements,
    });
  }

  render() {
    return (
      <ContentCard>
        <h3>Announcements</h3>
        <hr />
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
          <small>
            <em>No announcements.</em>
          </small>
        </p>
      );
    }

    return announcements.map(announcement => (
      <div className="content-card__section" key={announcement.jid}>
        <ContestAnnouncementCard announcement={announcement} />
      </div>
    ));
  };
}

function createContestAnnouncementsPage(contestAnnouncementActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
  });

  const mapDispatchToProps = {
    onGetPublishedAnnouncements: contestAnnouncementActions.getPublishedAnnouncements,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestAnnouncementsPage));
}

export default createContestAnnouncementsPage(injectedContestAnnouncementActions);
