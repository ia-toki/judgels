import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { HtmlText } from '../../../../../../components/HtmlText/HtmlText';
import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import ContestRegistrationCard from '../ContestRegistrationCard/ContestRegistrationCard';
import { selectContest } from '../../../modules/contestSelectors';
import * as contestActions from '../../../modules/contestActions';

import './ContestOverviewPage.css';

class ContestOverviewPage extends React.Component {
  state = {
    description: undefined,
  };

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

  renderRegistration = () => {
    return <ContestRegistrationCard />;
  };

  renderDescription = () => {
    const { description } = this.state;

    if (description === undefined) {
      return <LoadingState />;
    }

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

const mapStateToProps = state => ({
  contest: selectContest(state),
});
const mapDispatchToProps = {
  onGetContestDescription: contestActions.getContestDescription,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ContestOverviewPage));
