import { Button, Callout, Icon, Intent, Tag } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { APP_CONFIG } from 'conf';
import { LoadingState } from 'components/LoadingState/LoadingState';
import { ContestContestantState } from 'modules/api/uriel/contestContestant';
import { Contest } from 'modules/api/uriel/contest';
import { AppState } from 'modules/store';
import { selectIsLoggedIn } from 'modules/session/sessionSelectors';

import ContestRegistrantsDialog from '../ContestRegistrantsDialog/ContestRegistrantsDialog';
import ContestRegistrationConfirmationDialog from '../ContestRegistrationConfirmationDialog/ContestRegistrationConfirmationDialog';

import { selectContest } from '../../../modules/contestSelectors';
import { contestWebActions as injectedContestWebActions } from '../../modules/contestWebActions';
import { contestContestantActions as injectedContestContestantActions } from '../../modules/contestContestantActions';

import './ContestRegistrationCard.css';

export interface ContestRegistrationCardProps {
  isLoggedIn: boolean;
  contest: Contest;
  onGetMyContestantState: (contestJid: string) => Promise<ContestContestantState>;
  onGetApprovedContestantsCount: (contestJid: string) => Promise<number>;
  onGetContestWebConfig: (contestJid: string) => Promise<void>;
  onRegisterMyselfAsContestant: (contestJid: string) => Promise<void>;
  onUnregisterMyselfAsContestant: (contestJid: string) => Promise<void>;
}

interface ContestRegistrationCardState {
  contestantState?: ContestContestantState;
  contestantsCount?: number;
  isActionButtonLoading?: boolean;
  isRegistrantsDialogOpen?: boolean;
  isRegistrationConfirmationDialogOpen?: boolean;
}

class ContestRegistrationCard extends React.PureComponent<ContestRegistrationCardProps, ContestRegistrationCardState> {
  state: ContestRegistrationCardState = {};

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

  private refresh = async () => {
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

  private renderCard = () => {
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
        {this.renderRegistrationConfirmationDialog()}
        <div className="clearfix" />
      </>
    );
  };

  private renderContestantStateTag = (contestantState: ContestContestantState) => {
    if (
      contestantState === ContestContestantState.Registrant ||
      contestantState === ContestContestantState.Contestant
    ) {
      return (
        <Tag large intent={Intent.SUCCESS} className="contest-registration-card__item">
          <Icon icon="tick" /> Registered
        </Tag>
      );
    }
    return null;
  };

  private renderActionButton = (contestantState: ContestContestantState) => {
    if (contestantState === ContestContestantState.Registrable) {
      return (
        <Button
          className="contest-registration-card__item"
          intent={Intent.PRIMARY}
          text="Register"
          onClick={this.tryRegister}
          loading={this.state.isActionButtonLoading}
          disabled={this.state.isRegistrationConfirmationDialogOpen}
        />
      );
    }
    if (contestantState === ContestContestantState.Registrant) {
      return (
        <Button
          className="contest-registration-card__item"
          intent={Intent.DANGER}
          text="Unregister"
          onClick={this.unregister}
          loading={this.state.isActionButtonLoading}
        />
      );
    }
    return null;
  };

  private renderViewRegistrantsButton = (contestantsCount: number) => {
    return (
      <Button
        className="contest-registration-card__item"
        icon="people"
        text={`View registrants (${contestantsCount})`}
        onClick={this.toggleRegistrantsDialog}
      />
    );
  };

  private renderRegistrantsDialog = () => {
    if (!this.state.isRegistrantsDialogOpen) {
      return null;
    }
    return <ContestRegistrantsDialog onClose={this.toggleRegistrantsDialog} />;
  };

  private renderRegistrationConfirmationDialog = () => {
    if (!this.state.isRegistrationConfirmationDialogOpen) {
      return null;
    }
    const { contest } = this.props;

    return (
      <ContestRegistrationConfirmationDialog
        contest={contest}
        onClose={this.toggleRegistrationConfirmationDialog}
        onRegister={this.register}
      />
    );
  };

  private register = async () => {
    this.setState({ isActionButtonLoading: true });
    await this.props.onRegisterMyselfAsContestant(this.props.contest.jid);
    this.setState({ isActionButtonLoading: false });
    await this.refresh();
  };

  private unregister = async () => {
    this.setState({ isActionButtonLoading: true });
    await this.props.onUnregisterMyselfAsContestant(this.props.contest.jid);
    this.setState({ isActionButtonLoading: false });
    await this.refresh();
  };

  private tryRegister = () => {
    console.log(APP_CONFIG);
    if (APP_CONFIG.termsAndConditions !== undefined && APP_CONFIG.termsAndConditions.contest !== undefined) {
      this.toggleRegistrationConfirmationDialog();
    } else {
      this.register();
    }
  };

  private toggleRegistrantsDialog = () => {
    this.setState((prevState: ContestRegistrationCardState) => ({
      isRegistrantsDialogOpen: !prevState.isRegistrantsDialogOpen,
    }));
  };

  private toggleRegistrationConfirmationDialog = () => {
    this.setState((prevState: ContestRegistrationCardState) => ({
      isRegistrationConfirmationDialogOpen: !prevState.isRegistrationConfirmationDialogOpen,
    }));
  };
}

function createContestRegistrationCard(contestWebActions, contestContestantActions) {
  const mapStateToProps = (state: AppState) => ({
    isLoggedIn: selectIsLoggedIn(state),
    contest: selectContest(state)!,
  });
  const mapDispatchToProps = {
    onGetContestWebConfig: contestWebActions.getWebConfig,
    onGetMyContestantState: contestContestantActions.getMyContestantState,
    onGetApprovedContestantsCount: contestContestantActions.getApprovedContestantsCount,
    onRegisterMyselfAsContestant: contestContestantActions.registerMyselfAsContestant,
    onUnregisterMyselfAsContestant: contestContestantActions.unregisterMyselfAsContestant,
  };
  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ContestRegistrationCard));
}

export default createContestRegistrationCard(injectedContestWebActions, injectedContestContestantActions);
