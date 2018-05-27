import { Callout, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';

import { FormattedDuration } from '../../../../../../../../../components/FormattedDuration/FormattedDuration';
import { ContestState } from '../../../../../../../../../modules/api/uriel/contestWeb';
import { AppState } from '../../../../../../../../../modules/store';
import { selectContestWebConfig } from '../../../../modules/contestWebConfigSelectors';

import './ContestStateWidget.css';

export interface ContestStateWidgetProps {
  contestState: ContestState;
  remainingContestStateDuration?: number;
}

interface ContestStateWidgetState {
  baseRemainingDuration?: number;
  baseTimeForRemainingDuration?: number;
  currentTime?: number;
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
    this.refreshCurrentTime();
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
        leftComponent: <span>Contest ended.</span>,
      };
    }
    return null;
  };

  private getRemainingDuration = () => {
    const { baseRemainingDuration, baseTimeForRemainingDuration, currentTime } = this.state;
    const remainingDuration = baseRemainingDuration! + baseTimeForRemainingDuration! - currentTime!;

    return <FormattedDuration value={Math.max(0, remainingDuration)} />;
  };

  private refreshCurrentTime = () => {
    this.setState({ currentTime: new Date().getTime() });
    this.currentTimeout = setTimeout(() => this.refreshCurrentTime(), 900);
  };
}

export function createContestStateWidget() {
  const mapStateToProps = (state: AppState) =>
    ({
      contestState: selectContestWebConfig(state)!.contestState,
      remainingContestStateDuration: selectContestWebConfig(state)!.remainingContestStateDuration,
    } as Partial<ContestStateWidgetProps>);

  return connect(mapStateToProps)(ContestStateWidget);
}

export default createContestStateWidget();
