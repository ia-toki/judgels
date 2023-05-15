import { Component } from 'react';
import { connect } from 'react-redux';

import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { HtmlText } from '../../../../../../components/HtmlText/HtmlText';
import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import ContestRegistrationCard from '../ContestRegistrationCard/ContestRegistrationCard';
import { selectContest } from '../../../modules/contestSelectors';
import * as contestActions from '../../../modules/contestActions';

import './ContestOverviewPage.scss';

class ContestOverviewPage extends Component {
  state = {
    response: undefined,
  };

  async componentDidMount() {
    const response = await this.props.onGetContestDescription(this.props.contest.jid);
    this.setState({
      response,
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

  renderRegistration = () => {
    return <ContestRegistrationCard />;
  };

  renderDescription = () => {
    const { response } = this.state;

    if (response === undefined) {
      return <LoadingState />;
    }

    const { description, profilesMap } = response;
    if (!description) {
      return null;
    }

    return (
      <ContentCard>
        <HtmlText profilesMap={profilesMap}>{description}</HtmlText>
      </ContentCard>
    );
  };
}

const mapStateToProps = state => ({
  contest: selectContest(state),
});
const mapDispatchToProps = {
  onGetContestDescription: contestActions.getContestDescription,
};

export default connect(mapStateToProps, mapDispatchToProps)(ContestOverviewPage);
