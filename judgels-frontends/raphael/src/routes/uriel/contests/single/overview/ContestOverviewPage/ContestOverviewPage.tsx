import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { LoadingState } from 'components/LoadingState/LoadingState';
import { AppState } from 'modules/store';
import { Contest, ContestDescription } from 'modules/api/uriel/contest';
import { HtmlText } from 'components/HtmlText/HtmlText';
import { ContentCard } from 'components/ContentCard/ContentCard';
import ContestRegistrationCard from '../ContestRegistrationCard/ContestRegistrationCard';

import { selectContest } from '../../../modules/contestSelectors';
import { contestActions as injectedContestActions } from '../../../modules/contestActions';

import './ContestOverviewPage.css';

export interface ContestOverviewPageProps {
  contest: Contest;
  onGetContestDescription: (contestJid: string) => Promise<ContestDescription>;
}

interface ContestOverviewPageState {
  description?: ContestDescription;
}

class ContestOverviewPage extends React.PureComponent<
  ContestOverviewPageProps,
  ContestOverviewPageState
> {
  state: ContestOverviewPageState = {};

  async componentDidMount() {
    const description = await this.props.onGetContestDescription(this.props.contest.jid);
    this.setState({
      description,
    });
  }

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
    const { description } = this.state;

    if (!description) {
      return <LoadingState />;
    }

    if (!description.description) {
      return null;
    }
    
    return (
      <ContentCard>
        <HtmlText>{description.description}</HtmlText>
      </ContentCard>
    );
  };
}

function createContestOverviewPage(contestActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state)!,
  });
  const mapDispatchToProps = {
    onGetContestDescription: contestActions.getContestDescription,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestOverviewPage));
}

export default createContestOverviewPage(injectedContestActions);
