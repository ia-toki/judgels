import { Card } from '@blueprintjs/core';
import * as HTMLReactParser from 'html-react-parser';
import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { Contest } from '../../../../../../../../../../modules/api/uriel/contest';
import { AppState } from '../../../../../../../../../../modules/store';
import { selectContest } from '../../../../../modules/contestSelectors';

interface ContestDetailsPageProps extends RouteComponentProps<{ contestJid: string }> {
  contest: Contest;
}

class ContestDetailsPage extends React.Component<ContestDetailsPageProps> {
  render() {
    const { contest } = this.props;

    return <Card>{HTMLReactParser(contest.description)}</Card>;
  }
}

function createContestDetailsPage() {
  const mapStateToProps = (state: AppState) =>
    ({
      contest: selectContest(state)!,
    } as Partial<ContestDetailsPageProps>);

  return withRouter<any>(connect(mapStateToProps)(ContestDetailsPage));
}

export default createContestDetailsPage();
