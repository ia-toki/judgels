import * as React from 'react';

import { APP_CONFIG, Mode } from 'conf';

import './TimeanddateLink.css';

export interface TimeanddateLinkProps {
  time: number;
  message: string;
  children?: any;
}

export class TimeanddateLink extends React.PureComponent<TimeanddateLinkProps> {
  render() {
    if (APP_CONFIG.mode === Mode.PRIVATE_CONTESTS) {
      return this.props.children;
    }
    return (
      <span className="timeanddate" onClick={this.onClick}>
        {this.props.children}
      </span>
    );
  }

  private onClick = e => {
    e.preventDefault();

    const time = new Date(this.props.time)
      .toISOString()
      .replace(/[-:Z.]/g, '')
      .substring(0, 15);
    const message = encodeURIComponent(this.props.message);

    window.open(`https://www.timeanddate.com/worldclock/fixedtime.html?msg=${message}&iso=${time}`);
  };
}
