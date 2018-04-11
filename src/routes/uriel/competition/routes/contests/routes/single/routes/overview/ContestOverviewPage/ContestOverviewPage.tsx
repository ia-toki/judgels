import { Callout, Intent } from '@blueprintjs/core';
import * as HTMLReactParser from 'html-react-parser';
import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { Card } from '../../../../../../../../../../components/Card/Card';
import { Contest } from '../../../../../../../../../../modules/api/uriel/contest';
import { AppState } from '../../../../../../../../../../modules/store';
import { selectContest } from '../../../../../modules/contestSelectors';
import { APP_CONFIG } from '../../../../../../../../../../conf';

import './ContestOverviewPage.css';

interface ContestOverviewPageProps extends RouteComponentProps<{ contestJid: string }> {
  contest: Contest;
}

class ContestOverviewPage extends React.Component<ContestOverviewPageProps> {
  render() {
    const { contest } = this.props;

    return (
      <div>
        <Callout intent={Intent.WARNING} icon="info-sign" className="contest-overview__callout">
          <strong>New page under construction.</strong> See the old contest page here:{' '}
          <a href={`${APP_CONFIG.tempHome.urielUrl}/contests/${contest.id}`}>
            <strong>
              {APP_CONFIG.tempHome.urielUrl}/contests/{contest.id}
            </strong>
          </a>
        </Callout>
        {this.renderDescription(contest.description)}
      </div>
    );
  }

  private renderDescription = (description: string) => {
    return description && <Card title="Description">{HTMLReactParser(description)}</Card>;
  };
}

function createContestOverviewPage() {
  const mapStateToProps = (state: AppState) =>
    ({
      contest: selectContest(state)!,
    } as Partial<ContestOverviewPageProps>);

  return withRouter<any>(connect(mapStateToProps)(ContestOverviewPage));
}

export default createContestOverviewPage();
