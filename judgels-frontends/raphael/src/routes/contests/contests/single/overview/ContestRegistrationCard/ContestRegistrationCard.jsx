import { Button, Callout, Icon, Intent, Tag } from '@blueprintjs/core';
import { Component } from 'react';
import { connect } from 'react-redux';

import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { ContestContestantState } from '../../../../../../modules/api/uriel/contestContestant';
import { selectIsLoggedIn } from '../../../../../../modules/session/sessionSelectors';
import ContestRegistrantsDialog from '../ContestRegistrantsDialog/ContestRegistrantsDialog';
import { selectContest } from '../../../modules/contestSelectors';
import * as contestWebActions from '../../modules/contestWebActions';
import * as contestContestantActions from '../../modules/contestContestantActions';

import './ContestRegistrationCard.css';

class ContestRegistrationCard extends Component {
  state = {
    contestantState: undefined,
    contestantCount: undefined,
    isActionButtonLoading: false,
    isRegistrantsDialogOpen: false,
  };

  async componentDidMount() {
    await this.refresh();
  }

  render() {
    if (!this.props.isLoggedIn) {
      return (
        <Callout icon="ban-circle" className="contest-registration-card--error secondary-info">
          Please log in to register.
        </Callout>
      );
    }
    return <Callout className="contest-registration-card">{this.renderCard()}</Callout>;
  }

  refresh = async () => {
    if (!this.props.isLoggedIn) {
      return;
    }

    const [contestantState, contestantsCount] = await Promise.all([
      this.props.onGetMyContestantState(this.props.contest.jid),
      this.props.onGetApprovedContestantsCount(this.props.contest.jid),
      this.props.onGetContestWebConfig(this.props.contest.jid),
    ]);
    this.setState({ contestantState, contestantsCount });
  };

  renderCard = () => {
    const { contestantState, contestantsCount } = this.state;
    if (!contestantState || contestantsCount === undefined) {
      return <LoadingState />;
    }

    return (
      <>
        {this.renderContestantStateTag(contestantState)}
        {this.renderActionButton(contestantState)}
        {this.renderViewRegistrantsButton(contestantsCount)}
        {this.renderRegistrantsDialog()}
        <div className="clearfix" />
      </>
    );
  };

  renderContestantStateTag = contestantState => {
    if (
      contestantState === ContestContestantState.Registrant ||
      contestantState === ContestContestantState.Contestant
    ) {
      return (
        <Tag large intent={Intent.SUCCESS} className="contest-registration-card__item contest-registration-card__state">
          <Icon icon="tick" /> Registered
        </Tag>
      );
    }
    return null;
  };

  renderActionButton = contestantState => {
    if (contestantState === ContestContestantState.RegistrableWrongDivision) {
      return (
        <Button
          className="contest-registration-card__item contest-registration-card__action"
          intent={Intent.WARNING}
          text="Your rating is not allowed for this contest division"
          disabled
        />
      );
    }
    if (contestantState === ContestContestantState.Registrable) {
      return (
        <Button
          className="contest-registration-card__item contest-registration-card__action"
          intent={Intent.PRIMARY}
          text="Register"
          onClick={this.register}
          loading={this.state.isActionButtonLoading}
        />
      );
    }
    if (contestantState === ContestContestantState.Registrant) {
      return (
        <Button
          className="contest-registration-card__item contest-registration-card__action"
          intent={Intent.DANGER}
          text="Unregister"
          onClick={this.unregister}
          loading={this.state.isActionButtonLoading}
        />
      );
    }
    return null;
  };

  renderViewRegistrantsButton = contestantsCount => {
    return (
      <Button
        className="contest-registration-card__item"
        icon="people"
        text={`View registrants (${contestantsCount})`}
        onClick={this.toggleRegistrantsDialog}
      />
    );
  };

  renderRegistrantsDialog = () => {
    if (!this.state.isRegistrantsDialogOpen) {
      return null;
    }
    return <ContestRegistrantsDialog onClose={this.toggleRegistrantsDialog} />;
  };

  register = async () => {
    this.setState({ isActionButtonLoading: true });
    await this.props.onRegisterMyselfAsContestant(this.props.contest.jid);
    this.setState({ isActionButtonLoading: false });
    await this.refresh();
  };

  unregister = async () => {
    this.setState({ isActionButtonLoading: true });
    await this.props.onUnregisterMyselfAsContestant(this.props.contest.jid);
    this.setState({ isActionButtonLoading: false });
    await this.refresh();
  };

  toggleRegistrantsDialog = () => {
    this.setState(prevState => ({
      isRegistrantsDialogOpen: !prevState.isRegistrantsDialogOpen,
    }));
  };
}

const mapStateToProps = state => ({
  isLoggedIn: selectIsLoggedIn(state),
  contest: selectContest(state),
});
const mapDispatchToProps = {
  onGetContestWebConfig: contestWebActions.getWebConfig,
  onGetMyContestantState: contestContestantActions.getMyContestantState,
  onGetApprovedContestantsCount: contestContestantActions.getApprovedContestantsCount,
  onRegisterMyselfAsContestant: contestContestantActions.registerMyselfAsContestant,
  onUnregisterMyselfAsContestant: contestContestantActions.unregisterMyselfAsContestant,
};
export default connect(mapStateToProps, mapDispatchToProps)(ContestRegistrationCard);
