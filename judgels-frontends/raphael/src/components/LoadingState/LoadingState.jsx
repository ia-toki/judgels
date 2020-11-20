import { ProgressBar } from '@blueprintjs/core';
import * as React from 'react';

import { SingleColumnLayout } from '../SingleColumnLayout/SingleColumnLayout';

import './LoadingState.css';

export class LoadingState extends React.PureComponent {
  timer;

  state = {
    showProgressBar: false,
  };

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

  renderProgressBar = () => {
    if (!this.state.showProgressBar) {
      return null;
    }
    return <ProgressBar className="loading-state" />;
  };
}
