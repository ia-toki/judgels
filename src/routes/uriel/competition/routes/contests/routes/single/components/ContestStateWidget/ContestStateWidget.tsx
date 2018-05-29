import { Callout, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';

import { FormattedDuration } from '../../../../../../../../../components/FormattedDuration/FormattedDuration';
import { Contest } from '../../../../../../../../../modules/api/uriel/contest';
import { ContestState } from '../../../../../../../../../modules/api/uriel/contestWeb';
import { AppState } from '../../../../../../../../../modules/store';
import { selectContest } from '../../../../modules/contestSelectors';
import { selectContestWebConfig } from '../../../../modules/contestWebConfigSelectors';
import { contestWebConfigActions as injectedContestWebConfigActions } from '../../modules/contestWebConfigActions';

import './ContestStateWidget.css';

export interface ContestStateWidgetProps {
  contest: Contest;
  contestState: ContestState;
  remainingContestStateDuration?: number;
  onFetchContestWebConfig: (contestJid: string) => void;
}

interface ContestStateWidgetState {
  baseRemainingDuration?: number;
  baseTimeForRemainingDuration?: number;
  remainingDuration?: number;
}

// TODO(fushar): unit tests
class ContestStateWidget extends React.PureComponent<ContestStateWidgetProps, ContestStateWidgetState> {
  state: ContestStateWidgetState = {};

  private currentTimeout;

  static getDerivedStateFromProps(props: ContestStateWidgetProps): ContestStateWidgetState | null {
    const { remainingContestStateDuration } = props;
    if (!remainingContestStateDuration) {
      return null;
    }

    return {
      baseRemainingDuration: remainingContestStateDuration,
      baseTimeForRemainingDuration: new Date().getTime(),
    };
  }

  componentDidMount() {
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
      <Callout intent={Intent.PRIMARY}>
        <div className="contest-state-widget__left">{leftComponent}</div>
        <div className="contest-state-widget__right">{rightComponent}</div>
        <div className="clearfix" />
      </Callout>
    );
  }

  private getWidgetComponents = (): any => {
    const contestState = this.props.contestState;

    if (contestState === ContestState.NotBegun) {
      return {
        leftComponent: <span>Contest hasn't begun yet.</span>,
        rightComponent: <span>Begins in {this.getRemainingDuration()}</span>,
      };
    }
    if (contestState === ContestState.Running) {
      return {
        leftComponent: <span>Contest is running.</span>,
        rightComponent: <span>Ends in {this.getRemainingDuration()}</span>,
      };
    }
    if (contestState === ContestState.Ended) {
      return {
        leftComponent: <span>Contest is over.</span>,
      };
    }
    return null;
  };

  private getRemainingDuration = () => {
    return <FormattedDuration value={this.state.remainingDuration!} />;
  };

  private refreshRemainingDuration = () => {
    const { baseRemainingDuration, baseTimeForRemainingDuration } = this.state;
    const remainingDuration = Math.max(
      0,
      baseRemainingDuration! + baseTimeForRemainingDuration! - new Date().getTime()
    );
    this.setState({ remainingDuration });

    if (remainingDuration === 0) {
      this.props.onFetchContestWebConfig(this.props.contest.jid);
    }

    this.currentTimeout = setTimeout(() => this.refreshRemainingDuration(), 500);
  };
}

export function createContestStateWidget(contestWebConfigActions) {
  const mapStateToProps = (state: AppState) =>
    ({
      contest: selectContest(state)!,
      contestState: selectContestWebConfig(state)!.contestState,
      remainingContestStateDuration: selectContestWebConfig(state)!.remainingContestStateDuration,
    } as Partial<ContestStateWidgetProps>);

  const mapDispatchToProps = {
    onFetchContestWebConfig: contestWebConfigActions.fetch,
  };

  return connect(mapStateToProps, mapDispatchToProps)(ContestStateWidget);
}

export default createContestStateWidget(injectedContestWebConfigActions);
