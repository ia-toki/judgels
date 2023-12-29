import { Component } from 'react';
import { connect } from 'react-redux';

import { Card } from '../../../../components/Card/Card';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { ContestRatingChangesDialog } from '../ContestRatingChangesDialog/ContestRatingChangesDialog';
import { ContestsPendingRatingTable } from '../ContestsPendingRatingTable/ContestsPendingRatingTable';

import * as ratingActions from '../modules/ratingActions';

class RatingsPage extends Component {
  state = {
    response: undefined,
    selectedContest: undefined,
    isApplyingRatingChanges: false,
  };

  componentDidMount() {
    this.refreshContestsPendingRating();
  }

  render() {
    return <Card title="Ratings">{this.renderContestsPendingRating()}</Card>;
  }

  renderContestsPendingRating = () => {
    return (
      <ContentCard>
        <h4>Contests pending rating changes</h4>
        <hr />
        {this.renderContestsPendingRatingTable()}
        {this.renderContestRatingChangesDialog()}
      </ContentCard>
    );
  };

  renderContestsPendingRatingTable = () => {
    const { response } = this.state;
    if (!response) {
      return null;
    }

    const { data: contests } = response;
    if (contests.length === 0) {
      return (
        <p>
          <small>No contests.</small>
        </p>
      );
    }

    return (
      <ContestsPendingRatingTable
        contests={contests}
        onClickView={this.selectContest}
        isContestViewed={!!this.state.selectedContest}
      />
    );
  };

  renderContestRatingChangesDialog = () => {
    const { response, selectedContest } = this.state;
    if (!response || !selectedContest) {
      return null;
    }

    return (
      <ContestRatingChangesDialog
        contest={selectedContest}
        ratingChanges={response.ratingChangesMap[selectedContest.jid]}
        onClose={this.closeContestRatingChangesDialog}
        onApply={this.applyRatingChanges}
        isApplying={this.state.isApplyingRatingChanges}
      />
    );
  };

  closeContestRatingChangesDialog = () => {
    this.selectContest(undefined);
  };

  selectContest = contest => {
    this.setState({ selectedContest: contest });
  };

  refreshContestsPendingRating = async () => {
    const response = await this.props.onGetContestsPendingRating();
    this.setState({ response });
  };

  applyRatingChanges = async () => {
    this.setState({ isApplyingRatingChanges: true });

    const { response, selectedContest } = this.state;
    await this.props.onUpdateRatings({
      eventJid: selectedContest.jid,
      time: selectedContest.beginTime + selectedContest.duration,
      ratingsMap: response.ratingChangesMap[selectedContest.jid].ratingsMap,
    });

    this.setState({ selectedContest: undefined, isApplyingRatingChanges: false });
    await this.refreshContestsPendingRating();
  };
}

const mapDispatchToProps = {
  onGetContestsPendingRating: ratingActions.getContestsPendingRating,
  onUpdateRatings: ratingActions.updateRatings,
};

export default connect(undefined, mapDispatchToProps)(RatingsPage);
