import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { HtmlText } from 'components/HtmlText/HtmlText';
import { ContentCard } from 'components/ContentCard/ContentCard';
import ContestRegistrationCard from '../ContestRegistrationCard/ContestRegistrationCard';
import { ContestDescription } from 'modules/api/uriel/contest';
import { AppState } from 'modules/store';

import { selectContest } from '../../../modules/contestSelectors';

import './ContestOverviewPage.css';

interface ContestOverviewPageProps extends RouteComponentProps<{ contestJid: string }> {
  contestDescription: ContestDescription;
}

class ContestOverviewPage extends React.PureComponent<ContestOverviewPageProps> {
  render() {
    return (
      <>
        {this.renderRegistration()}
        {this.renderDescription()}
      </>
    );
  }

  private renderRegistration = () => {
    return <ContestRegistrationCard />;
  };

  private renderDescription = () => {
    const { description } = this.props.contestDescription;
    if (!description) {
      return null;
    }
    
    return (
      <ContentCard>
        <HtmlText>{description}</HtmlText>
      </ContentCard>
    );
  };
  
}

function createContestOverviewPage() {
  const mapStateToProps = (state: AppState) =>
    ({
      contestDescription: selectContest(state)!,
    } as Partial<ContestOverviewPageProps>);

  return withRouter<any>(connect(mapStateToProps)(ContestOverviewPage));
}

export default createContestOverviewPage();
