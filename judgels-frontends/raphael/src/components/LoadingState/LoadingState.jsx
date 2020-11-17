import { ProgressBar } from '@blueprintjs/core';
import * as React from 'react';

import { SingleColumnLayout } from '../../components/SingleColumnLayout/SingleColumnLayout';

import './LoadingState.css';

export interface LoadingStateProps {
  large?: boolean;
}

export interface LoadingStateState {
  showProgressBar: boolean;
}

export class LoadingState extends React.PureComponent<LoadingStateProps, LoadingStateState> {
  timer: any;
  state: LoadingStateState = { showProgressBar: false };

  componentDidMount() {
    this.timer = setTimeout(() => {
      this.setState({ showProgressBar: true });
      this.timer = 0;
    }, 500);
  }

  componentWillUnmount() {
    if (this.timer) {
      clearTimeout(this.timer);
      this.timer = 0;
    }
  }

  render() {
    if (this.props.large) {
      return <SingleColumnLayout>{this.renderProgressBar()}</SingleColumnLayout>;
    }
    return this.renderProgressBar();
  }

  private renderProgressBar = () => {
    if (!this.state.showProgressBar) {
      return null;
    }
    return <ProgressBar className="loading-state" />;
  };
}
