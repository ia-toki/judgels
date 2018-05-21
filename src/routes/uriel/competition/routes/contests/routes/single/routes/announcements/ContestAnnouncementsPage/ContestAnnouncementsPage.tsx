import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { LoadingState } from '../../../../../../../../../../components/LoadingState/LoadingState';
import { ContentCard } from '../../../../../../../../../../components/ContentCard/ContentCard';
import { AppState } from '../../../../../../../../../../modules/store';
import { selectContest } from '../../../../../modules/contestSelectors';
import { UsersMap } from '../../../../../../../../../../modules/api/jophiel/user';
import { Contest } from '../../../../../../../../../../modules/api/uriel/contest';
import {
  ContestAnnouncement,
  ContestAnnouncementsResponse,
} from '../../../../../../../../../../modules/api/uriel/contestAnnouncement';
import { contestAnnouncementActions as injectedContestAnnouncementActions } from '../modules/contestAnnouncementActions';
import { ContestAnnouncementCard } from '../ContestAnnouncementCard/ContestAnnouncementCard';

export interface ContestAnnouncementsPageProps {
  contest: Contest;
  onFetchAnnouncements: (contestJid: string) => Promise<ContestAnnouncementsResponse>;
}

interface ContestAnnouncementsPageState {
  announcements?: ContestAnnouncement[];
  usersMap?: UsersMap;
}

export class ContestAnnouncementsPage extends React.PureComponent<
  ContestAnnouncementsPageProps,
  ContestAnnouncementsPageState
> {
  state: ContestAnnouncementsPageState = {};

  async componentDidMount() {
    const { data, usersMap } = await this.props.onFetchAnnouncements(this.props.contest.jid);
    this.setState({
      announcements: data,
      usersMap,
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
    const { announcements, usersMap } = this.state;
    if (!announcements || !usersMap) {
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

    return (
      <div>
        {announcements.map(announcement => (
          <ContestAnnouncementCard key={announcement.jid} announcement={announcement} />
        ))}
      </div>
    );
  };
}

function createContestAnnouncementsPage(contestAnnouncementActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
  });

  const mapDispatchToProps = {
    onFetchAnnouncements: contestAnnouncementActions.fetchList,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestAnnouncementsPage));
}

export default createContestAnnouncementsPage(injectedContestAnnouncementActions);
