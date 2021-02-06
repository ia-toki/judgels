import { Alert, Button, Callout, Intent } from '@blueprintjs/core';
import { PureComponent } from 'react';
import { connect } from 'react-redux';

import { ButtonLink } from '../../../../../../components/ButtonLink/ButtonLink';
import { FormattedDuration } from '../../../../../../components/FormattedDuration/FormattedDuration';
import { ContestState } from '../../../../../../modules/api/uriel/contestWeb';
import { selectContest } from '../../../modules/contestSelectors';
import { selectContestWebConfig } from '../../../modules/contestWebConfigSelectors';
import * as contestWebActions from '../../modules/contestWebActions';
import * as contestActions from '../../../modules/contestActions';

import './ContestStateWidget.css';

// TODO(fushar): unit tests
class ContestStateWidget extends PureComponent {
  state = {
    baseRemainingDuration: undefined,
    baseTimeForRemainingDuration: undefined,
    remainingDuration: undefined,
    isVirtualContestAlertOpen: undefined,
    isVirtualContestButtonLoading: undefined,
    problemSet: undefined,
  };

  currentTimeout;

  componentDidUpdate(prevProps) {
    if (prevProps.remainingStateDuration !== this.props.remainingStateDuration) {
      this.setUpBaseRemainingDuration();
    }
    this.searchProblemSet();
  }

  componentDidMount() {
    this.setUpBaseRemainingDuration();
    this.refreshRemainingDuration();
  }

  componentWillUnmount() {
    if (this.currentTimeout) {
      clearTimeout(this.currentTimeout);
    }
  }

  render() {
    const { leftComponent, rightComponent } = this.getWidgetComponents();
    return (
      <Callout intent={Intent.PRIMARY} className="secondary-info">
        <div className="contest-state-widget__left">{leftComponent}</div>
        <div className="contest-state-widget__right">{rightComponent}</div>
        <div className="clearfix" />
        {this.renderVirtualContestAlert()}
      </Callout>
    );
  }

  renderVirtualContestAlert = () => (
    <Alert
      isOpen={this.state.isVirtualContestAlertOpen || false}
      confirmButtonText="Yes, start my participation"
      onConfirm={this.startVirtualContest}
      cancelButtonText="Cancel"
      onCancel={this.cancelVirtualContest}
      intent={Intent.WARNING}
      icon="time"
    >
      Are you sure you want to start your participation in this contest?
    </Alert>
  );

  renderUpsolveButton = () => {
    const { problemSet } = this.state;
    if (!problemSet) {
      return null;
    }
    return (
      <>
        &nbsp;&nbsp;
        <ButtonLink small intent={Intent.WARNING} to={`/problems/${problemSet.slug}`}>
          Upsolve problems
        </ButtonLink>
      </>
    );
  };

  getWidgetComponents = () => {
    const state = this.props.state;

    if (state === ContestState.NotBegun) {
      return {
        leftComponent: <span>Contest hasn't started yet.</span>,
        rightComponent: !!this.state.remainingDuration && <span>Starts in {this.getRemainingDuration()}</span>,
      };
    }
    if (state === ContestState.Begun) {
      return {
        leftComponent: (
          <Button
            small
            intent={Intent.WARNING}
            onClick={this.alertVirtualContest}
            loading={this.state.isVirtualContestButtonLoading}
          >
            Click here to start your participation
          </Button>
        ),
        rightComponent: !!this.state.remainingDuration && <span>Ends in {this.getRemainingDuration()}</span>,
      };
    }
    if (state === ContestState.Started) {
      return {
        leftComponent: <span>Contest is running.</span>,
        rightComponent: !!this.state.remainingDuration && <span>Ends in {this.getRemainingDuration()}</span>,
      };
    }
    if (state === ContestState.Finished) {
      return {
        leftComponent: (
          <>
            <span>Contest is over.</span>
            {this.renderUpsolveButton()}
          </>
        ),
      };
    }
    if (state === ContestState.Paused) {
      return {
        leftComponent: <span>Contest is paused.</span>,
      };
    }
    return {};
  };

  getRemainingDuration = () => {
    return <FormattedDuration value={this.state.remainingDuration} />;
  };

  refreshRemainingDuration = () => {
    const {
      remainingDuration: prevRemainingDuration,
      baseRemainingDuration,
      baseTimeForRemainingDuration,
    } = this.state;
    const remainingDuration = Math.max(0, baseRemainingDuration + baseTimeForRemainingDuration - new Date().getTime());
    this.setState({ remainingDuration });

    if (remainingDuration === 0 && prevRemainingDuration !== 0) {
      this.props.onGetContestWebConfig(this.props.contest.jid);
    }

    this.currentTimeout = setTimeout(() => this.refreshRemainingDuration(), 500);
  };

  setUpBaseRemainingDuration = () => {
    this.setState({
      baseRemainingDuration: this.props.remainingStateDuration,
      baseTimeForRemainingDuration: new Date().getTime(),
    });
  };

  alertVirtualContest = () => {
    this.setState({ isVirtualContestAlertOpen: true });
  };

  cancelVirtualContest = () => {
    this.setState({ isVirtualContestAlertOpen: false, isVirtualContestButtonLoading: false });
  };

  startVirtualContest = async () => {
    this.setState({ isVirtualContestAlertOpen: false, isVirtualContestButtonLoading: true });
    await this.props.onStartVirtualContest(this.props.contest.jid);
    await this.props.onGetContestWebConfig(this.props.contest.jid);
    this.setState({ isVirtualContestButtonLoading: false });
  };

  searchProblemSet = async () => {
    if (this.props.state === ContestState.Finished) {
      if (this.state.problemSet === undefined) {
        const problemSet = await this.props.onSearchProblemSet(this.props.contest.jid);
        this.setState({ problemSet });
      }
    }
  };
}

const mapStateToProps = state => ({
  contest: selectContest(state),
  state: selectContestWebConfig(state).state,
  remainingStateDuration: selectContestWebConfig(state).remainingStateDuration,
});

const mapDispatchToProps = {
  onGetContestWebConfig: contestWebActions.getWebConfig,
  onStartVirtualContest: contestActions.startVirtualContest,
  onSearchProblemSet: contestActions.searchProblemSet,
};

export default connect(mapStateToProps, mapDispatchToProps)(ContestStateWidget);
