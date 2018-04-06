import { Callout, Card, Intent } from '@blueprintjs/core';
import * as HTMLReactParser from 'html-react-parser';
import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { Contest } from '../../../../../../../../../../modules/api/uriel/contest';
import { AppState } from '../../../../../../../../../../modules/store';
import { selectContest } from '../../../../../modules/contestSelectors';
import { APP_CONFIG } from '../../../../../../../../../../conf';

interface ContestDetailsPageProps extends RouteComponentProps<{ contestJid: string }> {
  contest: Contest;
}

class ContestDetailsPage extends React.Component<ContestDetailsPageProps> {
  render() {
    const { contest } = this.props;

    return (
      <div>
        <Callout intent={Intent.WARNING} iconName="info-sign">
          <strong>New page under construction.</strong> See the old contest page here:{' '}
          <a href={`${APP_CONFIG.tempHome.urielUrl}/contests/${contest.id}`}>
            <strong>
              {APP_CONFIG.tempHome.urielUrl}/contests/{contest.id}
            </strong>
          </a>
        </Callout>
        <hr />
        {this.renderDescription(contest.description)}
      </div>
    );
  }

  private renderDescription = (description: string) => {
    return description && <Card>{HTMLReactParser(description)}</Card>;
  };
}

function createContestDetailsPage() {
  const mapStateToProps = (state: AppState) =>
    ({
      contest: selectContest(state)!,
    } as Partial<ContestDetailsPageProps>);

  return withRouter<any>(connect(mapStateToProps)(ContestDetailsPage));
}

export default createContestDetailsPage();
