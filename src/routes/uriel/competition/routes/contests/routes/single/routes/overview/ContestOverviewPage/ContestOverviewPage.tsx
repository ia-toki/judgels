import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { HtmlText } from '../../../../../../../../../../components/HtmlText/HtmlText';
import { ContentCard } from '../../../../../../../../../../components/ContentCard/ContentCard';
import { Contest } from '../../../../../../../../../../modules/api/uriel/contest';
import { AppState } from '../../../../../../../../../../modules/store';
import { selectContest } from '../../../../../modules/contestSelectors';

import './ContestOverviewPage.css';

interface ContestOverviewPageProps extends RouteComponentProps<{ contestJid: string }> {
  contest: Contest;
}

class ContestOverviewPage extends React.PureComponent<ContestOverviewPageProps> {
  render() {
    const { contest } = this.props;

    return <ContentCard>{this.renderDescription(contest.description)}</ContentCard>;
  }

  private renderDescription = (description?: string) => {
    return description && <HtmlText>{description}</HtmlText>;
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
